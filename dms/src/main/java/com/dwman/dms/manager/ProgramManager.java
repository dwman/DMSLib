package com.dwman.dms.manager;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;


import com.dwman.dms.Constants;
import com.dwman.dms.bean.ProgramData;
import com.dwman.dms.bean.ProgramInfo;
import com.dwman.dms.delete.CProgramDelete;
import com.dwman.dms.download.CProgramImport;
import com.dwman.dms.parser.ProgramParse;
import com.dwman.dms.query.ProgramQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ProgramManager {
    private static final String TAG = "ProgramManager";


    private String mProgramSrc = Constants.USB;
    private int mError=0;
    String mCurProgramId;
    // 首先必须要先LOAD硬盘节目，才允许LOAD U盘上的节目。建立一个标志表示硬盘已经LOADed。
    boolean mIsLoadHdisk;
    private List<ProgramInfo> movieInfos;
    private List<ProgramInfo> publicAdInfos;
    private List<ProgramInfo> badAdInfos;
    private static ProgramManager programManager;
    private ProgramQuery programQuery;
    private CProgramImport programImport;
    private CProgramDelete cProgramDelete;
    private ProgramData mProgramData;
    
    private MediaPlayer mediaPlayer = new MediaPlayer();


    private ProgramManager(){
        movieInfos=new ArrayList<>();
        publicAdInfos =new ArrayList<>();
        badAdInfos =new ArrayList<>();
    }


        public static ProgramManager getInstance(){
            if (programManager == null){
                synchronized (ProgramManager.class){
                    if (programManager==null){
                        programManager= new ProgramManager();
                    }
                }
            }


            return programManager;

        }



    // 取得节目数量
    public  int getProgramsCount(String nSrc, int type){
        checkSrc(nSrc);
        int length = 0;
        switch (type){
            case ProgramInfo.TYPE_MOVIE:
                length = movieInfos.size();
                break;
            case ProgramInfo.TYPE_PUBLICAD:
                length = publicAdInfos.size();
                break;
            case ProgramInfo.TYPE_SUBAD:
                length = badAdInfos.size();
                break;

            default:
                Log.d(TAG,"类型尚未支持查询 ");
                break;
        }

        return length;
    }

    // 取影片列表
    public void getProgramList(String nSrc, int type, List<ProgramInfo> programList) {
        checkSrc(nSrc);
        if (programList != null) {
            programList.clear();
        }

        switch (type){
            case ProgramInfo.TYPE_MOVIE:
                if (movieInfos.size()==0){
                    return ;
                }
                if (programList != null) {
                    programList.addAll(movieInfos);
                }


                break;
            case ProgramInfo.TYPE_PUBLICAD:
                if (publicAdInfos.size()==0){
                    return ;
                }
                if (programList != null) {
                    programList.addAll(publicAdInfos);
//                    Log.e(TAG,"publicAdInfos "+publicAdInfos.size());
                }
                break;
            case ProgramInfo.TYPE_BUSINESSAD:
                if (badAdInfos.size()==0){
                    return ;
                }
                if (programList != null) {
                    programList.addAll(badAdInfos);
                }
                break;
            case ProgramInfo.TYPE_MUSIC:
                // TODO: 2017/11/23 背景音乐
              /*  if (musicList.size()==0){
                    return -1;
                }
                if (programList != null) {
                    programList.addAll(badAdInfos);
                }*/
                break;

            default:
                Log.d(TAG,"类型尚未支持查询 ");
                break;
        }
    }


    private void checkSrc(String src) {
        if (TextUtils.equals(src, Constants.USB)){

        }else if (TextUtils.equals(src, Constants.HDISK)){
//            Log.e(TAG,Constants.HDISK+"cannot reach now!");

        }else {
//            Log.d(TAG,"src type exception");

            throw new RuntimeException("src type exception");
        }
    }


    // 查询节目是否已经存在
    public boolean findProgramOnHd(int nType,  ProgramInfo info) {

        ProgramQuery programQuery = new ProgramQuery();
        return programQuery.findProgramOnHd(nType,info);
    }

    //


    private void dataClear() {
        if (mProgramData != null) {
            mProgramData.clearData();
        }


        movieInfos.clear();
        publicAdInfos.clear();
        badAdInfos.clear();
    }
    public boolean isInfoEmpty(){

        if (mProgramData == null) {
            return true;
        }


        return mProgramData.empty();
    }
    public void loadInfo(String src){

        if ((!TextUtils.equals(src, Constants.USB))&&(!TextUtils.equals(src, Constants.HDISK))){
            Log.d(TAG,"SRC onError");
            return;
        }
        dataClear();


        if (programQuery == null) {
            programQuery = new ProgramQuery();
        }
        ProgramParse parse = new ProgramParse();
        mProgramData = programQuery.queryAllProgram(src);

        getInfoDataList(mProgramData,parse);




        // TODO: 2017/11/2 解析音视频编码

        mProgramSrc = src;


    }

    private void getInfoDataList(ProgramData mProgramData, ProgramParse parse) {
        getInfoDataList(parse,mProgramData.mMovieList,movieInfos,ProgramInfo.TYPE_MOVIE);
        getInfoDataList(parse,mProgramData.mBusinessAdList,badAdInfos,ProgramInfo.TYPE_BUSINESSAD);
        getInfoDataList(parse,mProgramData.mPublicAdList,publicAdInfos,ProgramInfo.TYPE_PUBLICAD);



    }

    private void getInfoDataList(ProgramParse parse, List<String> dataList, List<ProgramInfo> dataInfos, int programType) {
        for (String s : dataList) {
            ProgramInfo info = parse.parseProgramInfo(s, programType);
            if (info != null) {
                dataInfos.add(info);
            }



        }
    }

    //查询广告时长--------------

    /**
     * 查询影片时长（特指非标准格式广告）
     *
     * @param info program信息
     * @return 影片的时长单位为秒
     */
    public int getADDuration(ProgramInfo info){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        if (info.mProgramDuration>0){
            return info.mProgramDuration;

        }
        //广告时长获取
        mediaPlayer.stop();
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(info.mPath+info.mVideoFile);

            mediaPlayer.prepare();
            info.mProgramDuration = mediaPlayer.getDuration()/1000;

            return  info.mProgramDuration;
        } catch (IOException e) {
            e.printStackTrace();
        }



        return 0;
    }
    // 查询广告总时长

    /**
     *本地公益广告总时长获取
     * @return
     */
    public int getPADTotalDuration(){
        int pADTotalDuration =0;

        if (mProgramSrc .equals(Constants.HDISK)){
            if (publicAdInfos.size()>0) {
                for (ProgramInfo publicAdInfo : publicAdInfos) {

                    pADTotalDuration+=  getADDuration(publicAdInfo);
                }
                return pADTotalDuration;

            }else if (mProgramSrc.equals(Constants.USB)){
                List<ProgramInfo> adInfos = new ArrayList<>();

                if (programQuery == null) {
                     programQuery = new ProgramQuery();
                }

                ProgramParse parse= new ProgramParse();
                List<String> paths = programQuery.queryProgram(Constants.HDISK, ProgramInfo.TYPE_PUBLICAD);

                 getInfoDataList(parse,paths,adInfos,ProgramInfo.TYPE_PUBLICAD);
                for (ProgramInfo publicAdInfo : adInfos) {

                    pADTotalDuration+=  getADDuration(publicAdInfo);
                }
                return pADTotalDuration;

            }
        }


        return 0;
    }

    /**
     * 获取所传列表广告总时长
     * @param programInfos
     * @return
     */

    public int getBADDuration(List<ProgramInfo> programInfos){

        int totalDuration =0;

        if (programInfos != null && programInfos.size()>0) {

            for (ProgramInfo programInfo : programInfos) {
                totalDuration+= getADDuration(programInfo);
            }


        }




        return totalDuration;

    }





    //Src 选择-----------------

    public void switchSr(String programSrc){
        loadInfo(programSrc);
    }
    public String getSrc() {
        return mProgramSrc;
    }




    //影片导入监听-----------------
    public void setOnPrepareListener(CProgramImport.OnPrepareListener onPrepareListener){
        if (programImport == null) {
            programImport = new CProgramImport();

        }

        programImport.setOnPrepareListener(onPrepareListener);
    }
    public void setOnImportErrorListener(CProgramImport.OnImportErrorListener onImportErrorListener){
        if (programImport == null) {
            programImport = new CProgramImport();

        }

        programImport.setOnImportErrorListener(onImportErrorListener);
    }
    public void setOnImportProgressListener(CProgramImport.OnImportProgressListener onImportProgressListener){
        if (programImport == null) {
            programImport = new CProgramImport();

        }

        programImport.setOnImportProgressListener(onImportProgressListener);
    }
    public void setOnCompletionListener(CProgramImport.OnCompletionListener onCompletionListener){
        if (programImport == null) {
            programImport = new CProgramImport();

        }

        programImport.setOnCompletionListener(onCompletionListener);
    }
















    public void importProgram(Context context, int taProgramType, List<ProgramInfo> playInfos) {
        if (programImport == null) {
            programImport = new CProgramImport();

        }
        programImport.importProgram(context,taProgramType,playInfos);
    }
    public void stopImport(){
        if (programImport != null) {
            programImport.stop();
        }
    }

    //影片删除-----------
    public void deleteProgram(int taProgramType,List<ProgramInfo> infos){
        if (cProgramDelete == null) {
            cProgramDelete = new CProgramDelete();
        }

        cProgramDelete.delete(taProgramType,infos);
    }
    public void deleteProgram(ProgramInfo info){
        if (cProgramDelete == null) {
            cProgramDelete = new CProgramDelete();
        }

        cProgramDelete.delete(info);
    }

    public void onDestroy(){
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer=null;
        }
        stopImport();
    }
}
