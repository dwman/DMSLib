package com.dwman.dmsdemo;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dwman.log.LogManager;
import com.dwman.log.bean.ADBean;
import com.dwman.log.bean.BaseLogBean;
import com.dwman.log.bean.MovieBean;
import com.dwman.log.db.DBDao;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private LogManager manager;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);

            parserData();


        }
    };

    private void parserData() {
        List<BaseLogBean> logBeen = manager.queryAllLog(0, 0);

        if (logBeen != null && logBeen.size()>0) {
            for (BaseLogBean baseLogBean : logBeen) {
                Log.e(TAG, "baseLogBean: "+baseLogBean.mType+"----"+baseLogBean.action+"---"+baseLogBean.date+"---" +
                        baseLogBean.content);

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

        mHandler.sendEmptyMessageDelayed(0x110,30000);

    }

    private void initData() {
        manager = LogManager.getInstance();


        new Thread(){
            @Override
            public void run() {
                super.run();


        BaseLogBean baseLogBean;
                DBDao.Type type;
        for (int i = 0; i < 20; i++) {
            if (i% 3 ==0){
                baseLogBean = new ADBean() ;
                baseLogBean.mType = 2;
                type = DBDao.Type.AD;
            }else {
                type = DBDao.Type.MOVIE;
                baseLogBean = new MovieBean();
                baseLogBean.mType = 1;
            }


            if (i%2 == 0){
                baseLogBean.action = 0;
            }else {
                baseLogBean.action = 1;
            }
            baseLogBean.date =System.currentTimeMillis();

            manager.insertIntoAllLog(type,baseLogBean);
            SystemClock.sleep(500);
        }

       }
        }.start();
    }
}
