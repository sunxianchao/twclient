package com.phonegame.view;

import java.util.HashMap;
import java.util.Map;

import com.phonegame.Constants;
import com.phonegame.PhoneGame;
import com.phonegame.RespondCode;
import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.service.OpenApiService;
import com.phonegame.util.AccountUtil;
import com.phonegame.util.AnalysisUtil;
import com.phonegame.util.ClientUtil;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.DES;
import com.phonegame.util.DeviceIdManager;
import com.phonegame.util.JsonUtil;
import com.phonegame.util.SharePreferencesEditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class RegisterActivity extends Activity implements OnClickListener{

    private int regBtnId;
    private int toLoginBtnId;
    private SharePreferencesEditor editor;
    private EditText accountTxt;
    private EditText passwordTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editor=new SharePreferencesEditor(this);
        setContentView(CommonUtil.getResourceId(this, "layout", "activity_register"));
        regBtnId=CommonUtil.getResourceId(this, "id", "account_register_btn");
        toLoginBtnId=CommonUtil.getResourceId(this, "id", "account_login_btn");
        findViewById(regBtnId).setOnClickListener(this);
        findViewById(toLoginBtnId).setOnClickListener(this);
        accountTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "account_login"));
        passwordTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "password_login"));
    }
    
    public void toLogin(View target){
        Intent intent=new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == toLoginBtnId){
            toLogin(v);
        }else if(v.getId() == regBtnId){
            doRegister();
        }
    }
    
    private void doRegister(){
        EditText confirmPasswordTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "register_confire_password"));
        EditText registerEmailTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "register_email"));
        final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
        String invalid_account=this.getString(CommonUtil.getResourceId(this, "string", "invalid_account"));
        String invalid_password=this.getString(CommonUtil.getResourceId(this, "string", "invalid_password"));
        String invalid_repassword=this.getString(CommonUtil.getResourceId(this, "string", "invalid_repassword"));
        String invalid_email=this.getString(CommonUtil.getResourceId(this, "string", "invalid_email"));
        final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
        final String account=accountTxt.getText().toString();
        final String password=passwordTxt.getText().toString();
        String confirmPassword=confirmPasswordTxt.getText().toString();
        String regEmail=registerEmailTxt.getText().toString();
        if(!AccountUtil.isAvailableUsername(account)){
            CommonUtil.showAlertDialog(this, error, invalid_account, returnTxt, null);
            accountTxt.requestFocus();
            return;
        }
        if(!AccountUtil.isAvailablePassword(password)){
            CommonUtil.showAlertDialog(this, error, invalid_password, returnTxt, null);
            passwordTxt.requestFocus();
            return;
        }
        if(!password.equals(confirmPassword)){
            CommonUtil.showAlertDialog(this, error, invalid_repassword, returnTxt, null);
            confirmPasswordTxt.requestFocus();
            return;
        }
        if(!AccountUtil.isEmail(regEmail)){
            CommonUtil.showAlertDialog(this, error, invalid_email, returnTxt, null);
            registerEmailTxt.requestFocus();
            return;
        }
        Map<String, String> userInfo=new HashMap<String, String>();
        userInfo.put("account", account);
        userInfo.put("password", password);
        userInfo.put("repassword", confirmPassword);
        userInfo.put("email", regEmail);
        userInfo.put("imei", DeviceIdManager.getDeviceId(this));
        OpenApiService.getInstance(this).register(userInfo, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                try {
                    JsonUtil json=JsonUtil.newJsonUtil(rspContent);
                    ClientUtil.getConfigMap().add(Constants.CURRENT_USER_GAME_ID, json.getString("userGameId"));
                    editor.put("account", account);
                    editor.put("password", DES.encode(password, ClientUtil.getConfigMap().getValue("secretKey")));
                    PhoneGame.getInternalHandler().onSuccess(rspContent, RespondCode.SUCCESS);
                    finish();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.showAlertDialog(RegisterActivity.this, error, exp.getMessage(), returnTxt, null);
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toLogin(null);
    }
    
    public void onResume() {
        super.onResume();
        AnalysisUtil.umengOnResume(this);
    }

    public void onPause() {
        super.onPause();
        AnalysisUtil.umengOnPause(this);
    }
}
