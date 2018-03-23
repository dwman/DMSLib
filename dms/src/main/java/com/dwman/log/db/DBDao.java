package com.dwman.log.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dwman.MyApp;
import com.dwman.dms.Constants;
import com.dwman.dms.util.LogUtil;
import com.dwman.log.bean.ADBean;
import com.dwman.log.bean.BaseLogBean;
import com.dwman.log.bean.DownloadBean;
import com.dwman.log.bean.MovieBean;
import com.dwman.log.bean.PowerBean;
import com.google.gson.Gson;

/**
 * Created by ldw on 2018/3/20.
 */

public class DBDao {
    private static final String TAG = "DBDao";
    private static DBDao instance;
    private final DBHelper dbHelper;
    private final SQLiteDatabase db;

    private DBDao() {

        dbHelper = new DBHelper(MyApp.getApplication());
        db = dbHelper.getWritableDatabase();


    }


    public static DBDao getInstance() {
        if (instance == null) {
            synchronized (DBDao.class) {
                if (instance == null) {
                    instance = new DBDao();
                }
            }
        }
        return instance;
    }


    public long insertIntoAllLog(Type type, BaseLogBean baseLogBean) {

        Gson gson = new Gson();
        String content;
        ContentValues values = new ContentValues();

        switch (type) {
            case MOVIE:
                MovieBean movieBean = (MovieBean) baseLogBean;
                movieBean.mMovieId = 465465464;
                movieBean.mType = Constants.LOG_TYPE_MOVIE;
                content = gson.toJson(movieBean);

                values.put("type", Constants.LOG_TYPE_MOVIE);


                break;
            case AD:
                ADBean adBean = (ADBean) baseLogBean;
                adBean.mType = Constants.LOG_TYPE_AD;
                adBean.adId=6465465;
                content = gson.toJson(adBean);
                values.put("type", Constants.LOG_TYPE_AD);

                break;
            case DOWNLOAD:
                DownloadBean downloadBean = (DownloadBean) baseLogBean;
                downloadBean.mType = Constants.LOG_TYPE_DOWNLOAD;
                content = gson.toJson(downloadBean);
                values.put("type", Constants.LOG_TYPE_DOWNLOAD);

                break;
            case POWER:

                PowerBean powerBean = (PowerBean) baseLogBean;
                powerBean.mType = Constants.LOG_TYPE_POWER;
                content = gson.toJson(powerBean);
                values.put("type", Constants.LOG_TYPE_POWER);
                break;
            default:
                throw new RuntimeException(TAG + "  unknown log type");


        }

        values.put("date", baseLogBean.date);
        values.put("content", content);
        Log.e(TAG,"content :"+content);
        values.put("action", baseLogBean.action);
        long insert = db.insert(Constants.TABLE_ALL_LOG, null, values);
        return insert;


    }

    public long insertIntoMovieLog(BaseLogBean baseLogBean) {
        MovieBean movieBean = (MovieBean) baseLogBean;

        Gson gson = new Gson();
        String content = gson.toJson(movieBean);
        ContentValues values = new ContentValues();
        values.put("action", movieBean.action);
        values.put("movieId", movieBean.mMovieId);
        values.put("date", movieBean.date);
        values.put("content", content);
        values.put("name", movieBean.mMovieName);


       return db.insert(Constants.TABLE_MOVIE_LOG, null, values);


    }

    public long insertIntoAdLog(BaseLogBean baseLogBean) {
        ADBean bean = (ADBean) baseLogBean;

        Gson gson = new Gson();
        String content = gson.toJson(bean);
        ContentValues values = new ContentValues();
        values.put("action", bean.action);
        values.put("movieId", bean.adId);
        values.put("date", bean.date);
        values.put("content", content);
        values.put("name", bean.name);


       return db.insert(Constants.TABLE_MOVIE_LOG, null, values);


    }


    public Cursor queryAllLog(String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {


        return db.query(Constants.TABLE_ALL_LOG, columns, selection, selectionArgs
                , groupBy, having, orderBy);
    }

    public Cursor queryMovieLog(String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {

        return db.query(Constants.TABLE_ALL_LOG, columns, selection, selectionArgs
                , groupBy, having, orderBy);
    }

    public Cursor queryAdLog(String[] columns, String selection, String[] selectionArgs
            , String groupBy, String having, String orderBy) {

        return  db.query(Constants.TABLE_ALL_LOG, columns, selection, selectionArgs
                , groupBy, having, orderBy);
    }

    public long updateAllLog(ContentValues values,String whereClause,String[] whereArgs ){
       return db.update(Constants.TABLE_ALL_LOG,values,whereClause,whereArgs);
    }
    public long updateMovieLog(ContentValues values,String whereClause,String[] whereArgs ){
        return db.update(Constants.TABLE_MOVIE_LOG,values,whereClause,whereArgs);
    }
    public long updateAdLog(ContentValues values,String whereClause,String[] whereArgs ){
        return db.update(Constants.TABLE_AD_LOG,values,whereClause,whereArgs);
    }
    public long deletAllLog(String whereClause,String[] whereArgs){
        return db.delete(Constants.TABLE_ALL_LOG,whereClause,whereArgs);
    }
    public long deletAdLog(String whereClause,String[] whereArgs){
        return db.delete(Constants.TABLE_AD_LOG,whereClause,whereArgs);
    }
    public long deletMovieLog(String whereClause,String[] whereArgs){
        return db.delete(Constants.TABLE_MOVIE_LOG,whereClause,whereArgs);
    }



    public enum Type {
        MOVIE, AD, DOWNLOAD, POWER
    }

}
