package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/6/2019.
 */
public class AppSensor {

    /**
     * 应用id
     */
    private Long appId;

    private Long ffAppId;

    /**
     * 测点数量
     */
    private Integer sensorCout;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getSensorCout() {
        return sensorCout;
    }

    public void setSensorCout(Integer sensorCout) {
        this.sensorCout = sensorCout;
    }

    public Long getFfAppId() {
        return ffAppId;
    }

    public void setFfAppId(Long ffAppId) {
        this.ffAppId = ffAppId;
    }
}
