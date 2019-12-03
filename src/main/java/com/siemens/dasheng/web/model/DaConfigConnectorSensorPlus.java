package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/7
 */
public class DaConfigConnectorSensorPlus extends DaConfigConnectorSensor {
    private Long connectorStatus;

    private String dbName;

    private Integer validateStatus;

    private String description;

    private String msg;

    private Boolean check;

    private String connectorName;

    private String siecode;

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Long getConnectorStatus() {
        return connectorStatus;
    }

    public void setConnectorStatus(Long connectorStatus) {
        this.connectorStatus = connectorStatus;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setValidateStatus(Integer validateStatus) {
        this.validateStatus = validateStatus;
    }

    public Integer getValidateStatus() {
        return validateStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
