package com.uas.uaspda.util;

/**
 * Created by LiuJie on 2015/12/21.
 */
public class StringUtil {
    private static final String SPLIT = "//:UAS://";
    private static StringUtil stringUtil = null;
    private StringUtil(){}

  //  String cookies = "JSESSIONID=849C6F39A1EF83C487C248A3193BB152; Path=/ERP/; HttpOnly";
    public static String splitCookieString(String pCookie){
        String[] str = pCookie.split(";");
        String cookie = str[0]+";";
        return cookie;
    }

    //连接字符串
    public static String jointString(String oldUri,String newIp, String newPort){
        String result = null;
        if(oldUri == null){
            return (newIp+SPLIT+newPort);
        }
        return (oldUri+SPLIT+newIp+SPLIT+newPort);
    }

    //分割字符串
    public static String[] splitString(String str){
        String[] result = null;
        result = str.split(SPLIT);
        return result;
    }
    public String joinString(String [] str){
        String result = null;
        return result;
    }
}
