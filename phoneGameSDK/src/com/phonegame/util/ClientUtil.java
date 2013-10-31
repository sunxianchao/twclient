/**
 * $Id: ClientUtil.java,v 1.2 2012/09/10 10:36:25 xianchao.sun Exp $
 */
package com.phonegame.util;

import com.phonegame.YunOpParams;

/**
 * @author xianchao.sun@downjoy.com
 */
public class ClientUtil {
    
//    private static String SERVICE_DOMAIN="http://192.168.5.12:8081";
//
//    private static String PAY_DOMAIN="http://192.168.5.12:7031";
    private static String SERVICE_DOMAIN="http://member.phonegame.com.tw";
    
    private static String PAY_DOMAIN="http://newpay.phonegame.com.tw";

    private static final String SERVICE_URL=SERVICE_DOMAIN + "/service/";
    
    private static final String PAY_SERVICE_URL=PAY_DOMAIN + "/service/";

    private static final String COMMON_URL=SERVICE_DOMAIN + "/common/";

    public static final String GAME_INIT_URL= SERVICE_URL + "game/init";
    
    public static final String GAME_SERVER_LOGIN_URL= SERVICE_URL + "game/loginserver";
    
    public static final String USER_LOGIN_URL= SERVICE_URL +"user/login";
    
    public static final String OTHER_USER_LOGIN_URL= SERVICE_URL +"user/otherlogin";
    
    public static final String USER_REGISTER_URL = SERVICE_URL + "user/register";
    
    public static final String USER_FIND_PASSWORD_URL = COMMON_URL + "user/findpwd";
    
    public static final String USER_CHANGE_PASSWORD_URL = COMMON_URL + "user/changepwd";
    
    public static final String CREATE_ORDER_URL = PAY_SERVICE_URL + "order/create";
    
    public static final String GOOGLE_BILLING_UPDATE = PAY_SERVICE_URL + "order/googlebillingupdate";

    public static final String USER_HEART_BEAT_URL=SERVICE_URL + "user/heartbeat";
    
    public static final String USER_LOGOUT_BEAT_URL=SERVICE_URL + "user/logout";
    
    private static final YunOpParams configMap=new YunOpParams();

    /**
     * 新的sdk设置参数
     * @param paramsMap
     */
    public static void setConfigMap(YunOpParams paramsMap) {
        // 设置厂商传过来的参数
        for(String key: paramsMap.getKeys()) {
            ClientUtil.configMap.add(key, paramsMap.getValue(key));
        }
//        // 设置sdk基本参数，这些基本参数都通过GameConfi设置，不需要厂商传过来，因此在给开发商包的时候请确保这些参数的正确设置
//        configMap.add("cpId", String.valueOf(GameConfig.CP_ID_VALUE));
//        configMap.add("gameSeq", String.valueOf(GameConfig.GAME_SEQNUM));
//        configMap.add("secretKey", GameConfig.SECRETKEY);
    }
 
    public static YunOpParams getConfigMap() {
        return configMap;
    }
}
