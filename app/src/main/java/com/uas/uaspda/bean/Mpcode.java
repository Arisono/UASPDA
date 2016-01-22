package com.uas.uaspda.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 打开工单备料：采集界面自动Loading出的数据
 */
public class Mpcode {

    boolean success;
    List<Message> message;

    public class Message implements Serializable{
        boolean mp_mccode;
        String mp_code;
        String mp_makecode;
        boolean mp_whcode;
        int mp_id;
        String mp_linecode;

        @Override
        public String toString() {
            return "Message{" +
                    "mp_mccode=" + mp_mccode +
                    ", mp_code='" + mp_code + '\'' +
                    ", mp_makecode='" + mp_makecode + '\'' +
                    ", mp_whcode=" + mp_whcode +
                    ", mp_id=" + mp_id +
                    ", mp_linecode='" + mp_linecode + '\'' +
                    '}';
        }

        public boolean isMp_mccode() {
            return mp_mccode;
        }

        public String getMp_code() {
            return mp_code;
        }

        public String getMp_makecode() {
            return mp_makecode;
        }

        public boolean isMp_whcode() {
            return mp_whcode;
        }

        public int getMp_id() {
            return mp_id;
        }

        public String getMp_linecode() {
            return mp_linecode;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Message> getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Mpcode{" +
                "success=" + success +
                ", message=" + message.toString() +
                '}';
    }
}
