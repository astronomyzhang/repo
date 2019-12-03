package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum OpcuaGateway {

    /**
     * YES
     */
    YES {

        @Override
        public Long getType() {
            return 1L;
        }
    },
    /**
     * NO
     */
    NO {

        @Override
        public Long getType() {
            return 0L;
        }
    };

    public abstract Long getType();

}
