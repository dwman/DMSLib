package com.dwman.dms.parser;

import com.dwman.dms.bean.ProgramInfo;

import java.io.File;

/**
 * Created by ldw on 2018/3/14.
 */

public class ProgramParse implements IProgramParse {


    private  IProgramParse mParser = new CDmsProgramParse();

    public ProgramParse(IProgramParse parse) {
        this.mParser = parse;
    }

    public ProgramParse() {
    }

    @Override
    public ProgramInfo parseProgramInfo(String programPath, int programType) {
        return mParser.parseProgramInfo(programPath,programType);
    }

    @Override
    public boolean checkIntegrity(File file) {
        return mParser.checkIntegrity(file);
    }

    public void setmParser(IProgramParse mParser) {
        this.mParser = mParser;
    }
}
