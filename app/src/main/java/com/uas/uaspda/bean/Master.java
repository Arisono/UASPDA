package com.uas.uaspda.bean;

/**
 * @param :maId       账套ID
 * @param :maFunction 账套信息
 * @param ：maName     账套编号
 * @注释：账套类
 */
public class Master {
    private String maId;
    private String maFunction;
    private String maName;

    /*构造函数*/
    public Master() {}

    public Master(String pMaId, String pMaName, String pMaFunction) {
        maId = pMaId;
        maFunction = pMaFunction;
        maName = pMaName;
    }


    public String getMaName() {
        return maName;
    }

    public String getMaFunction() {
        return maFunction;
    }

    public String getMaId() {

        return maId;
    }
}
