package com.phonegame.util;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;


public class AnalysisUtil {

    public static void umengOnResume(Activity activity){
        MobclickAgent.onResume(activity);
    }
    
    public static void umengOnPause(Activity activity){
        MobclickAgent.onPause(activity);
    }
    
}
