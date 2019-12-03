package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author allan
 * Created by ofm on 2019/4/3.
 */
public class AppInfo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("标识（0内部应用（不可新增），1普通应用，2远程桌面，3后台服务应用）")
    private Integer flag;

    @ApiModelProperty("app name")
    private String name;

    @ApiModelProperty("full name")
    private String fullname;

    @ApiModelProperty("description")
    private String description;

    @ApiModelProperty("应用提供者数量")
    private Integer providerNum;

    @ApiModelProperty("app唯一标识符")
    private String appid;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Integer getProviderNum() {
        return providerNum;
    }

    public void setProviderNum(Integer providerNum) {
        this.providerNum = providerNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
