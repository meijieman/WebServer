package com.major.webserver;

import android.os.Binder;

/**
 * @desc: TODO
 * @author: Major
 * @since: 2017/8/20 11:50
 */
public class Moniter extends Binder{

    private MoniterListener mListener;
    private WebService mService;

    public Moniter(WebService service){
        mService = service;
    }

    public void reg(MoniterListener listener){
        mListener = listener;
    }

    public void unreg(MoniterListener listener){
        mListener = null;
    }

    public void update(String msg){
        if(mListener != null){
            mListener.onMsg(msg);
        }
    }


    public interface MoniterListener{

        void onMsg(String msg);
    }
}
