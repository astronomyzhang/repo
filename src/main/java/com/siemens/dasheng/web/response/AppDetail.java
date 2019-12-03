package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaConfigProvider;

import java.util.List;

/**
 * @author liming
 * @Date: 2019/4/8 11:12
 */
public class AppDetail {

    private Integer type;

    private Long scope;

    private String description;

    private Long dateTime;

    private String registoredUser;

    private Long ffAppId;

    private String name;

    private Boolean inherit;

    private List<InheritedApp> publicApplications;

    private List<DaConfigProvider> providers;

    public Boolean getInherit() {
        return inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFfAppId() {
        return ffAppId;
    }

    public void setFfAppId(Long ffAppId) {
        this.ffAppId = ffAppId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getScope() {
        return scope;
    }

    public void setScope(Long scope) {
        this.scope = scope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getRegistoredUser() {
        return registoredUser;
    }

    public void setRegistoredUser(String registoredUser) {
        this.registoredUser = registoredUser;
    }

    public List<InheritedApp> getPublicApplications() {
        return publicApplications;
    }

    public void setPublicApplications(List<InheritedApp> publicApplications) {
        this.publicApplications = publicApplications;
    }

    public List<DaConfigProvider> getProviders() {
        return providers;
    }

    public void setProviders(List<DaConfigProvider> providers) {
        this.providers = providers;
    }
}
