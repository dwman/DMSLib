package com.dwman.dms.query;

import com.dwman.dms.bean.ProgramData;
import com.dwman.dms.bean.ProgramInfo;

import java.util.List;

/**
 * Created by ldw on 2018/3/13.
 */

public interface IProgramQuery {





    /**
     * 查询全部节目的列表
     * @param src
     * @return 返回值不会为空，但是要判断集合数据是否为零
     */
    ProgramData queryAllProgram(String src);

    /**
     * 查询某一类型的节目
     * @param src 存储位置
     * @param programType 节目类型 ，programInfo中的type
     * @return
     */
    List<String> queryProgram(String src, int programType);

    /**
     * 查询节目是否已经存在
     * @param type
     * @param info
     * @return
     */
    boolean findProgramOnHd(int type, ProgramInfo info);
}
