package com.dwman.dms.query;

import android.text.TextUtils;
import android.util.Log;

import com.dwman.dms.Constants;
import com.dwman.dms.bean.ProgramData;
import com.dwman.dms.bean.ProgramInfo;
import com.dwman.dms.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CProgramQuery implements IProgramQuery {
    private static final String TAG = "ProgramQuery";
    private String mRootSrc ;


    private String mRootMovie;
    private String mRootPublicAd;
    private String mRootBusinessAd;
    private String mRootMusic;
    private String mRootSubAd;
    private ProgramData mProgramData;

    private  String mDiskRoot = Constants.DISK_ROOT;
    private  String mUsbRoot = Constants.USB_ROOT;

    private boolean open(String src) {

        if (TextUtils.isEmpty(src)) {

            LogUtil.e(TAG,"存储位置为空");
            return false;
        }
        if (src.equals(Constants.USB)){

            //判断节目的路径是否存在
            mRootSrc = Constants.USB_ROOT;

        }else if (src.equals(Constants.HDISK)){
            mRootSrc = Constants.DISK_ROOT;
        }else {
            throw new RuntimeException("unknown storage location,please check storage location!!! ");
        }

        if (!exists(mRootSrc)) {
            LogUtil.e(TAG,mRootSrc +"not exist!");

            return false;
        }

        mRootMovie = mRootSrc+"MovieList/";
        if (!exists(mRootMovie)){
            LogUtil.i(TAG,mRootMovie+"not exist ");
        }
        mRootPublicAd = mRootSrc+"PadList/";
        if (!exists(mRootPublicAd)){
            LogUtil.i(TAG, mRootPublicAd +"not exist ");
        }

        mRootBusinessAd = mRootSrc+"BadList/";
        if (!exists(mRootBusinessAd)){
            LogUtil.i(TAG, mRootBusinessAd +"not exist ");
        }

        mRootMusic = mRootSrc+"MusicList/";
        if (!exists(mRootMusic)){
            LogUtil.i(TAG,mRootMusic+"not exist ");
        }

        mRootSubAd = mRootSrc+"SadList/";
        if (!exists(mRootSubAd)){
            LogUtil.i(TAG, mRootSubAd +"not exist ");
        }

        return true;
    }

    private boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    @Override
    public boolean findProgramOnHd(int type, ProgramInfo info) {
        String path ;
        switch (type){
            case ProgramInfo.TYPE_MOVIE:
                path = mDiskRoot +"MovieList";
                break;
            case ProgramInfo.TYPE_BUSINESSAD:
                path = mDiskRoot +"BadList";
                break;
            case ProgramInfo.TYPE_PUBLICAD:
                path = mDiskRoot +"PadList";
                break;
            case ProgramInfo.TYPE_MUSIC:
                path = mDiskRoot +"MusicList";
                break;
            default:
                return false;
        }

        String uid ;
        if (TextUtils.isEmpty( info.mProgramId)) {
            uid = info.mProgramName;
        }else {
            uid = info.mProgramId;
        }
        File file = new File(path);
        if (file.exists()){
            File[] files = file.listFiles();

            for (File file1 : files) {
                if (file1.getName().equals(uid)) {
                    return true;
                }
            }
        }




        return false;
    }

    @Override
    public ProgramData queryAllProgram(String src) {

        if (!open(src)) {


            return new ProgramData();
        }
        if (mProgramData == null) {
            mProgramData = new ProgramData();
        }else {
            mProgramData.clearData();
        }
        //  查询电影节目目录----------------
        getDataList(mRootMovie, mProgramData.mMovieList);

        // 查询公益广告节目目录------------
        getDataList(mRootPublicAd, mProgramData.mPublicAdList);


        // 查询商业广告节目目录-----------
        getDataList(mRootBusinessAd, mProgramData.mBusinessAdList);

//        Log.e(TAG,"businessadList :"+businessadList.size());
        //  查询背景音乐目录

        getNeedCheckDataList(mRootMusic, mProgramData.mMusicList);

        //   查询文字广告目录
        getNeedCheckDataList(mRootSubAd, mProgramData.mSubAdList);




        return mProgramData;
    }

    private void getNeedCheckDataList(String mRootPath, List<String> containerList) {
        if(TextUtils.isEmpty(mRootPath)){
            Log.e(TAG,"mRootPath not initialize...");

            return;
        }
        File file = new File(mRootPath);
        if (file.exists()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    String absolutePath = file1.getAbsolutePath();
                    if (mRootPath.equals(mRootMusic)) {
                        if (isSupportMusicFile(absolutePath)) {
                            containerList.add(absolutePath);
                        }
                    } else {
                        if (isSupportSubAdFile(absolutePath)) {
                            containerList.add(absolutePath);
                        }
                    }
                }
            }

        }else {
            Log.d(TAG,mRootPath+"File Not Exist");
        }
    }

    private void getDataList(String mRootPath, List<String> containerList) {

        if(TextUtils.isEmpty(mRootPath)){
            Log.e(TAG,"mRootPath not initialize...");
            return;
        }
        File file = new File(mRootPath);
        if (file.exists()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    containerList.add(file1.getAbsolutePath());
                }
            }

        }else {
            Log.d(TAG,mRootPath+"File Not Exist");
        }
    }

    @Override
    public List<String> queryProgram(String src, int programType) {


        List<String> paths = new ArrayList<>();
        String path ;
        if (src.equals(Constants.USB)){
            path = mUsbRoot+"DMS_HDD/";
        }else if (src.equals(Constants.HDISK)){
            path = mUsbRoot;
        }else {
            throw new RuntimeException("unknown src path!!!");
        }
        switch (programType){
            case ProgramInfo.TYPE_MOVIE:
                path = path+"MovieList/";
                break;
            case ProgramInfo.TYPE_BUSINESSAD:
                path = path+"BadList/";
                break;
            case ProgramInfo.TYPE_PUBLICAD:
                path = path+"PadList/";
                break;
            case ProgramInfo.TYPE_MUSIC:
                path = path+"MusicList/";
                break;
        }

        getDataList(path,paths);


        return paths;

    }


    // 判断是否为支持的音乐格式文件
    public  boolean isSupportMusicFile(String sFilename){

        if (sFilename.length() >= 4)
        {
            String sTypeStr = sFilename.substring(sFilename.length() - 4, sFilename.length());
            String s = sTypeStr.toUpperCase();
            return (TextUtils.equals(s, ".MP3") || TextUtils.equals(sTypeStr, ".WAV"));
        }
        return false;
    }
    //   -- Check whether is the supported sub ad file.
    public boolean isSupportSubAdFile(String sFilename){


        if (sFilename.length() >= 4)
        {
            String sTypeStr = sFilename.substring(sFilename.length() - 4, sFilename.length());
//            boost::to_upper(sTypeStr);
            String s = sTypeStr.toUpperCase();
            return (TextUtils.equals(s, ".SAD"));
        }


        return false;
    }
}
