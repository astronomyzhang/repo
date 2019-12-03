package com.siemens.dasheng.web.enums;

/**
 * @author xin.xu@siemens.com
 * Created by xuxin on 2018/11/15.
 */
public enum ConnectorAvailableStatus {

    /**
     * Not Available(已分配资源，不可用)
     */
    NOT_AVAILABLE {
        @Override
        public Integer getType() {
            return 0;
        }
    },
    /**
     * Available（已分配资源，可用）
     */
    AVAILABLE {
        @Override
        public Integer getType() {
            return 1;
        }
    },
    /**
     * undistributed（未分配资源）
     */
    UNDISTRIBUTED {
        @Override
        public Integer getType() {
            return 2;
        }
    };

    public abstract Integer getType();

}
