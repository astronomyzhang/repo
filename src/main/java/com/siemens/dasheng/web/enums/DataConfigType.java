package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum DataConfigType {

    /**
     * DATA_CONNECTOR
     */
    DATA_CONNECTOR {

        @Override
        public String getType() {
            return "DATA_CONNECTOR";
        }
    },
    /**
     * DATA_SOURCE
     */
    DATA_SOURCE {

        @Override
        public String getType() {
            return "DATA_SOURCE";
        }
    },
    /**
     * DATA_PROVIDER
     */
    DATA_PROVIDER {

        @Override
        public String getType() {
            return "DATA_PROVIDER";
        }
    },
    /**
     * DATA_ADAPTER
     */
    DATA_ADAPTER {

        @Override
        public String getType() {
            return "DATA_ADAPTER";
        }
    };

    public abstract String getType();

}
