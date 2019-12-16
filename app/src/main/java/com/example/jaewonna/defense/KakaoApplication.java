package com.example.jaewonna.defense;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

public class KakaoApplication extends Application{
    private static volatile KakaoApplication obj = null;
    private static volatile Activity currentActivity = null;
    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }
    public static KakaoApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        KakaoApplication.currentActivity = currentActivity;
    }

}
