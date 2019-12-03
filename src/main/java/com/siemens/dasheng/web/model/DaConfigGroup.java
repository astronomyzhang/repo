package com.siemens.dasheng.web.model;

/**
 * @author ly
 * @data 2019/4/9
 */
public class DaConfigGroup {
    private Long id;

    private String name;

    private String description;

    private Long account;

    private Long appId;



    public DaConfigGroup(Long id, String name, String description, Long account, Long appId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.account = account;
        this.appId = appId;
    }

    public DaConfigGroup() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Long getAccount() {
        return account;
    }

    public void setAccount(Long account) {
        this.account = account;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

}