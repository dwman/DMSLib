package com.dwman.dms.util;

import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by ldw on 2017/11/21.
 */

public class StorageUtil {
    private static final String TAG = "StorageUtil";

    /**
     * 获取外接内存的大小，单位字节
     * @param context
     * @return 数组Data，Data[0] totalSize；Data[1] availableSize；
     */
    public static long[]  getUsbExtention(Context context){
        long[] usbData = new long[2];
        StorageManager storageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            long totalSize =0;
            long availableSize=0;
            for (int i = 0; i < ((String[])invoke).length; i++) {
                String path = ((String[]) invoke)[i];
                Log.i(TAG, "path----> " + path);
                int j= path.lastIndexOf("/");
                int length = path.length();
                if ((length-j)>3){
                    String s = path.substring(j+1, j + 4);
                    Log.e(TAG,"s "+s);
                    if (TextUtils.equals(s,"sda")) {
                        long[] sdcardSize = getSdcardSize(path);
                            totalSize +=sdcardSize[0];
                            availableSize+=sdcardSize[1];

                    }
                }



            }

            usbData[0]=totalSize;
            usbData[1]=availableSize;
            return usbData;


        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        return usbData;
    }

    /**
     * 获取SD卡,u盘的大小信息
     * @param path sd卡或usb路径
     * @return 数组Data，Data[0] totalSize；Data[1] availableSize；
     */
    public static long[] getSdcardSize(String path) {
        long[] uData = new long[2];

        StatFs stat = new StatFs(path);

        long availableBytes = stat.getAvailableBytes();
        Log.i(TAG,"availableBytes :"+(availableBytes/1024/1024)+"MB");

        long totalBytes = stat.getTotalBytes();
        Log.i(TAG,"totalBytes :"+(totalBytes/1024/1024)+"MB");

        uData[0]=totalBytes;
        uData[1]=availableBytes;
        return uData;
    }
}
