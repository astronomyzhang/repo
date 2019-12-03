package com.siemens.dasheng.web.request;

/**
 * @author allan
 * Created by z0041dpv on 6/13/2019.
 */
public class AppLockRequest {
    /**
     * 注册appid
     */
    private String appId;
    /**
     * 加锁appId
     */
    private String lockAppId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getLockAppId() {
        return lockAppId;
    }

    public void setLockAppId(String lockAppId) {
        this.lockAppId = lockAppId;
    }
}
