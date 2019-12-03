package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum DelStatus {

    /**
     * UNIMPORT
     */
    UNIMPORT {

        @Override
        public Long getType() {
            return 0L;
        }
    },
    /**
     * IMPORTED
     */
    IMPORTED {

        @Override
        public Long getType() {
            return 1L;
        }
    },
    /**
     * DELETE
     */
    DELETE {

        @Override
        public Long getType() {
            return 2L;
        }
    };

    public abstract Long getType();

}
