package com.phonegame.to;


public enum PayType {

    GOOGLE_PLAY(3), MYCARD_PAY(4);
    
    private int type;
    
    private PayType(int type){
        this.type=type;
    }
    
    public int getType(){
        return type;
    }
}
