package com.siemens.dasheng.web.model;

import com.siemens.dasheng.web.util.MyMapper;

import java.util.Date;
import java.util.Objects;

/**
 * @author xuxin
 * DaConfigProviderConnector
 * created by xuxin on 28/11/2018
 */
public class DaConfigProviderConnector {
    private Long id;

    private Long connectorId;

    private Long providerId;


    public DaConfigProviderConnector(Long id, Long connectorId, Long providerId) {
        this.id = id;
        this.connectorId = connectorId;
        this.providerId = providerId;
    }

    public DaConfigProviderConnector() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof DaConfigProviderConnector)) {return false;}
        DaConfigProviderConnector that = (DaConfigProviderConnector) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}