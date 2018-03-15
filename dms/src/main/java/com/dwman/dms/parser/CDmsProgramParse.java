package com.dwman.dms.parser;


import android.text.TextUtils;

import com.dwman.dms.bean.ProgramInfo;
import com.dwman.dms.util.ByteUtil;
import com.dwman.dms.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Created by ldw on 2017/10/31.
 */

public class CDmsProgramParse implements IProgramParse{
    private static final String TAG = "CDmsProgramParse";
    private String mProgramPath;
    private boolean isSpecialAd;
    private String mProgramId;
    private String mProgramName;
    private int mDuration;
    private String mVideoFormat;
    private String mAudioFormat;
    private String mDmsAVFormat;
    private String mDefaultAudioName="Chinese";
    private boolean isIntegrity;

    private boolean initProgramPath(String programPath, int programType) {
        // 取得并格式化文件目录名
        if(TextUtils.isEmpty(programPath))
        {
           LogUtil.i(TAG,"DmsParse: Invalid program path ");
            return false;
        }

        if(!programPath.endsWith("/") && !programPath.endsWith("\\")){
            File file = new File(programPath);
            if (!file.exists()) {
                return false;
            }



            mProgramPath = programPath + "/";
        }else{
            mProgramPath = programPath;
        }


        // 解析id文件
         parseDmsIdInfo(programType);
        return true;
    }



    private void parseDmsIdInfo(int programType) {
        String content = ""; //文件内容字符串
        int i;//影片名称字节长度
//            List idList = new ArrayList();//解析ID后数据的集合
        byte[] idBytes = new byte[0];//Id文件的字符数组
        byte[] number = new byte[8];//制作流水号
        byte[] discs = new byte[1];//影片打包总数
        byte[] cd = new byte[1];//影片当前包数
        byte[] length = new byte[1];//影片名称长度
        byte[] videoLength = new byte[8];//影片视频长度
        byte[] audioLength = new byte[8];//影片音频长度
        byte[] Film_time = new byte[4];//影片时间长度


        isSpecialAd = false;
        isIntegrity = true;

        //打开文件
        File file = new File(mProgramPath+"id");

        //判断id文件是否存在

             if (file.exists() && !file.isDirectory()){
            File parentFile = file.getParentFile();

                 //存在时检查完整性

                 isIntegrity = checkIntegrity(parentFile);

                    if (!isIntegrity){

                        //文件不完整时，不解析id文件

                        return;
                    }

            }else {


                 if (programType==ProgramInfo.TYPE_MOVIE){
                     isIntegrity = false;

                 }else {
                     isSpecialAd = true;
                 }

                 //没有id文件时不解析
                 return;

             }





            try {
                InputStream instream = new FileInputStream(file);
                InputStreamReader inputreader = new InputStreamReader(instream, "GBK");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line + "\n";
                }
                instream.close();
            } catch (java.io.FileNotFoundException e) {
               LogUtil.d("TestFile", "文件未找到");
            } catch (IOException e) {
               LogUtil.d("TestFile", e.getMessage());
            }

        try {
            idBytes = content.getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (idBytes.length<42){
           LogUtil.i(TAG,"文件格式错误");
            return  ;

        }
        //制作流水号
        System.arraycopy(idBytes, 0, number, 0, 8);
       LogUtil.d("制作流水号", ByteUtil.bytesToHexString(number));

        //影片当前包数
        System.arraycopy(idBytes, 8, discs, 0, 1);
//           LogUtil.d("影片当前包数", bytesToHexString(discs));
//
        //影片打包总数
        System.arraycopy(idBytes, 9, cd, 0, 1);
       LogUtil.d("影片打包总数", ByteUtil.bytesToHexString(cd));
        mProgramId = ByteUtil.bytesToHexString(number);
       LogUtil.d(TAG,"mProgramId :"+ mProgramId);
        //影片名称长度
        System.arraycopy(idBytes, 10, length, 0, 1);
        i = length[0];
       LogUtil.d("影片长度", i + "");


        byte[] name = new byte[i];//电影名称
        System.arraycopy(idBytes, 11, name, 0, i);
        try {
            mProgramName = new String(name, "GBK");
           LogUtil.d(TAG,"programName"+ mProgramName);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        System.arraycopy(idBytes, 11+i, videoLength, 0, 8);
        String fm = ByteUtil.bytesToHexString(videoLength);
        BigInteger bigInteger = new BigInteger(fm,16);
       LogUtil.d("影片长度", fm + "      " +bigInteger);


        //影片时长
        System.arraycopy(idBytes, 28 + i, Film_time, 0, 4);
//            mDuration =  ByteUtil.byteArrayToInt(Film_time);
        mDuration =  ByteUtil.byteArrayToInt1(Film_time);



       LogUtil.d(TAG,"mDuration :"+ mDuration);

        mVideoFormat = "MPEG4";		// 这个类型需要写进配置文件，不能修改
        mAudioFormat = "AC3";





    }
    @Override
    public boolean checkIntegrity(File file) {
        if (!file.exists()) {
            return  false;
        }

        File[] files = file.listFiles();
        if (files.length<6){
            return false;
        }
        for (File fileChild : files) {

            if (!checkFile(fileChild)) {
                return false;

            }
        }
        return true;

    }

    private boolean checkFile(File fileChild) {
        boolean result = false;
        String name = fileChild.getName();
        if (name.endsWith(".info")){
            result =true;
        }
        if (name.endsWith("xkz")){
            result = true;
        }
        if (name.endsWith(".ac3") && !name.endsWith("xkz.ac3")){
            result = true;
        }
        if (name.endsWith("xkz.ac3")){
            result = true;
        }
        if (name.equals(fileChild.getParentFile().getName())){
            result =true;
        }


        return result;
    }


    @Override
    public ProgramInfo parseProgramInfo(String programPath,int programType) {
        if (!initProgramPath(programPath,programType)) {
            return null;
        }

        if (!isIntegrity){
            //当节目不完整时不显示
            return null;
        }



        ProgramInfo info = new ProgramInfo();
        String programFileName, tempPath;
        tempPath = mProgramPath.substring(0,  mProgramPath.length() - 1);
        int npos = tempPath.lastIndexOf('/');

        npos++;
        programFileName = tempPath.substring(npos, tempPath.length());

        LogUtil.d(TAG,"programFileName================%s"+programFileName);
        File file = new File(mProgramPath);
        info.mPath = mProgramPath;
        info.mProgramType = programType;

        if (!file.canRead() && !file.canWrite()){
            LogUtil.i(TAG,file.getName()+"cannot read or write");

            return null;
        }


        File[] files = file.listFiles();
        if (files==null || files.length==0)
        {

            LogUtil.i(TAG,"DMS Parser: open directroy" +mProgramPath+"FAILED");
            return null;
        }
        if (isSpecialAd){
            //特殊格式的广告解析--------------
            for (File ad : files) {
                info.mPprogramSize =ad.length();
                String name = ad.getName();
                int i = name.indexOf(".");
                String type = (name.substring(i+1)).toUpperCase();
                info.mVideoFile = name;
                info.mAudioFile ="";
                info.mType = type;
                info.mProgramName = programFileName;
                info.mCreator = "数字节目管理中心";
//                Log.i(TAG,"影片名称 ："+info.programName);

/*                //  获取 广告时长暂时取消
                try {

                    player.reset();
                    player.setDataSource(info.path+info.videoFile);
                    player.prepare();
                    info.programDuration =player.getDuration()/1000;
                    Log.i(TAG,"programDuration :"+info.programDuration);
//                    player.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/


            }


            return info;
        }


        info.mProgramId = mProgramId;			// 影片ID
        info.mProgramName = mProgramName;		// 影片名称
        info.mType = "DMS";				// 影片类型
        info.contentType = "未知";
        info.mProgramDuration = mDuration;		// 影片时长(秒)

        info.mIssuer = "数字节目管理中心";	// 发行者
        info.mIssueDate = "";			// 发行时间
        info.mCreator = "数字节目管理中心";
        info.mCountry = mDmsAVFormat;		// Aavan: 2009-5-30    TEMP
        info.mDubLanguage = mDefaultAudioName;				// 语言
        info.mSubtitle = "";				// 字幕



        for (int i = 0; i < files.length; i++)  {
            String filePath, temp;
            String sName = files[i].getName();


            // 查找视频文件
            filePath = programFileName;
            if (TextUtils.equals(filePath,sName))
            {
                info.mPprogramSize = files[i].length();
                info.mVideoFile = programFileName;
            }

            temp = mProgramName;
            if (TextUtils.equals(temp,sName))
            {
                info.mVideoFile = mProgramName;
            }

            // 查找音频文件
            filePath = programFileName + ".ac3";
            if (TextUtils.equals(filePath,sName))
            {
                info.mAudioFile = programFileName + ".ac3";
            }

            temp = mProgramName + ".ac3";
            if (TextUtils.equals(temp,sName))
            {
                info.mAudioFile = mProgramName + ".ac3";
            }

            // 查找许可证视频文件
            filePath = programFileName + "xkz";

            if (TextUtils.equals(filePath,sName))
            {
                info.mVXkzFile = programFileName + "xkz";
            }

            temp = mProgramName + "xkz";
            if (TextUtils.equals(temp,sName))
            {
                info.mVXkzFile = mProgramName + "xkz";
            }

            // 查找许可证音频文件
            filePath = programFileName + "xkz.ac3";

            if (TextUtils.equals(filePath,sName))
            {
                info.mAXkzFile = programFileName + "xkz.ac3";
            }

            temp = mProgramName + "xkz.ac3";
            if (TextUtils.equals(temp,sName))
            {
                info.mAXkzFile = mProgramName + "xkz.ac3";
            }
        }

        return info;
    }


}
