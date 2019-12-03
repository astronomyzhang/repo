package com.siemens.dasheng.web.request;

import javax.validation.constraints.NotNull;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_APPLICATION_ID;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_SENSOR_CODE;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_TAG_INFO;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_PROVIDER_ID;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_CONNECTOR_ID;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class SensorRegistrationRequest {

    private Long id;

    @NotNull(message = DA_CONFIG_NOTBLANK_APPLICATION_ID)
    private Long applicationId;

    @NotNull(message = DA_CONFIG_NOTBLANK_SENSOR_CODE)
    private String siecode;

    private String description;

    @NotNull(message = DA_CONFIG_NOTBLANK_PROVIDER_ID)
    private Long providerId;

    @NotNull(message = DA_CONFIG_NOTBLANK_CONNECTOR_ID)
    private Long connectorId;

    @NotNull(message = DA_CONFIG_NOTBLANK_TAG_INFO)
    private String tag;

    private Boolean toBeCreated;

    private String origSiecode;

    private String optag;

    public String getOptag() {
        return optag;
    }

    public void setOptag(String optag) {
        this.optag = optag;
    }

    public String getOrigSiecode() {
        return origSiecode;
    }

    public void setOrigSiecode(String origSiecode) {
        this.origSiecode = origSiecode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

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

    public Boolean getToBeCreated() {
        return toBeCreated;
    }

    public void setToBeCreated(Boolean toBeCreated) {
        this.toBeCreated = toBeCreated;
    }
}
