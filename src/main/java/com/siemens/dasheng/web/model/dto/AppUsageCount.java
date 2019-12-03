package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/8/2019.
 */
public class AppUsageCount {

    private String globalAppId;

    private Long ffAppId;

    /**
     * 测点数量
     */
    private Integer sensorCout;

    public String getGlobalAppId() {
        return globalAppId;
    }

    public void setGlobalAppId(String globalAppId) {
        this.globalAppId = globalAppId;
    }

    public Long getFfAppId() {
        return ffAppId;
    }

    public void setFfAppId(Long ffAppId) {
        this.ffAppId = ffAppId;
    }

    public Integer getSensorCout() {
        return sensorCout;
    }

    public void setSensorCout(Integer sensorCout) {
        this.sensorCout = sensorCout;
    }
}
