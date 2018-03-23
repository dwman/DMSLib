package com.dwman.log.bean;

/**
 * Created by ldw on 2018/3/20.
 */

public class BaseLogBean {
    public static final int _ID = 0;
    public static final int ALL_LOG_TYPE = 1;
    public static final int ALL_LOG_ACTION = 2;
    public static final int ALL_LOG_DATE = 3;
    public static final int ALL_LOG_CONTENT = 4;

    public static final int MOIVIE_AND_AD_LOG_ACTION = 1;
    public static final int MOIVIE_AND_AD__LOG_DATE = 2;
    public static final int MOIVIE_AND_AD__LOG_ID = 3;
    public static final int MOIVIE_AND_AD__LOG_NAME = 4;
    public static final int MOIVIE_AND_AD__LOG_CONTEN = 5;




    public Integer _id;


    public int mType ;
    /**
     * 0为开始，1为结束或停止
     */
    public int action;

    /**
     * 数据保存的时间,精确到毫秒
     */
    public long date;
    /**
     * 为相关类型的json
     */
    public String content;

}
