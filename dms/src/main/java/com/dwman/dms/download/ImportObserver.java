package com.dwman.dms.download;


import com.dwman.dms.bean.ProgramInfo;

import java.util.List;

/**
 * Created by ldw on 2017/11/17.
 */

public interface ImportObserver {
    /**
     * 下载开始
     */
    void onImportStart();

    /**
     * 文件下载时，文件大小超出内存
     * @param l 可用内存大小
     * @param mnProgramSize 要导入文件大小
     */
    void onOutOfMemoryError(long l, long mnProgramSize);

    /**
     * 文件下载失败
     * @param index
     */
    void onImportFailedError(int index);

    /**
     * 下载完成
     * @param importFaileds 下载中失败的program index从0开始
     */
    void onImportCompletion(List<Integer> importFaileds);
    void onTempFileExist(List<String> temp);

    /**
     * 更新下载进度
     * @param singleProgramProgress
     * @param programCount 正在下载第几个文件
     * @param progress   进度0-100；
     */
    void notify(int singleProgramProgress, int programCount, int progress);

    /**
     * 下载中包含了已下载的program
     * @param programInfos 已存在的program信息列表
     */
    void onImportProgramExist(List<ProgramInfo> programInfos);
}
