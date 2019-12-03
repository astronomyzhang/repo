package com.siemens.dasheng.web.response;

/**
 * @author allan
 * Created by z0041dpv on 6/13/2019.
 */
public class AppLockRespone {

    private String msg;

    private Long code;

    private boolean success;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
