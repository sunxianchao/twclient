package com.phonegame;

public class Constants {

    // 日志开关 应用在调试程序期间可以将B_LOG_OPEN设置为true开启日志功能，方便调试，在发布程序前一定把B_LOG_OPEN设置为false，提高程序性能
    private static boolean B_LOG_OPEN=true;

    public static boolean isLogOpen() {
        return B_LOG_OPEN;
    }
    /**
     * 接口签名参数名
     */
    public static final String SIGN_PARAM_NAME="sign";

    public static final String TOKEN="token";
    
    /**
     * 厂商编号参数名
     */
    public static final String CP_ID="cpId";

    /**
     * 推广渠道参数名
     */
    public static final String PROMPT_CHANNEL_ID="prtchid";

    /**
     * 游戏序号
     */
    public static final String GAME_ID="gameId";
    
    /**
     * 充值服务器序号
     */
    public static final String SERVER_ID="serverId";
    
    public static final String CURRENT_USER_GAME_ID="currentUserGameId";
    
    /**
     * 用户id
     */
    public static final String USER_ID="uId";
    
    /**
     * 扩展信息
     */
    public static final String EXT_INFO="ext_info";
    
    /**
     * 支付类型
     */
    public static final String TYPE="type";// 1:INGAME,2:BILLING;3:GOOGLE
    
    public static final String VERSION="1.0.0";
    
}
