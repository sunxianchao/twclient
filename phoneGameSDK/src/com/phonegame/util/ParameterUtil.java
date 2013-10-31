package com.phonegame.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.phonegame.Constants;
import com.phonegame.GameConfig;
import com.phonegame.YunCallbackException;
import com.phonegame.YunOpParams;

public class ParameterUtil {
    
    public static String getSignData(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder content=new StringBuilder();
        // 按照key做排序
        List<String> keys=new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for(int i=0; i < keys.size(); i++) {
            String key=keys.get(i);
            if("sign".equals(key)) {
                continue;
            }
            String value=params.get(key);
            if(value != null) {
                content.append((i == 0 ? "" : "&") + key + "=" + URLEncoder.encode(value, "utf-8"));
            } else {
                content.append((i == 0 ? "" : "&") + key + "=");
            }
        }
        return content.toString();
    }
    
    /**
     * 参数按照添加到map中的顺序进行排序
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String mapToUrlString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb=new StringBuilder();
        boolean isFirst=true;
        List<String> keys=new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for(int i=0; i < keys.size(); i++) {
            String key=keys.get(i);
            String value=params.get(key);
            if(isFirst) {
                if(value != null) {
                    sb.append(key + "=" + URLEncoder.encode(value, "utf-8"));
                } else {
                    sb.append("&" + key + "=");
                }
                isFirst=false;
            } else {
                if(value != null) {
                    sb.append("&" + key + "=" + URLEncoder.encode(value, "utf-8"));
                } else {
                    sb.append("&" + key + "=");
                }
            }
        }
        return sb.toString();
    }

    /**
     * change Map to QueryString
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String generateQueryString(Map params) {
        if(params == null)
            return "";
        StringBuilder aQueryParam=new StringBuilder();
        if(params.size() > 0) {
            List<String> keys=new ArrayList<String>(params.keySet());
            Collections.sort(keys);
            for(int i=0; i < keys.size(); i++) {
                String key=keys.get(i);
                String value=String.valueOf(params.get(key));
                if(value != null) {
                    aQueryParam.append((i == 0 ? "" : "&") + key + "=" + value);
                } else {
                    aQueryParam.append((i == 0 ? "" : "&") + key + "=");
                }
            }
        }
        return aQueryParam.toString();
    }

    /**
     * change Map to QueryJson
     */
    @SuppressWarnings("rawtypes")
    public static String generateQueryJson(Map params) {
        if(params == null)
            return "";
        JSONObject aJsonObject=new JSONObject();
        if(params != null && params.size() > 0) {
            try {
                Set aKeySet=params.keySet();
                Iterator aKeyIterator=aKeySet.iterator();
                while(aKeyIterator.hasNext()) {
                    String aParamName=(String)aKeyIterator.next();
                    aJsonObject.put(aParamName, params.get(aParamName));
                }
            } catch(JSONException e) {
            }
            return aJsonObject.toString();
        } else {
            return "";
        }
    }

    /**
     * 对value进行编码
     * @param value
     * @return
     */
    public static String encode(String value) {
        if(value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8")
            // OAuth encodes some characters differently:
                .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
            // This could be done faster with more hand-crafted code.
        } catch(UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);

        }

    }

    /**
     * 把以‘&’拼接的参数分解到OpSdkParams里
     * @param paramString
     * @return
     */
    public static YunOpParams getParameters(String paramString) {
        if(TextUtils.isEmpty(paramString)) {
            Logger.debug("on getParamesters paramString is null !!");
            return null;
        }
        if(paramString.startsWith("?")) {
            paramString=paramString.substring(1);
        }

        YunOpParams opParam=new YunOpParams();

        try {
            if(paramString != null && !paramString.equals("")) {
                String[] p=paramString.split("&");
                for(String s: p) {
                    if(s != null && !s.equals("")) {
                        if(s.indexOf('=') > -1) {
                            String[] temp=s.split("=");
                            if(temp.length >= 2)
                                opParam.add(temp[0], temp[1]);
                        }
                    }
                }
            }
        } catch(Exception e) {
            Logger.error("on CommonUtil.getParameters exception happened,msg:" + e.getMessage());
        }
        return opParam;
    }

    /**
     * @param scriptName
     * @param params OpenApi的参数列表
     * @param protocol HTTP请求协议 "http" / "https"
     * @return 返回服务器响应内容
     */
    public static String generateQueryString(String actionName, String method, Map<String, String> params) {

        if(params == null) {
            params=new HashMap<String, String>();
        }
        extraParams(params);
        try {
            return mapToUrlString(params);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加系统所需的http扩展参数
     * @param params
     */
    public static void extraParams(Map<String, String> params) {
        YunOpParams configMap=ClientUtil.getConfigMap();
        if(configMap == null || configMap.size() == 0) {
            throw new YunCallbackException(YunCallbackException.INPUT_DATA_ERR, 500, "配置参数错误");
        }
        Iterator<Map.Entry<String, String>> iterator=params.entrySet().iterator();
        try {
            while(iterator.hasNext()) { //
                Map.Entry<String, String> entry=iterator.next();
                String key=entry.getKey();
                String values=entry.getValue();
                if(Arrays.asList("password", "repassword", "oldpassword", "newpassword", "cardno", "cardpwd").contains(key)) {
                    values=DES.encode(values, GameConfig.SECRETKEY);
                    params.put(key, values);
                }
            }
            params.put(Constants.CP_ID, String.valueOf(GameConfig.CP_ID_VALUE));
            params.put(Constants.PROMPT_CHANNEL_ID, configMap.getValue("promptChannel"));
            params.put(Constants.GAME_ID, String.valueOf(GameConfig.GAME_ID));
            params.put(Constants.CURRENT_USER_GAME_ID, configMap.getValue("currentUserGameId"));
            params.put("version", Constants.VERSION);
//            if(!TextUtils.isEmpty(configMap.getValue("token"))){
//                params.put(Constants.TOKEN, configMap.getValue("token"));
//            }
            String signData=getSignData(params);
            String sign=MessageDigestUtil.getMD5(signData + new String(Base64.decode(GameConfig.SECRETKEY)).toString());
            params.put(Constants.SIGN_PARAM_NAME, sign);
        } catch(Exception e) {
            e.printStackTrace();
            throw new YunCallbackException(YunCallbackException.INPUT_DATA_ERR, 501, "签名错误");
        }
    }

}
