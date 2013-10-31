package com.phonegame.service;

import java.util.Map;

import android.content.Context;

import com.phonegame.YunCallbackHandler;
import com.phonegame.util.ClientUtil;
import com.phonegame.util.HttpUtil;


public class OpenApiService {

    private static class SingleServiceHolder {

        private static final OpenApiService service=new OpenApiService();
    }

    public static OpenApiService getInstance(Context context) {
        HttpUtil.setContext(context);
        return SingleServiceHolder.service;
    }
    
    public void init(Map<String, String> params, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.GAME_INIT_URL, HttpUtil.POST_METHOD, params, handler);
    }
    
    public void login(Map<String, String> userInfo, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.USER_LOGIN_URL, HttpUtil.POST_METHOD, userInfo, handler);
    }
    
    public void thirdUserLogin(Map<String, String> userInfo, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.OTHER_USER_LOGIN_URL, HttpUtil.POST_METHOD, userInfo, handler);
    }
    
    public void register(Map<String, String> userInfo, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.USER_REGISTER_URL, HttpUtil.POST_METHOD, userInfo, handler);
    }
    
    public void loginServer(final Map<String, String> data){
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                HttpUtil.getInstance().httpSynSend(ClientUtil.GAME_SERVER_LOGIN_URL, "", HttpUtil.POST_METHOD, data);
            }
        }).start();
    }
    
    public void findPassword(Map<String, String> data, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.USER_FIND_PASSWORD_URL, HttpUtil.POST_METHOD, data, handler);
    }
    
    public void changePassword(Map<String, String> data, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.USER_CHANGE_PASSWORD_URL, HttpUtil.POST_METHOD, data, handler);
    }
    
    public void createOrder(Map<String, String> params, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.CREATE_ORDER_URL, HttpUtil.POST_METHOD, params, handler);
    }
    
    public void googleBillingUpdate(Map<String, String> params, YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.GOOGLE_BILLING_UPDATE, HttpUtil.POST_METHOD, params, handler);
    }
    
    public void heartBeat(){
        HttpUtil.getInstance().httpSynSend(ClientUtil.USER_HEART_BEAT_URL, "", HttpUtil.GET_METHOD, null);
    }
    
    public void logout(YunCallbackHandler handler){
        HttpUtil.getInstance().httpAsynSend(ClientUtil.USER_LOGOUT_BEAT_URL, HttpUtil.GET_METHOD, null, handler);
    }
}
