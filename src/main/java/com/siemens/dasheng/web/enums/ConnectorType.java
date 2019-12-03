package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum ConnectorType {

    /**
     * TIME_SERIES_DATA
     */
    TIME_SERIES_DATA {

        @Override
        public String getType() {
            return "TIME_SERIES_DATA";
        }
    },
    /**
     * SQL_DATA
     */
    SQL_DATA {

        @Override
        public String getType() {
            return "SQL_DATA";
        }
    };

    public abstract String getType();

}
