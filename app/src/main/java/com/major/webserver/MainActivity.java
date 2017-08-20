package com.major.webserver;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mMsg;
    private TextView mHint;

    private Moniter mMoniter;

    private Moniter.MoniterListener mListener = new Moniter.MoniterListener(){
        @Override
        public void onMsg(final String msg){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    mMsg.append("\n" + msg);
                }
            });
        }
    };

    private ServiceConnection mConn = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            mMoniter = (Moniter)service;
            mMoniter.reg(mListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    mMsg.append("\n" + "连接断开");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHint = (TextView)findViewById(R.id.tv_hint);
        mMsg = (TextView)findViewById(R.id.tv_msg);

        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        mHint.setText("请在远程浏览器中输入:\n\n" + Util.getLocalIpStr(this) + ":" + ResponseHandler.DEFAULT_SERVER_PORT);

        findViewById(R.id.btn_start).performClick();

        bindService(new Intent(this, WebService.class), mConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v){
        Intent service = new Intent(this, WebService.class);
        switch(v.getId()) {
            case R.id.btn_start:
                startService(service);
                break;
            case R.id.btn_stop:
                stopService(service);
                break;
        }
    }

    @Override
    protected void onDestroy(){
        if(mMoniter != null){
            mMoniter.unreg(null);
        }
        unbindService(mConn);

        super.onDestroy();
    }
}
