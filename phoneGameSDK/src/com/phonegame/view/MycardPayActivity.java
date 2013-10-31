package com.phonegame.view;

import java.util.HashMap;
import java.util.Map;

import tw.com.mycard.mycardsdk.MyCardActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.phonegame.GameConfig;
import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.app.Application;
import com.phonegame.service.OpenApiService;
import com.phonegame.to.PayType;
import com.phonegame.util.AnalysisUtil;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.JsonUtil;


public class MycardPayActivity extends Activity implements OnClickListener{

    private int submitPay;
    
    private int back;

    private String currentSku;
    
    private TableLayout payLayout;

    private int googlebuy;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_mycard_pay_billing"));
        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_mycard_pay_billing_portrait"));
        }
        Application.getInstance().addActivity("mycardActivity", this);
        submitPay=CommonUtil.getResourceId(this, "id", "buy");
        back=CommonUtil.getResourceId(this, "id", "close_btn");
        googlebuy=CommonUtil.getResourceId(this, "id", "googlebuy");
        findViewById(submitPay).setOnClickListener(this);
        findViewById(back).setOnClickListener(this);
        findViewById(googlebuy).setOnClickListener(this);
        payLayout=(TableLayout)findViewById(CommonUtil.getResourceId(this, "id", "tblLayout"));
        int rows=payLayout.getChildCount();
        for(int i=0; i<rows; i++){
            TableRow row=(TableRow)payLayout.getChildAt(i);
            row.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
       Map<String, String> params=new HashMap<String, String>();
       Bundle extData=this.getIntent().getExtras().getBundle("orderParams");
       if(extData != null && !extData.isEmpty()){
           for(String key : extData.keySet()){
               if(!TextUtils.isEmpty(key)){
                   params.put(key, extData.getString(key));
               }
           }
       }
       if(v.getId()==submitPay){
           if(TextUtils.isEmpty(currentSku)){
               final String error=this.getString(CommonUtil.getResourceId(this, "string", "error"));
               final String returnTxt=this.getString(CommonUtil.getResourceId(this, "string", "return_button_txt"));
               CommonUtil.showAlertDialog(this, error, "請選擇儲值金額", returnTxt, null);
               return;
           }
           params.put("amount", currentSku);
           params.put("type", String.valueOf(PayType.MYCARD_PAY.getType()));
           CommonUtil.showLoadingDialog(this);
           OpenApiService.getInstance(this).createOrder(params, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                CommonUtil.closeLodingDialog();
                JsonUtil json=JsonUtil.newJsonUtil(rspContent);
                MyCardActivity myCardActivity=new MyCardActivity(MycardPayActivity.this);
                myCardActivity.changeCP_TxID(json.getString("orderId"));
                myCardActivity.callUniqueTransaction(Integer.parseInt(currentSku), (Integer.parseInt(currentSku)*2)+GameConfig.GAME_COIN_DESC);
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
            }
        });
       }else if(v.getId() == googlebuy){
           Intent googleIntent=this.getIntent();
           googleIntent.setClass(this, PayActivity.class);
           startActivity(googleIntent);
           finish();
       }else if(v.getId()==back){
           finish();
       }else{
           currentSku=v.getTag().toString().toUpperCase().trim();
           selectTableRow(v);
       }
    }

    private void selectTableRow(View v){
        int rows=payLayout.getChildCount();
        for(int i=0; i<rows; i++){
            TableRow row=(TableRow)payLayout.getChildAt(i);
            row.setBackgroundDrawable(this.getResources().getDrawable(CommonUtil.getResourceId(this, "drawable", "pay_row")));
        }
        v.setBackgroundDrawable(this.getResources().getDrawable(CommonUtil.getResourceId(this, "drawable", "common_button_down")));
    }
    
    public void onResume() {
        super.onResume();
        AnalysisUtil.umengOnResume(this);
    }

    public void onPause() {
        super.onPause();
        AnalysisUtil.umengOnPause(this);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_mycard_pay_billing"));
        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_mycard_pay_billing_portrait"));
        }
    }
}
