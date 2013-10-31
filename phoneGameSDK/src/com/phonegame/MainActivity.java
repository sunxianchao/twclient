package com.phonegame;

import com.phonegame.util.CommonUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(CommonUtil.getResourceId(this, "layout", "activity_main"));
        init(null);
    }

    public void init(View v){
        
        YunOpParams paramsMap=new YunOpParams();
        PhoneGame.getInstance().init(this, paramsMap, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                Toast.makeText(getApplicationContext(), rspContent+""+statusCode, Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                
            }
        });
    }
    
    public void login(View target){
        PhoneGame.getInstance().login(this, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                Toast.makeText(getApplicationContext(), rspContent+"\n"+statusCode, Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                //登录失败的处理
            }
        });
    }
    
    
    public void pay(View target){
        YunOpParams paramsMap=new YunOpParams();
        paramsMap.add("extInfo", "test");
        PhoneGame.getInstance().doPay(this, paramsMap, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                Toast.makeText(getApplicationContext(), rspContent+"\n"+statusCode, Toast.LENGTH_LONG).show();
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    public void loginServer(View target){
        YunOpParams paramsMap=new YunOpParams();
        paramsMap.add("roleId", "1");
        paramsMap.add("roleName", "独角兽");
        paramsMap.add("serverId", "1");
        paramsMap.add("serverName", "星空战记");
        PhoneGame.getInstance().loginServer(this, paramsMap);
    }
    
    public void logout(View target){
        PhoneGame.getInstance().logout(this, null);
    }
   
}
