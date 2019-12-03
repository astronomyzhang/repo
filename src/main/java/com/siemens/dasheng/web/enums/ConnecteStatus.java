package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum ConnecteStatus {

    /**
     * CONNECTABLE
     */
    CONNECTABLE {

        @Override
        public Long getType() {
            return 1L;
        }
    },
    /**
     * UNCONNECTABLE
     */
    UNCONNECTABLE {

        @Override
        public Long getType() {
            return 2L;
        }
    };

    public abstract Long getType();

}
