package com.siemens.dasheng.web.request;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author allan
 * Created by ofm on 2019/4/4.
 */
public class DaConfigApplicationRequest {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("appId")
    private Long appId;

    @ApiModelProperty("app类型")
    private String appType;

    @ApiModelProperty("access scope")
    private String type;

    @ApiModelProperty("继承appid")
    private List<Long> extensionIds;

    @ApiModelProperty("连接器")
    private List<Long> providerIds;

    private String dbType;

    private String globalAppId;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
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

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Long> getExtensionIds() {
        return extensionIds;
    }

    public void setExtensionIds(List<Long> extensionIds) {
        this.extensionIds = extensionIds;
    }

    public List<Long> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<Long> providerIds) {
        this.providerIds = providerIds;
    }

    public String getGlobalAppId() {
        return globalAppId;
    }

    public void setGlobalAppId(String globalAppId) {
        this.globalAppId = globalAppId;
    }
}
