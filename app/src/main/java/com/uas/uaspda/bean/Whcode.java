package com.uas.uaspda.bean;

/**
 * @note:详细账单类
 * @param:pdWhcode:仓库编号
 * @param:piClass:出入库单类型
 * @param:piInoutno:出入库单号
 * @param:piId
 */
public class Whcode {
    private String piInoutno;
    private String piClass;
    private String pdWhcode;
    private String piId;

    public Whcode(){}
    public Whcode(String piInoutno,String piClass,String pdWhcode,String piId) {
        this.pdWhcode = pdWhcode;
        this.piClass = piClass;
        this.piInoutno = piInoutno;
        this.piId = piId;
    }

    public String getPdWhcode() {
        return pdWhcode;
    }

    public String getPiClass() {
        return piClass;
    }

    public String getPiInoutno() {
        return piInoutno;
    }

    public String getPiId() {
        return piId;
    }
}
