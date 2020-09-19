package com.eplan.user.util;

import java.util.HashMap;
import java.util.Map;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
public class UserConstants {

    public static enum ROLE_STATUS {
        ACTIVE(0, "Baru"),
        INACTIVE(1, "Dalam Proses");
        
        private Integer value;
        private String msg;
        private static Map map = new HashMap();
        
        private ROLE_STATUS(Integer value, String msg) {
            this.value = value;
            this.msg = msg;
        }

        static {
            for (ROLE_STATUS pageType : ROLE_STATUS.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static ROLE_STATUS valueOf(int pageType) {
            return (ROLE_STATUS) map.get(pageType);
        }

        public Integer getValue() {
            return value;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static enum USER_STATUS {
        ACTIVE(0, "Aktif"),
        INACTIVE(1, "Tidak Aktif"),
        BLOCKED(2, "Diblokir");

        private Integer value;
        private String msg;
        private static Map map = new HashMap();

        private USER_STATUS(Integer value, String msg) {
            this.value = value;
            this.msg = msg;
        }

        static {
            for (USER_STATUS pageType : USER_STATUS.values()) {
                map.put(pageType.value, pageType);
            }
        }

        public static USER_STATUS valueOf(int pageType) {
            return (USER_STATUS) map.get(pageType);
        }

        public Integer getValue() {
            return value;
        }

        public String getMsg() {
            return msg;
        }
    }
    
}
