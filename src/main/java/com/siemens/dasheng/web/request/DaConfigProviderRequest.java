package com.siemens.dasheng.web.request;

import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.validators.interfaces.CharacterBytesLength;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_CONNECTOR_NOTBLANK_PROVODER_NAME;

/**
 * @author xuxin
 * DaConfigProviderRequest
 * created by xuxin on 15/11/2018
 */
public class DaConfigProviderRequest {
    private Long id;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_PROVODER_NAME)
    @CharacterBytesLength(message = DATA_PROVODER_NAME_LENGTH_MAXSIZE_30,value = 30)
    private String name;

    private String dataType;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_CONNECTOR_TYPE)
    @CharacterBytesLength(message = CONNECTOR_TYPE_LENGTH_MAXSIZE_30,value = 30)
    private String connectorType;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_CONNECT_CLASS)
    @CharacterBytesLength(message = CONNECTOR_CLASS_LENGTH_MAXSIZE_30,value = 30)
    private String connectorClass;

    @CharacterBytesLength(message = CONNECTOR_DESCRIPTION_LENGTH_MAXSIZE_600,value = 600)
    private String description;

    private String updatedBy;

    private Long updateDate;

    private List<DaConfigConnector> connectorList;

    private Long availability;

    private String availabilityRate;

    @NotNull(message = DA_CONFIG_CONNECTOR_NOTBLANK_PROVIDER_STATUS)
    private Long status;

    public Long getAvailability() {
        return availability;
    }

    public void setAvailability(Long availability) {
        this.availability = availability;
    }



    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public List<DaConfigConnector> getConnectorList() {
        return connectorList;
    }

    public void setConnectorList(List<DaConfigConnector> connectorList) {
        this.connectorList = connectorList;
    }

    public String getAvailabilityRate() {
        return availabilityRate;
    }

    public void setAvailabilityRate(String availabilityRate) {
        this.availabilityRate = availabilityRate;
    }
}
