package com.phonegame.network;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.phonegame.YunOpParams;
import com.phonegame.util.ClientUtil;
import com.phonegame.util.CommonUtil;
import com.phonegame.util.HttpUtil;
import com.phonegame.util.JsonUtil;
import com.phonegame.util.Logger;
import com.phonegame.util.ParameterUtil;
import com.phonegame.util.PostImageUtility;

/**
 * @author xianchao.sun@downjoy.com 
 * http异步通讯
 */
public class AsyncHttpConnection implements Runnable {

    public static final int DID_START=0;

    public static final int DID_ERROR=1;

    public static final int DID_SUCCEED=2;

    public static final int DID_OAUTH_ERROR=3;

    private static final int GET_METHOD_INDEX=0;

    private static final int POST_METHOD_INDEX=1;

    private static final int POST_JSON_METHOD_INDEX=2; // //post json数据

    public static final String GET_METHOD="get";

    public static final String POST_METHOD="post";// post非json数据

    public static final String POST_JSON_METHOD="post_json";// post json数据

    public static final int GET_SUCCEED_STATUS=200; // 请求成功

    public static final int POST_SUCCEED_STATUS=201; // 上传数据成功

    public static final int PROCESSING_SUCCEED_STATUS=202; // 请求已经接受但是还没处理

    public static final int OAUTH_ERROR_STATUS=400; // 签名出错
    
    public static final String OAUTH_ERROR="auth error"; // 签名出错

    private String url;

    private int method;

    private Handler handler;

    private String strData;

    private Map<String, File> postFile;

    private HttpClient httpClient;

    public AsyncHttpConnection(Handler _handler) {
        this.handler=_handler;
    }

    public void create(int method, String url, String data, Map<String, File> files) {
        this.method=method;
        this.url=url;
        this.strData=data;
        this.postFile=files;
        AsyncHttpConnectionManager.getInstance().submit(this);
    }

    public void get(String url) {
        create(GET_METHOD_INDEX, url, null, null);
    }

    public void post(String url, String data) {
        create(POST_METHOD_INDEX, url, data, null);
    }

    public void post(String url, String data, Map<String, File> files) {
        create(POST_METHOD_INDEX, url, data, files);
    }

    public void postJson(String url, String data) {
        create(POST_JSON_METHOD_INDEX, url, data, null);
    }

    public void run() {
        if(handler != null) {
            handler.sendMessage(Message.obtain(handler, AsyncHttpConnection.DID_START));
        } else {
            Logger.debug("AsyncHttpConnection_run()", "Could not call handler to post DID_START message because it was null.");
        }

        httpClient=new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), HttpUtil.getTimeoutForHTTPConnection());
        try {
            HttpResponse response=null;
            Logger.debug("HttpUrl=" + url);
            
            switch(method) {
                case GET_METHOD_INDEX: {
                    HttpGet httpGet=new HttpGet(url);
                    if(!TextUtils.isEmpty(ClientUtil.getConfigMap().getValue("token"))){
                        httpGet.setHeader("token", ClientUtil.getConfigMap().getValue("token"));
                    }
                    response=httpClient.execute(httpGet);
                    break;
                }
                case POST_METHOD_INDEX: {
                    HttpPost httpPost=new HttpPost(url);
                    Logger.debug("Http post strData=" + strData);
                    if(!TextUtils.isEmpty(ClientUtil.getConfigMap().getValue("token"))){
                        httpPost.setHeader("token", ClientUtil.getConfigMap().getValue("token"));
                    }
                    if(postFile == null || postFile.size() <= 0) { // 没有post文件
                        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
                        httpPost.setEntity(new StringEntity(strData, "utf-8"));
                    } else {
                        httpPost.setHeader("Content-Type", "multipart/form-data" + "; boundary=" + PostImageUtility.BOUNDARY);
                        ByteArrayOutputStream bos=null;
                        bos=new ByteArrayOutputStream(100 * 1024);
                        YunOpParams opParam=null;
                        opParam=ParameterUtil.getParameters(strData);
                        PostImageUtility.paramToUpload(bos, opParam);
                        PostImageUtility.imageContentToUpload(bos, postFile);
                        byte temp[]=bos.toByteArray();
                        ByteArrayEntity formEntity=new ByteArrayEntity(temp);
                        httpPost.setEntity(formEntity);
                    }
                    // 关闭Expect:100-Continue握手
                    httpPost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
                    response=httpClient.execute(httpPost);

                    break;
                }
                case POST_JSON_METHOD_INDEX: {
                    HttpPost httpPost=new HttpPost(url);
                    Logger.debug("Http post json strData= " + strData);
                    httpPost.setHeader("Content-Type", "multipart/form-data");
                    if(!TextUtils.isEmpty(ClientUtil.getConfigMap().getValue("token"))){
                        httpPost.setHeader("token", ClientUtil.getConfigMap().getValue("token"));
                    }
                    httpPost.setHeader("Content-Type", "multipart/form-data");
                    httpPost.setEntity(new StringEntity(strData, "utf-8"));
                    // 关闭Expect:100-Continue握手
                    httpPost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
                    response=httpClient.execute(httpPost);
                    break;
                }
            }

            if(method <= POST_JSON_METHOD_INDEX) {
                Header header=response.getFirstHeader("token");
                if(header != null && !TextUtils.isEmpty(header.getValue())){
                    ClientUtil.getConfigMap().add("token", header.getValue());
                }
                processEntity(response.getEntity(), response.getStatusLine().getStatusCode());
            }

        } catch(Exception e) {
            if(handler != null) {
                e.printStackTrace();
                Message message=Message.obtain(handler, DID_ERROR, e);
                handler.sendMessage(message);

            } else {
                Logger.debug("AsyncHttpConnection_run()", "handler post DID_ERROR because it was null.");
                Logger.debug("AsyncHttpConnection_run()", e.toString());
            }
        }
    }

    /**
     * 读取返回结果
     * @param entity
     * @param statusCode
     * @throws IllegalStateException
     * @throws IOException
     * @throws JSONException 
     */
    private void processEntity(HttpEntity entity, int statusCode) throws IllegalStateException, IOException, JSONException {
        BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result="";
        StringBuilder sBuilder=new StringBuilder(result);
        while((line=br.readLine()) != null) {
            sBuilder.append(line);
        }
        result=sBuilder.toString();

        String aResultArray[]={String.valueOf(statusCode), result};
        Logger.debug("statusCode = " + aResultArray[0] + ",result = " + aResultArray[1]);

        Message message;
        if(statusCode != GET_SUCCEED_STATUS && statusCode != POST_SUCCEED_STATUS) { // 认证出错
            message=Message.obtain(handler, DID_ERROR, aResultArray);
        } else {
            if(TextUtils.isEmpty(result)){
                return;
            }
            boolean isSuccess=JsonUtil.newJsonUtil(aResultArray[1]).getBoolean("success");
            if(isSuccess){
                String businessResult=JsonUtil.newJsonUtil(aResultArray[1]).getString("businessResult");
                aResultArray[1]=businessResult;
                message=Message.obtain(handler, DID_SUCCEED, aResultArray);
            }else{
                String errorMsg=JsonUtil.newJsonUtil(aResultArray[1]).getString("errorMsg");
                message=Message.obtain(handler, DID_ERROR, errorMsg);
            }
        }
        if(handler != null) {
            handler.sendMessage(message);
        } else {
            Logger.debug("handler was null.");
        }
        br.close();
    }

    
}
