package com.siemens.dasheng.web.model;

import java.util.List;

/**
 * @author xuxin
 * DaConfigDatabase
 * created by xuxin on 15/11/2018
 */
public class DaConfigDatabase {
    private String id;

    private String name;

    private String classId;

    private List<DaConfigCategory> daConfigCategoryList;

    public DaConfigDatabase(String id, String name, String classId) {
        this.id = id;
        this.name = name;
        this.classId = classId;
    }

    public DaConfigDatabase() {
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

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId == null ? null : classId.trim();
    }

    public List<DaConfigCategory> getDaConfigCategoryList() {
        return daConfigCategoryList;
    }

    public void setDaConfigCategoryList(List<DaConfigCategory> daConfigCategoryList) {
        this.daConfigCategoryList = daConfigCategoryList;
    }
}