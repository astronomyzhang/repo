package com.siemens.dasheng.web.request;

import javax.validation.constraints.NotNull;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_CONNECTOR_ID;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_SENSOR_CODE;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class CheckTagExistRequest {

    @NotNull(message = DA_CONFIG_NOTBLANK_CONNECTOR_ID)
    private Long connectorId;

    @NotNull(message = DA_CONFIG_NOTBLANK_SENSOR_CODE)
    private String tag;

    private Long providerId;

    private Long applicationId;

    private Boolean toBeCreated;

    private String siecode;

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Boolean getToBeCreated() {
        return toBeCreated;
    }

    public void setToBeCreated(Boolean toBeCreated) {
        this.toBeCreated = toBeCreated;
    }

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
