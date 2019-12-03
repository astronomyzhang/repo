package com.siemens.dasheng.web.model;

import java.util.List;
import java.util.Objects;

/**
 * @author xuxin
 * DaConfigProvider
 * created by xuxin on 28/11/2018
 */
public class DaConfigProvider {
    private Long id;

    private String name;

    private String dataType;

    private String connectorType;

    private String connectorClass;

    private String description;

    private String updatedBy;

    private Long updateDate;

    private Long availability;

    private String availabilityRate;

    private Long status;

    private String connectorClassName;

    private List<DaConfigConnector> daConfigConnectorList;

    private Integer dataConnectors;

    private Long sensormappingUpdateTime = 0L;

    private Boolean selected;

    public DaConfigProvider(Long id, String name, String dataType, String connectorType, String connectorClass, String description, String updatedBy, Long updateDate, Long availability, String availabilityRate, Long status, Long sensormappingUpdateTime) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.connectorType = connectorType;
        this.connectorClass = connectorClass;
        this.description = description;
        this.updatedBy = updatedBy;
        this.updateDate = updateDate;
        this.availability = availability;
        this.availabilityRate = availabilityRate;
        this.status = status;
        if (null != sensormappingUpdateTime) {
            this.sensormappingUpdateTime = sensormappingUpdateTime;
        }
    }

    public Long getSensormappingUpdateTime() {
        return sensormappingUpdateTime;
    }

    public void setSensormappingUpdateTime(Long sensormappingUpdateTime) {
        this.sensormappingUpdateTime = sensormappingUpdateTime;
    }

    public DaConfigProvider() {
        super();
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
        this.name = name == null ? null : name.trim();
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType == null ? null : connectorType.trim();
    }

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass == null ? null : connectorClass.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy == null ? null : updatedBy.trim();
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DaConfigProvider)) {
            return false;
        }
        DaConfigProvider that = (DaConfigProvider) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public List<DaConfigConnector> getDaConfigConnectorList() {
        return daConfigConnectorList;
    }

    public void setDaConfigConnectorList(List<DaConfigConnector> daConfigConnectorList) {
        this.daConfigConnectorList = daConfigConnectorList;
    }

    public Long getAvailability() {
        return availability;
    }

    public void setAvailability(Long availability) {
        this.availability = availability;
    }

    public String getAvailabilityRate() {
        return availabilityRate;
    }

    public void setAvailabilityRate(String availabilityRate) {
        this.availabilityRate = availabilityRate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getConnectorClassName() {
        return connectorClassName;
    }

    public void setConnectorClassName(String connectorClassName) {
        this.connectorClassName = connectorClassName;
    }

    public Integer getDataConnectors() {
        return dataConnectors;
    }

    public void setDataConnectors(Integer dataConnectors) {
        this.dataConnectors = dataConnectors;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}