package com.siemens.dasheng.web.enums;

/**
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
public enum OpertionTypeEnum {
    /**
     * USE 使用
     */
    USE {

        @Override
        public String getType() {
            return "0";
        }
    },
    /**
     * RELEASE 释放
     */
    RELEASE {

        @Override
        public String getType() {
            return "1";
        }
    };

    public abstract String getType();
}
