package com.dwman.dms.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldw on 2018/3/13.
 */

public class ProgramData {

    public List<String> mMovieList;
    public List<String> mPublicAdList;
    public List<String> mBusinessAdList;
    public List<String> mMusicList;
    public List<String> mSubAdList;
    public ProgramData(){
        mMovieList = new ArrayList<>();
        mPublicAdList = new ArrayList<>();
        mBusinessAdList = new ArrayList<>();
        mMusicList = new ArrayList<>();
        mSubAdList = new ArrayList<>();
    }

    public  void clearData() {
        if (mMovieList != null) {
            mMovieList.clear();
        }
        if (mPublicAdList != null) {
            mPublicAdList.clear();
        }
        if (mBusinessAdList != null) {
            mBusinessAdList.clear();
        }
        if (mMusicList != null) {
            mMusicList.clear();
        }
        if (mSubAdList != null) {
            mSubAdList.clear();
        }


    }

    public boolean empty() {
        return mMovieList.size()+mPublicAdList.size()+mBusinessAdList.size()+mMusicList.size()+mSubAdList.size()==0;
    }
}
