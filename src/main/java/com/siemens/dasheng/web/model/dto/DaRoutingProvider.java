package com.siemens.dasheng.web.model.dto;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/2/26.
 */
public class DaRoutingProvider {

    private String providerId;
    private long sensorUpdateTime;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public long getSensorUpdateTime() {
        return sensorUpdateTime;
    }

    public void setSensorUpdateTime(long sensorUpdateTime) {
        this.sensorUpdateTime = sensorUpdateTime;
    }

}
