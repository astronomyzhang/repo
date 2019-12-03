package com.siemens.dasheng.web.response;

/**
 * @author allan
 * Created by ofm on 2019/4/3.
 */
public class ResponseObject {
    private String data;

    private Boolean isSuccess;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }
}
