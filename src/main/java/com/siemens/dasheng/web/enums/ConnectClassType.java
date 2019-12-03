package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum ConnectClassType {

    /**
     * ARCHIVED
     */
    ARCHIVED {

        @Override
        public String getType() {
            return "ARCHIVED";
        }
    },
    /**
     * REAL_TIME
     */
    REAL_TIME {

        @Override
        public String getType() {
            return "REAL_TIME";
        }
    },
    /**
     * REAL_TIME_AND_ARCHIVED
     */
    REAL_TIME_AND_ARCHIVED {

        @Override
        public String getType() {
            return "REAL_TIME_AND_ARCHIVED";
        }
    },
    /**
     * PIAF
     */
    PIAF {

        @Override
        public String getType() {
            return "PIAF";
        }
    };

    public abstract String getType();

}
