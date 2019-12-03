package com.siemens.dasheng.web.request;

/**
 * @author xuxin
 * DaConfigProviderRequest
 * created by xuxin on 15/5/2019
 */
public class DeleteSensorRequest {

    private String siecode;

    private Long appId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }
}
