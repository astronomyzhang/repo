package com.siemens.dasheng.web.exception;

/**
 * @author allan
 * Created by ofm on 2019/4/15.
 */
public class BusinessException extends RuntimeException {
    protected String code;

    protected String message;

    protected Object data;

    public BusinessException(String message) {
        this.message = message;
    }
    public BusinessException(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
