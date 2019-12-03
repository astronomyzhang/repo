package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/10/2019.
 */
public class AppOwnSensorGroupUsage {

    private Long ownAppId;
    private Long appId;

    private Integer useCount;

    public Long getOwnAppId() {
        return ownAppId;
    }

    public void setOwnAppId(Long ownAppId) {
        this.ownAppId = ownAppId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }
}
