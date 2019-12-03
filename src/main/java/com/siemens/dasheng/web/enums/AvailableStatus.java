package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum AvailableStatus {

    /**
     * Available
     */
    AVAILABLE {

        @Override
        public Long getType() {
            return 1L;
        }
    },
    /**
     * Partly Available
     */
    PARTLY_AVAILABLE {

        @Override
        public Long getType() {
            return 2L;
        }
    },
    /**
     * Unavailable
     */
    UNAVAILABLE {

        @Override
        public Long getType() {
            return 3L;
        }
    };

    public abstract Long getType();

}
