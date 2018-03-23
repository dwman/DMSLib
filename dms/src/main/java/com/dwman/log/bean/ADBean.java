package com.dwman.log.bean;

/**
 * Created by ldw on 2018/3/20.
 */

public class ADBean extends BaseLogBean {


    /**
     * 广告的id
     */
    public int adId;

    /**
     *广告的名字
     */
    public String name;

    /**
     * 广告的类型0为商业广告，1为公益广告
     */
    public int adType;
    public long mtartTime;//单位为毫秒
    public long mEndTime;
    /**
     * 播放广告类型: 0x00 手动播放单条广告,0x01 手动连续广告播放,0x02 开机广告自动播放(多条)
     */
    public int mADMode;

    public int mLongitude;//经度
    public int mLatitude;//纬度


}
