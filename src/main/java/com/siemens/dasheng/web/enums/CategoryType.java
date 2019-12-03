package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum CategoryType {

    /**
     * SQLDASV2012
     */
    SQLDASV2012_LINUX {

        @Override
        public String getType() {
            return "SQLDASV2012_LINUX";
        }
    },
    /**
     * SQLDASV2016_LINUX
     */
    SQLDASV2016_LINUX {

        @Override
        public String getType() {
            return "SQLDASV2016_LINUX";
        }
    },
    /**
     * SQLDASV2012
     */
    SQLDASV2012_WINDOWS{

        @Override
        public String getType() {
            return "SQLDASV2012_WINDOWS";
        }
    },
    /**
     * SQLDASV2016_LINUX
     */
    SQLDASV2016_WINDOWS {

        @Override
        public String getType() {
            return "SQLDASV2016_WINDOWS";
        }
    },
    /**
     * OPENPLANTSDK_LINUX
     */
    OPENPLANTSDK_LINUX {

        @Override
        public String getType() {
            return "OPENPLANTSDK_LINUX";
        }
    },
    /**
     * SDKV2016
     */
    SDKV2016_WINDOWS {

        @Override
        public String getType() {
            return "SDKV2016_WINDOWS";
        }
    },
    /**
     * OPCHDA
     */
    OPCHDA {

        @Override
        public String getType() {
            return "OPCHDA";
        }
    },
    /**
     * OPCHDA_REALTIME
     */
    OPCHDA_REALTIME {

        @Override
        public String getType() {
            return "OPCHDA_REALTIME";
        }
    },
    /**
     * OSIPIAFSERVER
     */
    OSIPIAFSERVER {

        @Override
        public String getType() {
            return "OSIPIAFSERVER";
        }
    },
    /**
     * OPCUA_GATEWAY
     */
    OPCUA_GATEWAY {

        @Override
        public String getType() {
            return "OPCUA_GATEWAY";
        }
    };

    public abstract String getType();

}
