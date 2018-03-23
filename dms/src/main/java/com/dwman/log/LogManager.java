package com.dwman.log;

import android.database.Cursor;

import com.dwman.dms.Constants;
import com.dwman.dms.util.LogUtil;
import com.dwman.log.bean.ADBean;
import com.dwman.log.bean.BaseLogBean;
import com.dwman.log.bean.DownloadBean;
import com.dwman.log.bean.MovieBean;
import com.dwman.log.bean.PowerBean;
import com.dwman.log.db.DBDao;
import com.dwman.log.db.DBDao.Type;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ldw on 2018/3/19.
 */

public class LogManager {
    private static final String TAG = "LogManager";

    private static LogManager instance;
    private final DBDao dbDao;

    public static LogManager getInstance() {
        if (instance == null) {
            synchronized (LogManager.class) {
                if (instance == null) {
                    instance = new LogManager();
                }
            }
        }
        return instance;
    }

    private LogManager() {
        dbDao = DBDao.getInstance();
    }

    public void insertIntoAllLog(Type type, BaseLogBean baseLogBean) {
        dbDao.insertIntoAllLog(type, baseLogBean);

    }

    public void insertIntoAdLog(BaseLogBean baseLogBean) {
        dbDao.insertIntoAdLog(baseLogBean);

    }

    public void insertIntoMovieLog(BaseLogBean baseLogBean) {
        dbDao.insertIntoMovieLog(baseLogBean);

    }

    /**
     * 查询所有日志信息,如果为给定日期默认查询当天的日志信息
     *
     * @param id
     * @param date
     * @return
     */
    public List<BaseLogBean> queryAllLog(int id, long date) {
        long tempDate;
        if (date == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)
                    , calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 1);

            tempDate = calendar.getTimeInMillis();
        }else {
            tempDate =date;
        }

        String groupBy = "date";
        String having = "date>"+tempDate;
        String orderBy = "date";
        Cursor cursor = queryAllLog(null, null, null, groupBy, having, orderBy);

        if (cursor == null || cursor.getCount()<=0) {
            return null;
        }
        List<BaseLogBean> logBeans = new ArrayList<>();
        Gson gson = new Gson();


        while (cursor.moveToNext()) {

            long _id = cursor.getLong(BaseLogBean._ID);
            if (_id<id){
                continue;
            }

            int type = cursor.getInt(BaseLogBean.ALL_LOG_TYPE);
            int action = cursor.getInt(BaseLogBean.ALL_LOG_ACTION);
            String content = cursor.getString(BaseLogBean.ALL_LOG_CONTENT);
            BaseLogBean baseLogBean = null;
            switch (type){
                case Constants.LOG_TYPE_MOVIE:
                    baseLogBean = gson.fromJson(cursor.getString(BaseLogBean.ALL_LOG_CONTENT), MovieBean.class);

                    break;
                case Constants.LOG_TYPE_AD:
                    baseLogBean = gson.fromJson(cursor.getString(BaseLogBean.ALL_LOG_CONTENT), ADBean.class);
                    break;
                case Constants.LOG_TYPE_DOWNLOAD:
                    baseLogBean = gson.fromJson(cursor.getString(BaseLogBean.ALL_LOG_CONTENT), DownloadBean.class);
                    break;
                case Constants.LOG_TYPE_POWER:
                    baseLogBean = gson.fromJson(cursor.getString(BaseLogBean.ALL_LOG_CONTENT), PowerBean.class);
                    break;
            }
            baseLogBean.content = content;

            logBeans.add(baseLogBean);


        }

        cursor.close();


        return logBeans;

    }


    public Cursor queryAllLog(String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {


        return dbDao.queryAllLog(columns, selection, selectionArgs
                , groupBy, having, orderBy);
    }

    public Cursor queryMovieLog(String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {

        return dbDao.queryMovieLog(columns, selection, selectionArgs
                , groupBy, having, orderBy);
    }

    public Cursor queryAdLog(String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {

        return dbDao.queryAdLog(columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public long deletAllLog(String whereClause,String[] whereArgs){
        return dbDao.deletAllLog(whereClause,whereArgs);
    }
    public long deletAdLog(String whereClause,String[] whereArgs){
        return dbDao.deletAdLog(whereClause,whereArgs);
    }
    public long deletMovieLog(String whereClause,String[] whereArgs){
        return dbDao.deletMovieLog(whereClause,whereArgs);
    }



}
