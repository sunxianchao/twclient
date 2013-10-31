package com.phonegame.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.phonegame.GameConfig;
import com.phonegame.YunCallbackException;
import com.phonegame.YunCallbackHandler;
import com.phonegame.app.Application;
import com.phonegame.service.OpenApiService;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.Logger;

/**
 * A sample application that demonstrates in-app billing.
 */
public class GooglePlay extends Activity {

    private static final String TAG="google";

    private Activity activity;

    private IabHelper mHelper; // Google Play Helper

    // 必须定义payload，且区分其它应用的payload
    private String payload="com.phonegame";

    private String base64EncodedPublicKey;

    // 商品列表
    private ArrayList<String> list;

    // Does the user have the premium upgrade?
    // 是否是“高级装备”红色车
    boolean mIsPremium=false;

    // Does the user have an active subscription to the infinite gas plan?
    // 是否订阅“无限气”
    boolean mSubscribedToInfiniteGas=false;

    private String currentSku;

    private String orderId;
    
    private SkuDetails skuDetails;

    // (arbitrary) request code for the purchase flow
    public static final int RC_REQUEST=10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        currentSku=this.getIntent().getStringExtra("currentSku");
        orderId=this.getIntent().getStringExtra("orderId");
        initData();
        Application.getInstance().addActivity("googlePlay", this);
        Logger.sysLog(currentSku);
        Logger.debug(orderId);
    }

    private void initData() {
        activity=this;
        base64EncodedPublicKey=GameConfig.GOOGLE_PUBLISH_BASE64_KEY;

        // 开始初始化
        Log.d(TAG, "创建IAB helper...");
        mHelper=new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(true);
        Log.d(TAG, "开始初始化数据...");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {// 检查是否有billig权限并能否进入play商店，如成功获取商品列表

                public void onIabSetupFinished(IabResult result) {
                    Log.d(TAG, "初化完成.");
                    if(!result.isSuccess()) {
                        // Oh noes, there was a problem.
                        complain(getString(CommonUtil.getResourceId(activity, "string", "setup_isSuccess")) + result, 0);
                        return;
                    }

                    Log.d(TAG, "初始化in-app biling成功. 查询我们已购买的物品..");
                    list=new ArrayList<String>();
                    list.add(currentSku);
                    mHelper.queryInventoryAsync(mGotInventoryListener, list);
                }
            });

    }

    // 查询库存（inventory）完成接口
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener=new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "查询操作完成");
            if(result.isFailure()) {
                complain(getString(CommonUtil.getResourceId(activity, "string", "query_inventory")) + result, 0);
                return;
            }
            Log.d(TAG, "查询成功！");
            // 因为SKU_GAS是可重复购买的产品，查询我们的已购买的产品，
            // 如果当中有SKU_GAS，我们应该立即消耗它，以方便下次可以重复购买。
            skuDetails=inventory.getSkuDetails(currentSku);// 查询消耗品详情
            Log.d(TAG, "商品详情：" + skuDetails);
            Purchase gasPurchase=inventory.getPurchase(currentSku);
            if(gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Logger.debug(TAG, "属于SKU_GAS" + currentSku);
                mHelper.consumeAsync(gasPurchase, mConsumeFinishedListener);
                return;
            }
            Log.d(TAG, "查询完成; 接下来可以操作UI线程了..");
            toBilling(currentSku);

        }
    };

    public void toBilling(String sku) {
        Log.d(TAG, "点击购买“SKU_GAS”按钮.");
        Log.d(TAG, "执行购买“SKU_GAS”方法：launchPurchaseFlow()");
        // 购买金币
        if(list.contains(sku)) {
            mHelper.launchPurchaseFlow(activity, sku, RC_REQUEST, mPurchaseFinishedListener, payload);
        } else {
            alert("未找到產品id：" + sku, 0);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Logger.debug("onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if(!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            // 如果助手类没有处理该结果，则要自己手动处理
            // 写处理代码 ...
        } else {
            Log.d(TAG, "onActivityResult结果已被IABUtil处理.");
        }
    };

    /*
     * Callback for when a purchase is finished 购买成功处理
     */
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener=new IabHelper.OnIabPurchaseFinishedListener() {

        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Logger.debug("购买操作完成: " + result + ", 购买的产品: " + purchase);
            int response=result.getResponse();
            if(result.isFailure()) {
                complain(getString(CommonUtil.getResourceId(activity, "string", "purchase_failure")), 0);
                return;
            }
            if(!verifyDeveloperPayload(purchase)) {
                complain(getString(CommonUtil.getResourceId(activity, "string", "purchase_failure_verify")), 0);
                return;
            }

            Log.d(TAG, "购买成功.");
            for(String sku: list) {
                if(purchase.getSku().equals(sku)) {
                    Log.d(TAG, "购买的产品是金币， 执行消耗操作。");
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                }
            }

        }
    };

    /*
     * Called when consumption is complete 如果是消耗品的话 需要调用消耗方法
     */
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener=new IabHelper.OnConsumeFinishedListener() {

        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Logger.debug("消耗操作完成. 产品为: " + purchase + ", 结果: " + result);
            // 如果有多个消耗产品，应该在这里一一检查。这里只有一个消耗产品，所以不检查
            CommonUtil.showLoadingDialog(getApplicationContext());
            if(result.isSuccess()) {
                Map<String, String> params=new HashMap<String, String>();
                params.put("signeddata", purchase.getOriginalJson());
                params.put("signature", purchase.getSignature());
                params.put("orderid", orderId);
                params.put("tradeno", purchase.getOrderId());
                params.put("status", String.valueOf(purchase.getPurchaseState()));
                params.put("productid", skuDetails.getSku());
                OpenApiService.getInstance(getApplicationContext()).googleBillingUpdate(params, new YunCallbackHandler() {
                    
                    @Override
                    public void onSuccess(String rspContent, int statusCode) {
                        complain(getString(CommonUtil.getResourceId(activity, "string", "purchase_success")), 200);
                        Log.d(TAG, "消耗成功！");
                    }
                    
                    @Override
                    public void onFailure(YunCallbackException exp) {
                        complain(getString(CommonUtil.getResourceId(activity, "string", "purchase_exception")), 500);
                    }
                });
            } else {
                complain(getString(CommonUtil.getResourceId(activity, "string", "consume_failure")) + result, 0);
            }
        }
    };

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        Logger.debug("p.getDeveloperPayload(): " + p.getDeveloperPayload());
        if(payload.equalsIgnoreCase(p.getDeveloperPayload())) {
            return true;
        }

        return false;
    }

    void complain(String message, int code) {
        Log.e(TAG, "**** complain: " + message);
        alert(message, code);
    }

    void alert(String message, final Integer code) {
        AlertDialog.Builder bld=new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("確定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(code == 200){
                    Application.getInstance().exit();
                }else{
                    finish();
                }
            }
        });
        Log.d(TAG, "弹出错误框: " + message);
        bld.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHelper != null) {
            mHelper.dispose();
            mHelper=null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
