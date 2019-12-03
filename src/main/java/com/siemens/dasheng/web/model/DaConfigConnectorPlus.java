package com.siemens.dasheng.web.model;

import java.util.Objects;

/**
 * @author zhangliming
 * DaConfigConnector
 * created by xuxin on 15/11/2018
 */
public class DaConfigConnectorPlus extends  DaConfigConnector {
    private Long providerId;

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}