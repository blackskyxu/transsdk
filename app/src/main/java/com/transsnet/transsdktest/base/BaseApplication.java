package com.transsnet.transsdktest.base;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.transsnet.transsdk.manager.TransConfigManager;

public class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
//        TransConfigImpl.getInstance().init(this, "1234567");
//        TransConfigImpl.getInstance().setLoginEnabled(true);

        TransConfigManager.getTransConfig().init(this, "xshare");
        TransConfigManager.getTransConfig().setLoginEnabled(true);
    }
}
