package com.code_tinker.window.menu.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by zhouqiong on 2017/6/9.
 */
public class DmmApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
