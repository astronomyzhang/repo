package com.siemens.dasheng.web.enums;

/**
 * @author yuanxin.zhang@siemens.com
 * Created by zhangyuanxin on 2018/1/29.
 */
public enum DataBaseType {

    /**
     * ORACLE
     */
    ORACLE {

        @Override
        public String getType() {
            return "oracle";
        }
    },
    /**
     * POSTGRESQL
     */
    POSTGRESQL {

        @Override
        public String getType() {
            return "postgresql";
        }
    };

    public abstract String getType();

}
