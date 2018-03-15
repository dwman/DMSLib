package com.dwman.dms.query;

import com.dwman.dms.bean.ProgramData;
import com.dwman.dms.bean.ProgramInfo;

import java.util.List;

/**
 * Created by ldw on 2018/3/13.
 */

public class ProgramQuery implements IProgramQuery {

    private  IProgramQuery  mQuery = new CProgramQuery();

    public ProgramQuery(IProgramQuery query){
        this.mQuery = query;
    }
    public ProgramQuery(){


    }


    @Override
    public ProgramData queryAllProgram(String src) {
        return mQuery.queryAllProgram(src);
    }

    @Override
    public List<String> queryProgram(String src, int programType) {
        return mQuery.queryProgram(src,programType);
    }

    @Override
    public boolean findProgramOnHd(int type, ProgramInfo info) {
        return mQuery.findProgramOnHd(type,info);
    }


    public void setmQuery(IProgramQuery mQuery) {
        this.mQuery = mQuery;
    }
}
