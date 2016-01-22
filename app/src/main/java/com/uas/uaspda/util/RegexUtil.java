package com.uas.uaspda.util;

/**
 * @注释：字符串匹配工具类
 */
public class RegexUtil {

    private static RegexUtil regexUtil = null;

    //正则表达式
    public static String IP_FORMAT = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
    public static String PORT_FORMAT = "^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)";
   // public static String PORT_FORMAT = "(\\d)+";

    public static  boolean checkString(String str, String regx) {
        if(regexUtil == null){
            regexUtil = new RegexUtil();
        }

        boolean right = false;
        if (str.matches(regx)) {
            right = true;
        }
        return right;
    }


    private RegexUtil(){}

}
