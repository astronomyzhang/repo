package com.siemens.dasheng.web.enums;

/**
 * @author xuxin
 * Created by xuxin on 2019/7/18.
 */
public enum ConnDatabaseType {

    /**
     * 1
     */
    SQLDAS_LINUX {
        @Override
        public int getType(){
            return 1;
        }
    }
    /**
     * 2
     */
    ,SQLDAS_WINDOWS {
        @Override
        public int getType(){
            return 2;
        }
    }
    /**
     * 3
     */
    ,OPENPLANT_LINUX {
        @Override
        public int getType(){
            return 3;
        }
    }
    /**
     * 4
     */
    ,SDK_WINDOWS {
        @Override
        public int getType(){
            return 4;
        }
    }
    /**
     * 5
     */
    ,PI_AF {
        @Override
        public int getType(){
            return 5;
        }
    }
    /**
     * 6
     */
    ,OPCHDA {
        @Override
        public int getType(){
            return 6;
        }
    }
    /**
     * 7
     */
    ,OPCHDA_REALTIME {
        @Override
        public int getType(){
            return 7;
        }
    }
    /**
     * 8
     */
    ,OPCUA {
        @Override
        public int getType(){
            return 8;
        }
    }
    /**
     * 0
     */
    ,DEFAULT {
        @Override
        public int getType(){
            return 0;
        }
    };

    public abstract int getType();
}
