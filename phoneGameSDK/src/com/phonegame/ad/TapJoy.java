package com.phonegame.ad;

import android.content.Context;

import com.phonegame.util.CommonUtil;
import com.tapjoy.TapjoyConnect;


public class TapJoy {

    private static TapJoy instance;
    
    public static TapJoy getInstance(){
        if(instance == null){
            instance=new TapJoy();
        }
        return instance;
    }
    
    public void requestTapJoy(Context ctx){
        String key=ctx.getString(CommonUtil.getResourceId(ctx, "string", "tapjoy_key"));
        String value=ctx.getString(CommonUtil.getResourceId(ctx, "string", "tapjoy_value"));
        TapjoyConnect.requestTapjoyConnect(ctx, key,value);
    }
}
