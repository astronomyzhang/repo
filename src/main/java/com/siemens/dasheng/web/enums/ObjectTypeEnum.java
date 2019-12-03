package com.siemens.dasheng.web.enums;

/**
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
public enum ObjectTypeEnum {
    /**
     * SENSOR
     */
    SENSOR {

        @Override
        public String getType() {
            return "SENSOR";
        }
    },
    /**
     * SENSORGROUP
     */
    SENSORGROUP {

        @Override
        public String getType() {
            return "SENSORGROUP";
        }
    },
    /**
     * SENSORRULE
     */
    SENSORRULE {

        @Override
        public String getType() {
            return "SENSORRULE";
        }
    };

    public abstract String getType();

}
