/**
 * $Id: YunYoYoApplication.java,v 1.1 2012/09/10 10:36:25 xianchao.sun Exp $
 */
package com.phonegame.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * @author xianchao.sun@downjoy.com
 */
public class Application{

    private Map<String, Activity> activityMap=new HashMap<String, Activity>();

    private List<Intent> intentList=new LinkedList<Intent>();

    private static Map<String, String> picMap=new HashMap<String, String>();

    private static Application instance;

    private Handler handler=new Handler();

    private Application() {
    }

    // 单例模式中获取唯一的MyApplication实例
    public static Application getInstance() {
        if(null == instance) {
            instance=new Application();
        }
        return instance;
    }

    // 添加Activity到容器中
    public void addActivity(String name, Activity activity) {
        activityMap.put(name, activity);
    }
    
    public Activity getActivity(String name){
        return activityMap.get(name);
    }

    public void addIntentService(Intent intent) {
        intentList.add(intent);
    }

    public void closeServices(Context ctx) {
        for(Intent intent: intentList) {
            if(intent != null){
                ctx.stopService(intent);
            }
        }
    }

    // 遍历所有Activity并finish
    public void exit() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
               Iterator<String> it=activityMap.keySet().iterator();
               while(it.hasNext()){
                   activityMap.get(it.next()).finish();
               }
            }
        }, 500);
    }

    public static Map<String, String> getResourceMap() {
        return picMap;
    }
}
