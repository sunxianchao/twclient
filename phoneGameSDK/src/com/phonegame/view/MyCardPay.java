package com.phonegame.view;

import java.util.Map;

import tw.com.mycard.mycardsdk.MyCardActivity;
import android.app.Activity;
import android.content.Context;

import com.phonegame.GameConfig;
import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.service.OpenApiService;
import com.phonegame.to.PayType;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.JsonUtil;


public class MyCardPay {

    private static MyCardPay instance;
    
    private MyCardActivity myCardActivity;
    
    private static Context ctx;
    
    private MyCardPay(){
    }
    
    public static MyCardPay getInstance(Context ctx){
        if(instance == null){
            instance = new MyCardPay();
        }
        MyCardPay.ctx=ctx;
        return instance;
    }

    public MyCardActivity getMyCardActivity() {
        return myCardActivity=new MyCardActivity((Activity)ctx);
    }
    
    public void doPay(final Map<String, String> params){
        params.put("type", String.valueOf(PayType.MYCARD_PAY.getType()));
        CommonUtil.showLoadingDialog(ctx);
        OpenApiService.getInstance(MyCardPay.ctx).createOrder(params, new YunCallbackHandler() {
            @Override
            public void onSuccess(String rspContent, int statusCode) {
                CommonUtil.closeLodingDialog();
                JsonUtil json=JsonUtil.newJsonUtil(rspContent);
                String amount="1";
                myCardActivity=getMyCardActivity();
                myCardActivity.changeCP_TxID(json.getString("orderId"));
                myCardActivity.callUniqueTransaction(Integer.parseInt(amount), (Integer.parseInt(amount)*2)+GameConfig.GAME_COIN_DESC);
            }
            
            @Override
            public void onFailure(YunCallbackException exp) {
                CommonUtil.closeLodingDialog();
            }
        });
    }

}
