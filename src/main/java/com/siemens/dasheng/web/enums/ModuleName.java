package com.siemens.dasheng.web.enums;

/**
 * 所属产品
 * @Author: Bian Fan
 * @Date: Created in 2018/7/13.
 */
public enum ModuleName {

    /**
     * AnomalyDetection
     */
    ANOMALY {
        @Override
        public String getName() {
            return "ANOMALYDETECTION";
        }
    }
    /**
     * CentralizedWarning
     */
    , CWS {
        @Override
        public String getName() {
            return "CENTRALIZEDWARNING";
        }
    }/**
     * Platform
     */
    , Platform {
        @Override
        public String getName() {
            return "Platform";
        }
    };

    public abstract String getName();
}
