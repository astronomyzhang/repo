package com.siemens.dasheng.web.enums;

/**
 * 10000－12000： 输出至前端的公共错误码
 * 统一的错误输出枚举类
 * yaming.chen@siemens.com
 *
 * @author chenyaming
 * @date 2017/1/20
 */
public enum ErrorCodeCommon {

    /**
     * PI RPC接口连接错误
     */
    PiConnectError {

        @Override
        public String getErrorCode() {
            return "10001";
        }
    },
    /**
     * PI RPC接口数据错误
     */
    PiDataError {

        @Override
        public String getErrorCode() {
            return "10002";
        }
    },
    /**
     * request 传入参数错误导致的空指针、程序流程错误等问题
     */
    RequestParamsError {

        @Override
        public String getErrorCode() {
            return "10003";
        }
    },
    /**
     * 数据库操作失败
     */
    SqlOptError {

        @Override
        public String getErrorCode() {
            return "10004";
        }
    },
    /**
     * 操作失败（指新建失败、更新失败或删除失败）
     */
    OperationError {

        @Override
        public String getErrorCode() {
            return "10005";
        }
    },
    /**
     * 用户会话失效
     */
    SessionTimeoutError {

        @Override
        public String getErrorCode() {
            return "10006";
        }
    },
    /**
     * 上传文件流失败
     */
    UploadingError {

        @Override
        public String getErrorCode() {
            return "10007";
        }
    },

    /**
     * 数据验证不通过
     */
    DataCheckError {
        @Override
        public String getErrorCode() {
            return "10008";
        }
    };



    public abstract String getErrorCode();

}
