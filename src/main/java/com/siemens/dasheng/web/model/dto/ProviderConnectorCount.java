package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/8/2019.
 */
public class ProviderConnectorCount {
    private Long providerId;

    private Integer connectorCount;

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Integer getConnectorCount() {
        return connectorCount;
    }

    public void setConnectorCount(Integer connectorCount) {
        this.connectorCount = connectorCount;
    }
}
