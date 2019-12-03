package com.siemens.dasheng.web.request;

import com.siemens.dasheng.web.model.DaConfigConnector;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/4/8
 */
public class ValidateConnectorsStatusRequest {

    private String providerId;

    private List<DaConfigConnector> daConfigConnectorList;

    private Long status;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public List<DaConfigConnector> getDaConfigConnectorList() {
        return daConfigConnectorList;
    }

    public void setDaConfigConnectorList(List<DaConfigConnector> daConfigConnectorList) {
        this.daConfigConnectorList = daConfigConnectorList;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
