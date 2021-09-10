package com.transsnet.transsdktest.base;

import androidx.multidex.MultiDexApplication;

import com.transsnet.transsdk.manager.TransConfigManager;

public class BaseApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // 此处必须配置channelKey否则无法使用
        TransConfigManager.getTransConfig().init(this, "input your ChannelKey");
        TransConfigManager.getTransConfig().setLoginEnabled(true);
    }
}
