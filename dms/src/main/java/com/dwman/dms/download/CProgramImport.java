package com.dwman.dms.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;


import com.dwman.dms.bean.ProgramInfo;
import com.dwman.dms.manager.ProgramManager;
import com.dwman.dms.query.ProgramQuery;
import com.dwman.dms.util.LogUtil;
import com.dwman.dms.util.SPUtils;
import com.dwman.dms.util.StorageUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class CProgramImport  {
    private static final String TAG = "CProgramImport";
    private static final int IMPORT_STATUS_READY = 0;
    private static final int IMPORT_STATUS_WROKING = 1;
    private static final int IMPORT_STATUS_STOP = 2;
    private static final int IMPORT_STATUS_END = 3;
    private static final int IMPORT_STATUS_ERROR = -1;


    private static final int EVENT_IMPORT_COMPLETION = 1;
    private static final int EVENT_IMPORT_NOTIFY = 2;



    private static final int OUT_OF_MEMORY_ERROR = -1;
    private static final int FILE_IMPORT_BREAK_BEFORE_ERROR = -2;
    private static final int FILE_EXIST_ERROR = -3;
    private static final int FILE_NOT_FOUND_ERROR = -4;
    private static final int UNKNOWN_PROGRAM_TYPE_ERROR =-5 ;
    private static final int FILE_MAKE_ERROR = -6;




    private  boolean downloadStop;
    private String mRoot;
    private String mBaseRoot = "/storage/sda1/DMS_HDD/";
    private String mDestDeletePath;
    private String mDestPath;
    private int mStatus;
    private long  mnProgramSize;  // 节目大小
    private int mProgramType;  //	ta_type_movie = 0,
    private  int mnIsEnd;//线程结束

    private int   mnSaveTime;
    private String msProgramId;

    private long mnStartTime;
    private List<String> temps = new ArrayList<>();
    private static List<Integer> importFaileds = new ArrayList<>();
    private  EventHandler handler;
    private static  long programSize;
    private static long importProgramSize;
    private OnPrepareListener onPrepareListener;
    private OnImportProgressListener onImportProgressListener;
    private OnCompletionListener onCompletionListener;
    private OnImportErrorListener onImportErrorListener;

    public CProgramImport() {

        handler = new EventHandler(Looper.myLooper());
    }


    public void importProgram(Context context, int type, List<ProgramInfo> infos) {
        clear();
        mStatus = IMPORT_STATUS_READY;
        //检查temp文件-----------------
        setProgramType(type);

       List<String> temp=  checkPreImport();


        if (temp.size()>0) {
        /*    if (mObserver != null) {
                mObserver.onTempFileExist(temp);
            }*/
            if (onImportErrorListener!=null){
                onImportErrorListener.OnImportError(FILE_IMPORT_BREAK_BEFORE_ERROR);


            }


            mStatus = IMPORT_STATUS_ERROR;
            return ;
        }


        //检查磁盘容量-----------------
        long[] usbExtention = StorageUtil.getUsbExtention(context);
        //检查要导入节目的大小
        mnProgramSize= getImportSize(infos);

            LogUtil.i(TAG,"usbExtention[1] :"+usbExtention[1]+"---mnProgramSize :"+mnProgramSize);
        if (usbExtention[1] - mnProgramSize<0) {
            if (onImportErrorListener!=null){
                onImportErrorListener.OnImportError(OUT_OF_MEMORY_ERROR);

            }
            mStatus = IMPORT_STATUS_ERROR;
            return ;
        }
        //查询文件是否存在-----------
        ProgramQuery query = new ProgramQuery();
       List<ProgramInfo> programInfos = new ArrayList<>();
        for (ProgramInfo info : infos) {
            if (query.findProgramOnHd(type, info)) {
                programInfos.add(info);
            }
        }

        if (programInfos.size()>0){
            if (onImportErrorListener!=null){
                onImportErrorListener.OnImportError(FILE_EXIST_ERROR);


            }
            return;

        }



        //下载---------------
        String secDir = "";
        switch (type){
            case ProgramInfo.TYPE_MOVIE://电影
                secDir="MovieList";


                break;
            case ProgramInfo.TYPE_PUBLICAD://公益广告
                secDir="PadList";

                break;
            case ProgramInfo.TYPE_BUSINESSAD://商业广告
                secDir="BadList";
                break;
            case ProgramInfo.TYPE_MUSIC://背景音乐
                secDir="MusicList";

                break;
            default:
                if (onImportErrorListener != null) {
                    onImportErrorListener.OnImportError(UNKNOWN_PROGRAM_TYPE_ERROR);
                }

                return ;
        }
        if (!mkFile(secDir)) {
            if (onImportErrorListener != null) {
                onImportErrorListener.OnImportError(FILE_MAKE_ERROR);
            }


            return ;
        }


        if (onPrepareListener != null) {
            onPrepareListener.onPrePared();
        }
        programCopy(secDir, infos);





    }


    private void programCopy(final String dir, final List<ProgramInfo> infos) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                ProgramInfo info;
                String uid;
                String pathTarget;
                String pathSource;
                String fileSource;
                String fileBaseName;
                String fileTarget;
                for (int i = 0; i < infos.size(); i++) {
                    info = infos.get(i);
                    if (TextUtils.isEmpty(info.mProgramId)){
                        uid = info.mProgramName;

                    }else {
                        uid = info.mProgramId;
                    }
                    pathTarget = mBaseRoot+dir+"/"+uid;
                    pathSource = info.mPath;

                    programSize = getFileSize(pathSource);
                    File file = new File(pathTarget);
                    if (!file.exists()) {
                        if (!file.mkdir()) {
                            // TODO: 2017/12/15 文件创建失败
                            if (!importFaileds.contains(i)) {
                                importFaileds.add(i);
                            }
                            continue;
                        }
                    }


                    boolean dmsProgram = isDmsProgram(pathSource);
                    LogUtil.i(TAG ,"dmsProgram "+dmsProgram);
                    File file1 = new File(pathSource);
                    fileBaseName = info.mVideoFile;
                    LogUtil.i(TAG,"fileBaseName :"+fileBaseName);


                    File[] files = file1.listFiles();
                    importProgramSize = 0;

                    for (File file2 : files) {
                        fileSource = pathSource+file2.getName();


                        if (dmsProgram){

                            if (TextUtils.equals(fileBaseName,file2.getName())){
                                fileTarget = pathTarget+"/"+uid;
                            }else if (TextUtils.equals("id",file2.getName())){
                                fileTarget = pathTarget+"/id";
                            }else if (TextUtils.equals(fileBaseName+".ac3",file2.getName())){
                                fileTarget = pathTarget+"/"+uid+".ac3";
                            }else if (TextUtils.equals(fileBaseName+".info",file2.getName())){
                                fileTarget = pathTarget+"/"+uid+".info";
                            }else if (TextUtils.equals(fileBaseName+"xkz",file2.getName())){
                                fileTarget = pathTarget+"/"+uid+"xkz";
                            }else if (TextUtils.equals(fileBaseName+"xkz.ac3",file2.getName())){
                                fileTarget = pathTarget+"/"+uid+"xkz.ac3";
                            }else {
                                throw new RuntimeException("File not found !");
                            }

                        }else {
                            fileTarget=pathTarget+"/"+file2.getName();
                        }

                        fileCopy(i+1,fileSource,fileTarget);


                        if (downloadStop){

                            SystemClock.sleep(500);
                            ProgramManager.getInstance().deleteProgram(infos.get(i));


                            return;
                        }
                    }

                }
                LogUtil.i(TAG,"sum :"+(sum +(infos.size())*4096)+"---- mnProgramSize"+mnProgramSize);
                SystemClock.sleep(200);

                Message msg = Message.obtain();
                msg.what = EVENT_IMPORT_COMPLETION;
                handler.sendMessage(msg);


            }
        }).start();

    }

    private boolean mkFile(String pathTarget) {

        File file = new File("/storage/sda1/DMS_HDD");
        if (!file.exists()){
            if (file.mkdir()) {
                return false;
            }
        }

        file = new File(mBaseRoot+pathTarget);
        if (!file.exists()){
            if (file.mkdir()) {
                return false;
            }
        }
        return true;
    }

    private  boolean isDmsProgram(String programPath) {
        File file = new File(programPath);
        if (!file.exists()) {
            return false;
        }
        File[] childFiles = file.listFiles();

        if (childFiles.length<6){
            return false;
        }
        for (File childFile : childFiles) {
            if (childFile.getName().equals("id")){
                return true;
            }
        }


        return false;
    }
        public void destroy(){
            if (handler != null) {
                handler.removeMessages(EVENT_IMPORT_NOTIFY);
                handler.removeMessages(EVENT_IMPORT_COMPLETION);
            }
        }

    private long sum =0;
    /**
     * 普通缓冲复制
     * @param source 源文件
     * @param target 目标文件
     */
    public  void fileCopy(final int index, String source, String target) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {

            in = new BufferedInputStream(new FileInputStream(source));
            out = new BufferedOutputStream(new FileOutputStream(target));
            byte[] buf = new byte[4096];
            int i;
            long start = System.currentTimeMillis();
            while ((i = in.read(buf)) != -1 && !downloadStop) {


                    out.write(buf, 0, i);
                    out.flush();
                    sum+=i;
                    importProgramSize+= i;



                long temp = System.currentTimeMillis();
                int progress = (int) ((sum + index*4096)*100/mnProgramSize);
                if (temp -start >500 || progress==100){
                    start = temp;
                    Message msg = Message.obtain();
                    msg.what = EVENT_IMPORT_NOTIFY;
                    msg.arg1 = index;
                    msg.arg2= progress;
                    handler.sendMessage(msg);
                }
            }

        } catch (IOException e) {
            /*f (mObserver != null) {
                mObserver.onImportFailedError(index);
            }*/
            if (!importFaileds.contains(index-1)) {
                importFaileds.add(index-1);
            }



            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void clear() {

        downloadStop =false;
        mnProgramSize = 0;
        mDestPath = "";
        importFaileds.clear();
    }

    private List<String> checkPreImport() {
        temps.clear();
        Map<String, ?> map = SPUtils.getAll();
        Set<String> keySet = map.keySet();
        for (String s : keySet) {
            if (s.length()>3) {
                String name = s.substring(0, 4);
                if (TextUtils.equals(name,"temp")) {
                    String temp = (String) SPUtils.get(s, "");
                    temps.add(temp);
                    SPUtils.remove(s);
                }
            }


        }
        return temps;

    }


    public int getImportStatus() {
        return mStatus;
    }

    public void stop() {
        downloadStop =true;
        destroy();

    }

    public long getProgramSize() {
        return mnProgramSize;
    }

    //获取文件大小---------------------

    public long getImportSize(List<ProgramInfo> infos) {
        long totalFileLength=0;

        for (ProgramInfo info : infos) {
            String path = info.mPath;
            LogUtil.i(TAG,"导入节目路径 :"+path);


            long fileSize = getFileSize(path);
            totalFileLength+=fileSize;

        }


        return totalFileLength;
    }
    private long getFileSize(String sFileName) {
        long fileLengthTemp =0;
        File file = new File(sFileName);
        File[] files = file.listFiles();
        if (files!=null&& files.length>0){
            for (File file1 : files) {
                long length = file1.length();
                fileLengthTemp+=length;
            }
        }

        return fileLengthTemp+file.length();
    }


    //设置影片类型----------------
    public void setProgramType(int ProgramType) {
        mProgramType = ProgramType;

        if (mProgramType == ProgramInfo.TYPE_MOVIE){
//            mRoot = "/storage/sda1/DMS_HDD/movie";
            mRoot = mBaseRoot+"MovieList";
        }
        else if(mProgramType == ProgramInfo.TYPE_PUBLICAD){
            //            mRoot = "/storage/sda1/DMS_HDD/publicad";
            mRoot = mBaseRoot+"PadList";
        }

        else if(mProgramType == ProgramInfo.TYPE_BUSINESSAD){
            //            mRoot = "/storage/sda1/DMS_HDD/businessad" ;
            mRoot = mBaseRoot+"BadList";
        }

        else if (mProgramType == ProgramInfo.TYPE_MUSIC){
            //            mRoot = "/storage/sda1/DMS_HDD/music";
            mRoot = mBaseRoot+"MusicList";
        }

    }

    public  boolean isBreakPointImport(String id, int nSize, int nType, int nStartTime) {
        return false;
    }

    public  boolean DelBreakPointImport() {
        return false;
    }

    public void DelBreakPointImportFile() {

    }

    public String getDestPath(String dest) {
        mDestPath = dest;

        return dest;
    }

    public void setOnPrepareListener(OnPrepareListener onPrepareListener){
        this.onPrepareListener = onPrepareListener;
    }
    public void setOnImportErrorListener(OnImportErrorListener onImportErrorListener){
        this.onImportErrorListener = onImportErrorListener;
    }
    public void setOnImportProgressListener(OnImportProgressListener onImportProgressListener){
        this.onImportProgressListener = onImportProgressListener;
    }
    public void setOnCompletionListener(OnCompletionListener onCompletionListener){
        this.onCompletionListener = onCompletionListener;
    }

    public interface OnPrepareListener{
        /**
         * 下载开始
         */
        void onPrePared();
    }
    public interface OnCompletionListener{
        /**
         * 下载完成
         * @param importFailed 下载中失败的program index从0开始
         */
        void onImportCompletion(List<Integer> importFailed);
    }
    public interface OnImportErrorListener{
        /**
         * 下载失败
         * @param errorCode
         */
        void OnImportError(int errorCode );

    }
    public interface OnImportProgressListener{
        /**
         * 更新下载进度
         * @param singleProgramProgress
         * @param programCount 正在下载第几个文件
         * @param progress   进度0-100；
         */
        void notify(int singleProgramProgress, int programCount, int progress);

    }



    private  class EventHandler extends Handler {


        public EventHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {

            switch (msg.what){
                case EVENT_IMPORT_COMPLETION:
                    if (onCompletionListener != null) {
                        onCompletionListener.onImportCompletion(importFaileds);
                    }
                    break;
                case EVENT_IMPORT_NOTIFY:
                    int singleProgramProgress;
                    if (onImportProgressListener != null) {
                        if (programSize!=0) {
                             singleProgramProgress = (int) (importProgramSize * 100 / programSize);
                        }else {
                            singleProgramProgress= 0;
                        }

                        onImportProgressListener.notify(singleProgramProgress,msg.arg1,msg.arg2);
                    }
                    break;
            }

        }

    }


}
