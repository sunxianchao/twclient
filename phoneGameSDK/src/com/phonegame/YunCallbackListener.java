package com.phonegame;

public class YunCallbackListener {

    public static ResultListener resultListener;

    public static abstract interface ResultListener {

        public abstract void callback(String code);
    }

    public static void setResultListener(ResultListener resultListener) {
        YunCallbackListener.resultListener=resultListener;
    }

    public static void resultCallback(String code) {
        if(resultListener != null) {
            resultListener.callback(code);
            resultListener=null;
        }
    }
}
