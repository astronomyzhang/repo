package com.siemens.dasheng.web.model;

import java.util.List;

/**
 * @author xuxin
 * DaConfigConnectorClass
 * created by xuxin on 15/11/2018
 */
public class DaConfigConnectorClass {
    private String id;

    private String name;

    private String connectorTypeId;

    private List<DaConfigDatabase> daConfigDatabaseList;

    public DaConfigConnectorClass(String id, String name, String connectorTypeId) {
        this.id = id;
        this.name = name;
        this.connectorTypeId = connectorTypeId;
    }

    public DaConfigConnectorClass() {
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

    public String getConnectorTypeId() {
        return connectorTypeId;
    }

    public void setConnectorTypeId(String connectorTypeId) {
        this.connectorTypeId = connectorTypeId == null ? null : connectorTypeId.trim();
    }

    public List<DaConfigDatabase> getDaConfigDatabaseList() {
        return daConfigDatabaseList;
    }

    public void setDaConfigDatabaseList(List<DaConfigDatabase> daConfigDatabaseList) {
        this.daConfigDatabaseList = daConfigDatabaseList;
    }
}