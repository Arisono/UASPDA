package com.uas.uaspda.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @note:入库单待采集信息
 */
public class ProductIn implements Serializable{
    int totalCount;
    List<TargetItem> target;

    public ProductIn(int totalCount, List target) {
        this.totalCount = totalCount;
        this.target = target;
    }

    @Override
    public String toString() {
        return "ProductIn{" +
                "totalCount=" + totalCount +
                ", target=" + target +
                '}';
    }

    public static class TargetItem {
        String enauditstatus;
        String pi_class;
        String pi_inoutno;
        String pi_title;
        String pi_cardcode;
        int pi_id;
        String pi_whname;
        String pi_whcode;
        String pi_statuscode;
        BarcodeSet barcodeset;
        List<Product> product;

        public TargetItem(String enauditstatus, String pi_class, String pi_inoutno, String pi_title, String pi_cardcode, int pi_id, String pi_whname, String pi_whcode, String pi_statuscode, BarcodeSet barcodeset, List<Product> product) {
            this.enauditstatus = enauditstatus;
            this.pi_class = pi_class;
            this.pi_inoutno = pi_inoutno;
            this.pi_title = pi_title;
            this.pi_cardcode = pi_cardcode;
            this.pi_id = pi_id;
            this.pi_whname = pi_whname;
            this.pi_whcode = pi_whcode;
            this.pi_statuscode = pi_statuscode;
            this.barcodeset = barcodeset;
            this.product = product;
        }



        public static class BarcodeSet {
            long bsId;
            String bsType;
            String bsLenprid;

            public BarcodeSet(long bsId, String bsType, String bsLenprid) {
                this.bsId = bsId;
                this.bsType = bsType;
                this.bsLenprid = bsLenprid;
            }

            public long getBsId() {
                return bsId;
            }

            public String getBsType() {
                return bsType;
            }

            public String getBsLenprid() {
                return bsLenprid;
            }
        }

        public static class Product {
            long pdId;
            int pdInqty;
            String pdOrderCode;
            int pdPdno;
            String pdProdcode;
            String piInoutno;
            String prDetail;
            long prId;
            String prIfBarcodeCheck;
            String prLocation;
            String prSpec;
            int prZxbzs;

            public Product(long pdId, int pdInqty, String pdOrderCode, int pdPdno, String pdProdcode, String piInoutno, String prDetail, long prId, String prIfBarcodeCheck, String prLocation, String prSpec, int prZxbzs) {
                this.pdId = pdId;
                this.pdInqty = pdInqty;
                this.pdOrderCode = pdOrderCode;
                this.pdPdno = pdPdno;
                this.pdProdcode = pdProdcode;
                this.piInoutno = piInoutno;
                this.prDetail = prDetail;
                this.prId = prId;
                this.prIfBarcodeCheck = prIfBarcodeCheck;
                this.prLocation = prLocation;
                this.prSpec = prSpec;
                this.prZxbzs = prZxbzs;
            }

            public void setPdId(long pdId) {
                this.pdId = pdId;
            }

            public void setPdInqty(int pdInqty) {
                this.pdInqty = pdInqty;
            }

            public void setPdOrderCode(String pdOrderCode) {
                this.pdOrderCode = pdOrderCode;
            }

            public void setPdPdno(int pdPdno) {
                this.pdPdno = pdPdno;
            }

            public void setPdProdcode(String pdProdcode) {
                this.pdProdcode = pdProdcode;
            }

            public void setPiInoutno(String piInoutno) {
                this.piInoutno = piInoutno;
            }

            public void setPrDetail(String prDetail) {
                this.prDetail = prDetail;
            }

            public void setPrId(long prId) {
                this.prId = prId;
            }

            public void setPrIfBarcodeCheck(String prIfBarcodeCheck) {
                this.prIfBarcodeCheck = prIfBarcodeCheck;
            }

            public void setPrLocation(String prLocation) {
                this.prLocation = prLocation;
            }

            public void setPrSpec(String prSpec) {
                this.prSpec = prSpec;
            }

            public void setPrZxbzs(int prZxbzs) {
                this.prZxbzs = prZxbzs;
            }
        }

        public String getEnauditstatus() {
            return enauditstatus;
        }

        public String getPi_class() {
            return pi_class;
        }

        public String getPi_inoutno() {
            return pi_inoutno;
        }

        public String getPi_title() {
            return pi_title;
        }

        public String getPi_cardcode() {
            return pi_cardcode;
        }

        public int getPi_id() {
            return pi_id;
        }

        public String getPi_whname() {
            return pi_whname;
        }

        public String getPi_whcode() {
            return pi_whcode;
        }

        public String getPi_statuscode() {
            return pi_statuscode;
        }

        public BarcodeSet getBarcodeset() {
            return barcodeset;
        }

        public List<Product> getProduct() {
            return product;
        }
    }

    public int getTotalCount() {
        return totalCount;
    }

    public List<TargetItem> getTarget() {
        return target;
    }
}
