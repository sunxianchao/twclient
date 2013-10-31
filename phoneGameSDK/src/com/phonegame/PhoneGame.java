package com.phonegame;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;

import com.phonegame.app.Application;
import com.phonegame.service.OpenApiService;
import com.phonegame.util.ClientUtil;
import com.phonegame.util.DeviceIdManager;
import com.phonegame.util.JsonUtil;
import com.phonegame.view.LoginActivity;
import com.phonegame.view.PayActivity;

public class PhoneGame {
    
    private static YunCallbackHandler internalHandler;
    
    private static class SingleServiceHolder {

        private static final PhoneGame instance=new PhoneGame();
    }
    
    public static YunCallbackHandler getInternalHandler(){
        return PhoneGame.internalHandler;
    }

    public static PhoneGame getInstance() {
        return SingleServiceHolder.instance;
    }

    public void init(Context context, YunOpParams paramsMap, final YunCallbackHandler handler) {
        ApplicationInfo info=null;
        try {
            info = ((Activity)context).getPackageManager().getApplicationInfo(
                ((Activity)context).getPackageName(), PackageManager.GET_META_DATA);
        } catch(NameNotFoundException e) {
            e.printStackTrace();
        }
        String promptChannel=info.metaData.get("UMENG_CHANNEL").toString();
        paramsMap.add("promptChannel", promptChannel);
        ClientUtil.setConfigMap(paramsMap);

        Map<String, String> params=new HashMap<String, String>();
        params.put("imei", DeviceIdManager.getDeviceId(context));
        params.put("macAddress", DeviceIdManager.getMacAddress(context));
        params.put("ua", Build.MODEL);
        OpenApiService.getInstance(context).init(params, new YunCallbackHandler() {

            @Override
            public void onSuccess(String rspContent, int statusCode) {
                String currentUserGameId=JsonUtil.newJsonUtil(rspContent).getString("id");
                ClientUtil.getConfigMap().add("currentUserGameId", currentUserGameId);
                handler.onSuccess("", RespondCode.SUCCESS);
            }

            @Override
            public void onFailure(YunCallbackException exp) {
                handler.onFailure(exp);
            }
        });
    }
    
    
    public void login(Context context, YunCallbackHandler handler){
        //是否初始化的判断
        internalHandler=handler;
        Intent intent=new Intent();
        intent.setClass(context, LoginActivity.class);
        ((Activity)context).startActivity(intent);
    }
    
    public void doPay(Context context, YunOpParams paramsMap, YunCallbackHandler handler){
      //是否初始化的判断
        internalHandler=handler;
        Bundle values=new Bundle();
        for(String key : paramsMap.getKeys()){
            values.putString(key, paramsMap.getValue(key));
        }
        Intent intent=new Intent();
        intent.setClass(context, PayActivity.class);
        intent.putExtra("orderParams", values);
        ((Activity)context).startActivity(intent);
    }
    
    public void loginServer(Context context, YunOpParams paramsMap){
        Map<String, String> data=paramsMap.getParameters();
        OpenApiService.getInstance(context).loginServer(data);
    }
    
    public void logout(Context context, final YunCallbackHandler handler){
        Application.getInstance().closeServices(context);
        OpenApiService.getInstance(context).logout(new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                handler.onSuccess("success", 200);
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                
            }
        });
    }
}
