package com.siemens.dasheng.web.model;

/**
 * @author xuxin
 * DaConfigCategory
 * created by xuxin on 15/11/2018
 */
public class DaConfigCategory {
    private String id;

    private String name;

    private String databaseId;

    public DaConfigCategory(String id, String name, String databaseId) {
        this.id = id;
        this.name = name;
        this.databaseId = databaseId;
    }

    public DaConfigCategory() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId == null ? null : databaseId.trim();
    }
}