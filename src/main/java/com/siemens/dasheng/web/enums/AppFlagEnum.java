package com.siemens.dasheng.web.enums;

/**
 * @author allan
 * Created by ofm on 2019/4/3.
 */
public enum AppFlagEnum {
    /**
     * ZERO
     */
    ZERO {

        @Override
        public Integer getType() {
            return 0;
        }
    },
    /**
     * ONE
     */
    ONE {

        @Override
        public Integer getType() {
            return 1;
        }
    },
    /**
     * TWO
     */
    TWO {

        @Override
        public Integer getType() {
            return 2;
        }
    },
    /**
     * THREE
     */
    THREE {

        @Override
        public Integer getType() {
            return 3;
        }
    };

    public abstract Integer getType();
}
