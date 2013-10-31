/**
 * $Id: RespondCode.java,v 1.1 2012/09/10 10:36:25 xianchao.sun Exp $
 */
package com.phonegame;

/**
 * @author xianchao.sun@downjoy.com
 */
public class RespondCode {

    public static final int SUCCESS=1;

    public static final int ERROR=0;

    public static final int CANCLE=1000;// 取消操作

    public static final int INIT_SDK_ERROR=1001;// "SDK 初始化失败！"

    public static final int ERROR_EXCEPTION=1002;// 程序调用出现异常终止

    public static final int NEED_LOGIN=1003; // 该操作需要登录

    public static final int EXIT=1004;// 退出操作
    
    public static final int UPDATE=1005; //普通更新
    
    public static final int FORCE_UPDATE=1006; // 强制更新客户端，需要关闭游戏
}
