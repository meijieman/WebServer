package com.major.webserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;

public class WebService extends Service{

    private ResponseHandler mHandler;

    public WebService(){
    }

    @Override
    public IBinder onBind(Intent intent){
        startWebServer();
        Moniter moniter = new Moniter(this);
        mHandler.setMoniter(moniter);
        return moniter;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        SL.i("启动服务");
        startWebServer();
        return START_STICKY;
    }

    private void startWebServer(){
        if(mHandler == null){
            mHandler = new ResponseHandler(ResponseHandler.DEFAULT_SERVER_PORT);
        }
        if(!mHandler.wasStarted()){
            try{
                mHandler.start();
            } catch(IOException e){
                e.printStackTrace();
                SL.e("启动 web service 发生异常 " + e);
            }
        }
    }

    @Override
    public void onDestroy(){
        SL.i("销毁服务");

        super.onDestroy();

    }
}
