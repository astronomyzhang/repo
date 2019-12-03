package com.siemens.dasheng.web.enums;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/5/18.
 */
public enum UsageType {

    /**
     * 0
     */
    AVAILABLE {
        @Override
        public Integer getType(){
            return 0;
        }
    }
    /**
     * 1
     */
    ,UNAVAILABLE {
        @Override
        public Integer getType(){
            return 1;
        }
    };

    public abstract Integer getType();
}
