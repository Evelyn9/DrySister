package com.lhx.drysister;

import android.app.Application;

/**
 * Description:
 * Created by Ly on 2019/8/12.
 */
public class DrySisterApp extends Application {

    private static DrySisterApp context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // TODO 做一些全局初始化
    }

    public static DrySisterApp getContext() {
        return context;
    }
}
