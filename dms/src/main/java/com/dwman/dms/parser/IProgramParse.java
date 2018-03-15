package com.dwman.dms.parser;


import com.dwman.dms.bean.ProgramInfo;

import java.io.File;

/**
 * Created by ldw on 2017/10/31.
 */

public interface IProgramParse {
    /**
     * 解析数据
     * @param programPath 要查询的路径
     * @param programType 要查询的节目类型
     * @return 当解析失败时会返回null，所以要做非空判断
     */
     ProgramInfo parseProgramInfo( String programPath,int programType);
     boolean checkIntegrity(File file);


}
