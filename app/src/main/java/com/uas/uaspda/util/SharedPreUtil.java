package com.uas.uaspda.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by LiuJie on 2015/12/21.
 */
public class SharedPreUtil {
    public static final String SHAREDPREF_URI = "share_ip_port";
    public static final String KEY_URI = "key_ip_port";
    public static final String KEY_INFO = "key_info";
    //储位缓存
    public static final String KEY_USELOCATION = "key_uselocation";
    //入库单缓存
    public static final String KEY_INMAKE_LOCALPROD = "key_inmake_orders";
    //工单备料，备料单缓存
    public static final String KEY_SCMAKE_PREPARE = "key_scmake_prepare";

    private static SharedPreferences sharedPreUtil = null;


    public static void saveString(Context pContext,String key, String value){
        if(sharedPreUtil == null){
            sharedPreUtil = pContext.getSharedPreferences(SHAREDPREF_URI, 0);
        }
        boolean is = sharedPreUtil.edit().putString(key,value).commit();
    }
    public static void removeString(Context pContext,String key){
        if(sharedPreUtil == null){
            sharedPreUtil = pContext.getSharedPreferences(SHAREDPREF_URI, 0);
        }
        sharedPreUtil.edit().remove(key).commit();
    }
    public static String getString(Context pContext, String key){
        if(sharedPreUtil == null){
            sharedPreUtil = pContext.getSharedPreferences(SHAREDPREF_URI, 0);
        }
        return sharedPreUtil.getString(key,null);
    }


    private SharedPreUtil(){}
}
