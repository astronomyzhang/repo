package com.siemens.dasheng.web.model;

import java.util.List;

/**
 * @author xuxin
 * DaConfigConnectorType
 * created by xuxin on 15/11/2018
 */
public class DaConfigConnectorType {
    private String id;

    private String name;

    private String dataType;

    private List<DaConfigConnectorClass> daConfigConnectorClassList;

    public DaConfigConnectorType(String id, String name, String dataType) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
    }

    public DaConfigConnectorType() {
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

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }

    public List<DaConfigConnectorClass> getDaConfigConnectorClassList() {
        return daConfigConnectorClassList;
    }

    public void setDaConfigConnectorClassList(List<DaConfigConnectorClass> daConfigConnectorClassList) {
        this.daConfigConnectorClassList = daConfigConnectorClassList;
    }
}