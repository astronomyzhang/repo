package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModelProperty;

/**@author alaln
 * Created by ofm on 2019/4/9.
 */
public class AppPublicInfo {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("fleet frame appId")
    private Long appId;

    @ApiModelProperty("标识（0内部应用（不可新增），1普通应用，2远程桌面，3后台服务应用）")
    private Integer flag;

    @ApiModelProperty("app name")
    private String name;

    @ApiModelProperty("description")
    private String description;

    @ApiModelProperty("应用提供者数量")
    private Integer providerNum;

    private Long availability;

    private String availabilityRate;

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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getProviderNum() {
        return providerNum;
    }

    public void setProviderNum(Integer providerNum) {
        this.providerNum = providerNum;
    }
}
