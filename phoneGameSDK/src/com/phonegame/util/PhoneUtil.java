package com.phonegame.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneUtil {

    private static PhoneUtil instance;

    public static PhoneUtil getInstance() {
        if(instance == null) {
            instance=new PhoneUtil();
        }
        return instance;
    }

    public String getLocalIpAddress() {
        try {
            for(Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf=en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr=intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress=enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        return inetAddress.getHostAddress().toString() + GetNetIp("http://fw.qq.com/ipaddress");
                    }
                }
            }
        } catch(SocketException e) {
            Log.e("WifiPreference IpAddress", e.toString());
        }
        return null;
    }

    String GetNetIp(String ipaddr) {
        URL infoUrl=null;
        InputStream inStream=null;
        try {
            infoUrl=new URL(ipaddr);
            URLConnection connection=infoUrl.openConnection();
            HttpURLConnection httpConnection=(HttpURLConnection)connection;
            int responseCode=httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK) {
                inStream=httpConnection.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber=new StringBuilder();
                String line=null;
                while((line=reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                return strber.toString();
            }
        } catch(MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public String getLocalMacAddress(Context ctx) {
        try {
            WifiManager wifi=(WifiManager)ctx.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info=wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getTelePhoneNumber(Context ctx) {
        try {
            TelephonyManager phoneMgr=(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
            return phoneMgr.getLine1Number();
        } catch(Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String getNetType(Context ctx) {
        try {
            ConnectivityManager connectivityManager=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo=connectivityManager.getActiveNetworkInfo();
            return activeNetInfo.getTypeName();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if(info != null) {
                for(int i=0; i < info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void netWorkStatusAndSetting(final Context context) {
        Builder b=new AlertDialog.Builder(context).setTitle("没有可用的网络").setMessage("是否对网络进行设置？");
        b.setPositiveButton("是", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Intent mIntent=new Intent("/");
                ComponentName comp=new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
                mIntent.setComponent(comp);
                mIntent.setAction("android.intent.action.VIEW");
                if(android.os.Build.VERSION.SDK_INT > 10) {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                } else {
                    context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
                System.exit(0);
            }

        }).setNeutralButton("否", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                System.exit(0);
            }
        }).show();

    }

    public String getLocation(Context ctx) {
        LocationManager m_location_manager=(LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        Location lm=m_location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return lm.toString();
    }

    public String getDeviceId(Context ctx) {
        TelephonyManager telephonyManager=(TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String imei=telephonyManager.getDeviceId();
        return imei;
    }
}
