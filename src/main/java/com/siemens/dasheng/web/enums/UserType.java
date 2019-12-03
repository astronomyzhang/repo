package com.siemens.dasheng.web.enums;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/5/18.
 */
public enum UserType {

    /**
     * 0
     */
    ADMIN {
        @Override
        public Integer getType(){
            return 0;
        }
    }
    /**
     * 1
     */
    ,COMMONUSER {
        @Override
        public Integer getType(){
            return 1;
        }
    };

    public abstract Integer getType();
}
