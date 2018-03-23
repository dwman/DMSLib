package com.dwman.log.bean;

/**
 * Created by ldw on 2018/3/20.
 */

public class MovieBean extends BaseLogBean {

    public String mMovieName;
    public int mMovieId;

    public long mtartTime;//毫秒为单位
    public long mEndTime;

    public int mRemainCount;//电影的剩余场次
    public int mTotalCount;//电影播放的总场次


    public int mLongitude;//经度
    public int mLatitude;//纬度


}
