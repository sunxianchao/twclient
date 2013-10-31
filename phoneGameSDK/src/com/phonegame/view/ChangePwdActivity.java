package com.phonegame.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.service.OpenApiService;
import com.phonegame.util.AccountUtil;
import com.phonegame.util.AnalysisUtil;
import com.phonegame.util.CommonUtil;


public class ChangePwdActivity extends Activity implements OnClickListener{

    private int changeBtn;
    
    private int toLoginBtnId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(CommonUtil.getResourceId(this, "layout", "activity_change_password"));
        changeBtn=CommonUtil.getResourceId(this, "id", "password_change_btn");
        toLoginBtnId=CommonUtil.getResourceId(this, "id", "account_login_btn");
        findViewById(toLoginBtnId).setOnClickListener(this);
        findViewById(changeBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == changeBtn){
            doChangePwd();
        }else if(v.getId() == toLoginBtnId){
            toLogin(v);
        }
    }
    
    private void doChangePwd(){
        EditText accountTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "change_account"));
        EditText oldPwdTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "change_old_password"));
        EditText newPwdTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "change_new_password"));
        EditText confirmPwdTxt=(EditText)findViewById(CommonUtil.getResourceId(this, "id", "change_confirm_new_password"));
        final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
        String invalid_account=this.getString(CommonUtil.getResourceId(this, "string", "invalid_account"));
        String invalid_password=this.getString(CommonUtil.getResourceId(this, "string", "invalid_password"));
        String invalid_repassword=this.getString(CommonUtil.getResourceId(this, "string", "invalid_repassword"));
        final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
        final String account=accountTxt.getText().toString();
        final String oldPassword=oldPwdTxt.getText().toString();
        final String newPassword=newPwdTxt.getText().toString();
        final String confirmPassword=confirmPwdTxt.getText().toString();
        if(!AccountUtil.isAvailableUsername(account) && !AccountUtil.isEmail(account)){
            CommonUtil.showAlertDialog(this, error, invalid_account, returnTxt, null);
            return;
        }
        if(!AccountUtil.isAvailablePassword(oldPassword)){
            CommonUtil.showAlertDialog(this, error, invalid_password, returnTxt, null);
            oldPwdTxt.requestFocus();
            return;
        }
        if(!AccountUtil.isAvailablePassword(newPassword)){
            CommonUtil.showAlertDialog(this, error, invalid_password, returnTxt, null);
            newPwdTxt.requestFocus();
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            CommonUtil.showAlertDialog(this, error, invalid_repassword, returnTxt, null);
            confirmPwdTxt.requestFocus();
            return;
        }
        Map<String, String> data=new HashMap<String, String>();
        data.put("account", account);
        data.put("oldpassword", oldPassword);
        data.put("newpassword", newPassword);
        data.put("repassword", confirmPassword);
        CommonUtil.showLoadingDialog(this);
        OpenApiService.getInstance(this).changePassword(data, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                CommonUtil.closeLodingDialog();
                Toast.makeText(ChangePwdActivity.this, rspContent, Toast.LENGTH_LONG).show();
                finish();
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
                CommonUtil.showAlertDialog(ChangePwdActivity.this, error, exp.getMessage(), returnTxt, null);
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
