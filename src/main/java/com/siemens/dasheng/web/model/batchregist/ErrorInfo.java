package com.siemens.dasheng.web.model.batchregist;


/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class ErrorInfo {

    private Integer errorCode;

    private String errorMsg;

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
