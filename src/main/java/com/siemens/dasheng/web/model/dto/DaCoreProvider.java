package com.siemens.dasheng.web.model.dto;

import java.util.List;
import java.util.Set;

/**
 * @author ly
 * Jan 18, 2019
 * 数据接收类，用于接收从da_core模块中获取的provider信息
 */
public class DaCoreProvider {

    private Integer providerId;
    private String name;
    private String connectorClass;
    private long sensorMappingUpdatedTimestamp;
    private boolean sensorMappingUpdated = true;
    private Set<DaCoreConnector> connectorSet;

    private List<DaProviderSensorMapping> sensorMappingList;

    public DaCoreProvider() {
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Set<DaCoreConnector> getConnectorSet() {
        return connectorSet;
    }

    public void setConnectorSet(Set<DaCoreConnector> connectorSet) {
        this.connectorSet = connectorSet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public long getSensorMappingUpdatedTimestamp() {
        return sensorMappingUpdatedTimestamp;
    }

    public void setSensorMappingUpdatedTimestamp(long sensorMappingUpdatedTimestamp) {
        this.sensorMappingUpdatedTimestamp = sensorMappingUpdatedTimestamp;
    }

    public List<DaProviderSensorMapping> getSensorMappingList() {
        return sensorMappingList;
    }

    public void setSensorMappingList(List<DaProviderSensorMapping> sensorMappingList) {
        this.sensorMappingList = sensorMappingList;
    }

    public boolean isSensorMappingUpdated() {
        return sensorMappingUpdated;
    }

    public void setSensorMappingUpdated(boolean sensorMappingUpdated) {
        this.sensorMappingUpdated = sensorMappingUpdated;
    }
}
