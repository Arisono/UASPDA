package com.uas.uaspda.bean;

/**
 * @note:工单备料:待采集订单
 */
public class UnCollect {
    String md_repcode;
    int md_baseqty;
    String pr_wiplocation;
    int md_qty;
    String md_prodcode;
    String md_location;
    String md_lastlocation;
    int md_needqty;
    String pr_detail;
    String pr_spec;
    int mp_id;
    int md_detno;
    int md_id;



    @Override
    public String toString() {
        return "UnCollect{" +
                "md_repcode='" + md_repcode + '\'' +
                ", md_baseqty=" + md_baseqty +
                ", pr_wiplocation='" + pr_wiplocation + '\'' +
                ", md_qty=" + md_qty +
                ", md_prodcode='" + md_prodcode + '\'' +
                ", md_location='" + md_location + '\'' +
                ", md_lastlocation='" + md_lastlocation + '\'' +
                ", md_needqty=" + md_needqty +
                ", pr_detail='" + pr_detail + '\'' +
                ", pr_spec='" + pr_spec + '\'' +
                ", mp_id=" + mp_id +
                ", md_detno=" + md_detno +
                ", md_id=" + md_id +
                '}';
    }

    public String getMd_repcode() {
        return md_repcode;
    }

    public int getMd_baseqty() {
        return md_baseqty;
    }

    public String getPr_wiplocation() {
        return pr_wiplocation;
    }

    public int getMd_qty() {
        return md_qty;
    }

    public String getMd_prodcode() {
        return md_prodcode;
    }

    public String getMd_location() {
        return md_location;
    }

    public String getMd_lastlocation() {
        return md_lastlocation;
    }

    public int getMd_needqty() {
        return md_needqty;
    }

    public String getPr_detail() {
        return pr_detail;
    }

    public String getPr_spec() {
        return pr_spec;
    }

    public int getMp_id() {
        return mp_id;
    }

    public int getMd_detno() {
        return md_detno;
    }

    public int getMd_id() {
        return md_id;
    }
}
