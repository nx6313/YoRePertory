package com.mtxyao.nxx.yorepertory.util;

import android.app.Application;

import com.lzy.okgo.OkGo;

public class MyApplication extends Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        OkGo.getInstance().init(this);
    }
}