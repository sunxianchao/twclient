package com.phonegame.view;

import java.util.HashMap;
import java.util.Map;

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
import com.phonegame.google.GooglePlay;
import com.phonegame.service.OpenApiService;
import com.phonegame.to.PayType;
import com.phonegame.util.AnalysisUtil;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.JsonUtil;


public class PayActivity extends Activity implements OnClickListener{

    private int submitPay;
    
    private int morePay;
    
    private int back;

    private String currentSku;
    
    private TableLayout payLayout;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_pay_billing"));
        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_pay_billing_portrait"));
        }
        Application.getInstance().addActivity("googleActivity", this);
        submitPay=CommonUtil.getResourceId(this, "id", "buy");
        morePay=CommonUtil.getResourceId(this, "id", "more_pay_btn");
        back=CommonUtil.getResourceId(this, "id", "close_btn");
        findViewById(submitPay).setOnClickListener(this);
        findViewById(morePay).setOnClickListener(this);
        findViewById(back).setOnClickListener(this);
        payLayout=(TableLayout)findViewById(CommonUtil.getResourceId(this, "id", "tblLayout"));
        int rows=payLayout.getChildCount();
        for(int i=0; i<rows; i++){
            TableRow row=(TableRow)payLayout.getChildAt(i);
            row.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
       final Intent intent=this.getIntent();
       Map<String, String> params=new HashMap<String, String>();
       Bundle extData=intent.getExtras().getBundle("orderParams");
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
           params.put("productId", currentSku);
           params.put("type", String.valueOf(PayType.GOOGLE_PLAY.getType()));
           CommonUtil.showLoadingDialog(this);
           OpenApiService.getInstance(this).createOrder(params, new YunCallbackHandler() {
            
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                CommonUtil.closeLodingDialog();
                JsonUtil json=JsonUtil.newJsonUtil(rspContent);
                intent.setClass(PayActivity.this, GooglePlay.class);
                intent.putExtra("currentSku", currentSku);
                intent.putExtra("orderId", json.getString("orderId"));
                startActivity(intent);
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
            }
        });
       }else if(v.getId()==morePay){
           intent.setClass(PayActivity.this, MycardPayActivity.class);
           startActivity(intent);
           finish();
       }else if(v.getId()==back){
           finish();
       }else{
           String tag=v.getTag().toString().toUpperCase().trim();
           currentSku=getCurrentSKU(tag);
           selectTableRow(v);
       }
    }
    
    private String getCurrentSKU(String tag){
        String sku=null;
        try {
            sku=GameConfig.class.getDeclaredField("GOOGLE_PLAY_PAY_"+tag).get(new GameConfig()).toString();
        } catch(Exception e) {
            e.printStackTrace();
        } 
        return sku;
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
    
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_pay_billing"));
        } else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(CommonUtil.getResourceId(this, "layout", "activity_pay_billing_portrait"));
        }
    }
}
