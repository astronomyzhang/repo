package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
public class DaConfigAppProvider {
    private Long id;

    private Long appId;

    private Long providerId;

    public DaConfigAppProvider(Long id, Long appId, Long providerId) {
        this.id = id;
        this.appId = appId;
        this.providerId = providerId;
    }

    public DaConfigAppProvider() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }
}