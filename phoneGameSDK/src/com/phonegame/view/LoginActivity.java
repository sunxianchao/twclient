package com.phonegame.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;
import com.phonegame.Constants;
import com.phonegame.GameConfig;
import com.phonegame.PhoneGame;
import com.phonegame.RequestCode;
import com.phonegame.RespondCode;
import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.app.Application;
import com.phonegame.facebook.FacebookLoginActivity;
import com.phonegame.google.plus.MomentUtil;
import com.phonegame.google.plus.PlusClientFragment;
import com.phonegame.google.plus.PlusClientFragment.OnSignedInListener;
import com.phonegame.service.HeartbeatService;
import com.phonegame.service.OpenApiService;
import com.phonegame.util.AccountUtil;
import com.phonegame.util.AnalysisUtil;
import com.phonegame.util.ClientUtil;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.DES;
import com.phonegame.util.JsonUtil;
import com.phonegame.util.SharePreferencesEditor;

@SuppressWarnings("deprecation")
public class LoginActivity extends FragmentActivity implements OnClickListener, OnSignedInListener{

    private int regBtnId;
    private int doLoginBtnId;
    private SharePreferencesEditor editor;
    private String secretKey;
    private int fbLoginBtnId;
    private int forgetPwdTxt;
    private int changePwdTxt;
    private int googleLoginBtnId;
    public static boolean googleLogin=false;
    private PlusClientFragment mSignInFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editor=new SharePreferencesEditor(this);
        setContentView(CommonUtil.getResourceId(this, "layout", "activity_login"));
        regBtnId=CommonUtil.getResourceId(this, "id", "account_register_btn");
        doLoginBtnId=CommonUtil.getResourceId(this, "id", "account_login_btn");
        fbLoginBtnId=CommonUtil.getResourceId(this, "id", "fb_login");
        forgetPwdTxt=CommonUtil.getResourceId(this, "id", "forget_password_tv");
        changePwdTxt=CommonUtil.getResourceId(this, "id", "change_password_text");
        fbLoginBtnId=CommonUtil.getResourceId(this, "id", "fb_login");
        googleLoginBtnId=CommonUtil.getResourceId(this, "id", "google_login");
        int accountLoginEditTxt=CommonUtil.getResourceId(this, "id", "account_login");
        int passwordEditTxt=CommonUtil.getResourceId(this, "id", "password_login");
        mSignInFragment=PlusClientFragment.getPlusClientFragment(this, MomentUtil.VISIBLE_ACTIVITIES);
        findViewById(doLoginBtnId).setOnClickListener(this);
        findViewById(regBtnId).setOnClickListener(this);
        findViewById(fbLoginBtnId).setOnClickListener(this);
        findViewById(googleLoginBtnId).setOnClickListener(this);
        findViewById(forgetPwdTxt).setOnClickListener(this);
        findViewById(changePwdTxt).setOnClickListener(this);
        String account=editor.get("account", "");
        String password=editor.get("password", "");
        try {
            ((EditText)findViewById(accountLoginEditTxt)).setText(account);
            if(!TextUtils.isEmpty(password)){
                secretKey=GameConfig.SECRETKEY;
                ((EditText)findViewById(passwordEditTxt)).setText(DES.decode(password, secretKey));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public void toReg(View target){
        Intent intent=new Intent();
        intent.setClass(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent();
        if(v.getId() == regBtnId){
            toReg(v);
        }else if(v.getId() == doLoginBtnId){
            doLogin();
        }else if(v.getId() == fbLoginBtnId){
            intent.setClass(this, FacebookLoginActivity.class);
            startActivityForResult(intent, RequestCode.REQUEST_CODE_FACEBOOK_LOGIN);
        }else if(v.getId() == googleLoginBtnId){
            googleLogin=true;
            mSignInFragment.signIn(RequestCode.REQUEST_CODE_GOOGLE_LOGIN);
        }else if(v.getId() == changePwdTxt){
            intent.setClass(this, ChangePwdActivity.class);
            startActivity(intent);
        }else if(v.getId() == forgetPwdTxt){
            intent.setClass(this, FindPwdActivity.class);
            startActivity(intent);
        }
    }
    
    private void doLogin(){
        EditText accountTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "account_login"));
        EditText passwordTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "password_login"));
        final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
        String invalid_account=this.getString(CommonUtil.getResourceId(this, "string", "invalid_account"));
        String invalid_password=this.getString(CommonUtil.getResourceId(this, "string", "invalid_password"));
        final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
        final String account=accountTxt.getText().toString();
        final String password=passwordTxt.getText().toString();
        if(!AccountUtil.isAvailableUsername(account) && !AccountUtil.isEmail(account)){
            CommonUtil.showAlertDialog(this, error, invalid_account, returnTxt, null);
            return;
        }
        if(!AccountUtil.isAvailablePassword(password)){
            CommonUtil.showAlertDialog(this, error, invalid_password, returnTxt, null);
            passwordTxt.requestFocus();
            return;
        }
        Map<String, String> userInfo=new HashMap<String, String>();
        userInfo.put("account", account);
        userInfo.put("password", password);
        CommonUtil.showLoadingDialog(this);
        OpenApiService.getInstance(this).login(userInfo, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                try {
                    JsonUtil json=JsonUtil.newJsonUtil(rspContent);
                    ClientUtil.getConfigMap().add(Constants.CURRENT_USER_GAME_ID, json.getString("userGameId"));
                    editor.put("account", account);
                    editor.put("password", DES.encode(password, GameConfig.SECRETKEY));
                    CommonUtil.closeLodingDialog();
                    startHeartService();
                    PhoneGame.getInternalHandler().onSuccess(rspContent, RespondCode.SUCCESS);
                    finish();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
                CommonUtil.showAlertDialog(LoginActivity.this, error, exp.getMessage(), returnTxt, null);
            }
        });
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RequestCode.REQUEST_CODE_GOOGLE_LOGIN){
            mSignInFragment.handleOnActivityResult(requestCode, resultCode, data);
        }else{
            Bundle bundle=data.getExtras();
            if(bundle == null){
                final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
                final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
                final String accountInfoError=this.getString(CommonUtil.getResourceId(this, "string", "account_info_error"));
                CommonUtil.showAlertDialog(LoginActivity.this, error, accountInfoError, returnTxt, null);
                return;
            }
            CommonUtil.showLoadingDialog(this);
            thirdUserLogin(RequestCode.REQUEST_CODE_FACEBOOK_LOGIN, bundle);
        }
    }
    
    private void thirdUserLogin(int userType, Bundle bundle){
        String tid=bundle.getString("id");
        Map<String, String> userInfo=new HashMap<String, String>();
        userInfo.put("tid", tid);
        userInfo.put("userType", String.valueOf(userType));
        
        final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
        final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
        OpenApiService.getInstance(this).thirdUserLogin(userInfo, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                CommonUtil.closeLodingDialog();
                startHeartService();
                PhoneGame.getInternalHandler().onSuccess(rspContent, RespondCode.SUCCESS);
                finish();
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
                CommonUtil.showAlertDialog(LoginActivity.this, error, exp.getMessage(), returnTxt, null);
                
            }
        });
    }
    
    private void startHeartService(){
        Intent serviceIntent=new Intent(this, HeartbeatService.class);
        this.startService(serviceIntent);
        Application.getInstance().addIntentService(serviceIntent);
    }
    
    @Override
    public void onSignedIn(PlusClient plusClient) {
        Person currentPerson=plusClient.getCurrentPerson();
        if(currentPerson != null && googleLogin) {
            Bundle bundle=new Bundle();
            bundle.putString("id", currentPerson.getId());
            thirdUserLogin(RequestCode.REQUEST_CODE_GOOGLE_LOGIN, bundle);
            googleLogin=false;
        } 
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
