package com.phonegame.view;

import java.util.HashMap;
import java.util.Map;

import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.service.OpenApiService;
import com.phonegame.util.AccountUtil;
import com.phonegame.util.AnalysisUtil;
import com.phonegame.util.CommonUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;


public class FindPwdActivity extends Activity implements OnClickListener{

    private int findBtn;
    
    private int toLoginBtnId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(CommonUtil.getResourceId(this, "layout", "activity_forget_password"));
        findBtn=CommonUtil.getResourceId(this, "id", "password_find_btn");
        findViewById(findBtn).setOnClickListener(this);        
        toLoginBtnId=CommonUtil.getResourceId(this, "id", "account_login_btn");
        findViewById(toLoginBtnId).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == findBtn){
            doFindPwd();
        }else if(v.getId() == toLoginBtnId){
            toLogin(v);
        }
    }
    
    private void doFindPwd(){
        EditText accountTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "forget_account"));
        EditText emailTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "forget_email"));
        final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
        String invalid_account=this.getString(CommonUtil.getResourceId(this, "string", "invalid_account"));
        String invalid_email=this.getString(CommonUtil.getResourceId(this, "string", "invalid_email"));
        final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
        final String account=accountTxt.getText().toString();
        final String email=emailTxt.getText().toString();
        if(!AccountUtil.isAvailableUsername(account) && !AccountUtil.isEmail(account)){
            CommonUtil.showAlertDialog(this, error, invalid_account, returnTxt, null);
            return;
        }
        if(!AccountUtil.isEmail(email)){
            CommonUtil.showAlertDialog(this, error, invalid_email, returnTxt, null);
            emailTxt.requestFocus();
            return;
        }
        Map<String, String> data=new HashMap<String, String>();
        data.put("account", account);
        data.put("email", email);
        CommonUtil.showLoadingDialog(this);
        OpenApiService.getInstance(this).findPassword(data, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(FindPwdActivity.this, rspContent, Toast.LENGTH_LONG).show();
                finish();
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
                CommonUtil.showAlertDialog(FindPwdActivity.this, error, exp.getMessage(), returnTxt, null);
            }
        });
    }
    
    public void toLogin(View target){
        Intent intent=new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
