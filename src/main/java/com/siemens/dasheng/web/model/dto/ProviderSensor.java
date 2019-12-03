package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/6/2019.
 */
public class ProviderSensor {
    /**
     * 提供者id
     */
    private Long providerId;

    private String name;

    /**
     * 测点数量
     */
    private Integer sensorCount;

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Integer getSensorCount() {
        return sensorCount;
    }

    public void setSensorCount(Integer sensorCount) {
        this.sensorCount = sensorCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
