package com.dwman.dms.delete;


import com.dwman.dms.bean.ProgramInfo;

import java.io.File;
import java.util.List;

/**
 * Created by ldw on 2017/11/27.
 */

public class CProgramDelete implements IProgramDelete {
    @Override
    public void delete(int programType, List<ProgramInfo> infos) {

        for (ProgramInfo info : infos) {
            String path = info.mPath;
            deleteFile(path);
        }

    }

    @Override
    public void delete(ProgramInfo info) {
        String path = info.mPath;
        deleteFile(path);
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.isDirectory()){

            File[] files = file.listFiles();
            for (File file1 : files) {
                deleteFile(file1.getAbsolutePath());
            }
            file.delete();

        }else {
            file.delete();
        }


    }

    @Override
    public void deleteAll() {

    }
}
