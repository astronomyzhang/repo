package com.siemens.dasheng.web.enums;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/5/28.
 */
public enum  DppModeType {

    /**
     * Anomaly only mode
     */
    SAM {

        @Override
        public String getType() {
            return "SAM";
        }
    }
    /**
     * Anomaly + Cws mode
     */
    ,ICM {

        @Override
        public String getType() {
            return "ICM";
        }
    };

    public abstract String getType();

}
