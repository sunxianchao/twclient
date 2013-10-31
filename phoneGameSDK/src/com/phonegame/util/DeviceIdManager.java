package com.phonegame.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


public class DeviceIdManager {

    public static String getDeviceId(Context context){
        String deviceId= null;
        TelephonyManager telephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyMgr != null){
            deviceId = telephonyMgr.getDeviceId(); // Requires READ_PHONE_STATE
        }
        if(TextUtils.isEmpty(deviceId)){
            WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            deviceId = wm.getConnectionInfo().getMacAddress();
        }
        if(TextUtils.isEmpty(deviceId)){
            deviceId = "35" + //we make this look like a valid IMEI
                    Build.BOARD.length()%10+ Build.BRAND.length()%10 + 
                    Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 + 
                    Build.DISPLAY.length()%10 + Build.HOST.length()%10 + 
                    Build.ID.length()%10 + Build.MANUFACTURER.length()%10 + 
                    Build.MODEL.length()%10 + Build.PRODUCT.length()%10 + 
                    Build.TAGS.length()%10 + Build.TYPE.length()%10 + 
                    Build.USER.length()%10 ;
        }
        return deviceId;
    }
    
    public static String getMacAddress(Context context){
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        String macaddress=wm.getConnectionInfo().getMacAddress();
        return macaddress;
    }
}
