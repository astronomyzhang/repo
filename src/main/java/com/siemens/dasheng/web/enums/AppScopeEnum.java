package com.siemens.dasheng.web.enums;

/**
 * @author allan
 * app 应用类型
 * Created by ofm on 2019/4/3.
 */
public enum AppScopeEnum {
    /**
     * PRIVATE
     */
    APP_PRIVATE {

        @Override
        public Integer getType() {
            return 0;
        }
    },
    /**
     * PUBLIC
     */
    APP_PUBLIC {

        @Override
        public Integer getType() {
            return 1;
        }
    },
    /**
     * ALL
     */
    APP_ALL{

        @Override
        public Integer getType() {
            return 2;
        }
    };

    public abstract Integer getType();
}
