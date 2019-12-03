package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum ConnectorStatus {

    /**
     * ACTIVATE
     */
    ACTIVATE {

        @Override
        public Long getType() {
            return 1L;
        }
    },
    /**
     * INACTIVE
     */
    INACTIVE {

        @Override
        public Long getType() {
            return 2L;
        }
    };

    public abstract Long getType();

}
