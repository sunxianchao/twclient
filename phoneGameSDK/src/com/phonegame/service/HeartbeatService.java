/**
 * $Id: HeartbeatService.java,v 1.4 2012/06/18 01:40:27 xianchao.sun Exp $
 */
package com.phonegame.service;

import android.content.Intent;
import android.os.IBinder;

/**
 * @author xianchao.sun@downjoy.com
 */
public class HeartbeatService extends android.app.Service {

    private boolean threadDisable;

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }
    
    private OpenApiService service=new OpenApiService();

    public void onCreate() {
        super.onCreate();
        /** 创建一个线程，每6分钟调用一次 */
        new Thread(new Runnable() {

            public void run() {
                while(!threadDisable) {
                    try {
                        Thread.sleep(1000 * 60 * 6);
                        service.heartBeat();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void onDestroy() {
        super.onDestroy();
        /** 服务停止时，终止 */
        this.threadDisable=true;
    }
}
