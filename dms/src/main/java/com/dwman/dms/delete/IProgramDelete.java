package com.dwman.dms.delete;


import com.dwman.dms.bean.ProgramInfo;

import java.util.List;

/**
 * Created by ldw on 2017/11/27.
 */

public interface IProgramDelete {

    void delete( int programType, List<ProgramInfo> infos);
    void delete(ProgramInfo info);

    void deleteAll();

}
