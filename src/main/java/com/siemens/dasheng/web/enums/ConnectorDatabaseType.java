package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum ConnectorDatabaseType {

    /**
     * OSIPI
     */
    OSIPI {

        @Override
        public String getType() {
            return "OSIPI";
        }
    },
    /**
     * OPENPLANT
     */
    OPENPLANT {

        @Override
        public String getType() {
            return "OPENPLANT";
        }
    },
    /**
     * OPC
     */
    OPC {

        @Override
        public String getType() {
            return "OPC";
        }
    },
    /**
     * OPC_REALTIME
     */
    OPC_REALTIME {

        @Override
        public String getType() {
            return "OPC_REALTIME";
        }
    },
    /**
     * OPC-UA
     */
    OPC_UA {

        @Override
        public String getType() {
            return "OPC-UA";
        }
    };

    public abstract String getType();

}
