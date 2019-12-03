package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/4/7
 */
public class DaConfigSensorPlus extends DaConfigSensor {

    private String connectorName;

    private String dbName;

    private Long scope;

    private Long ffAppId;

    private String appName;

    private String uniqueAppId;

    private Integer usage;

    public Integer getUsage() {
        return usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    public String getUniqueAppId() {
        return uniqueAppId;
    }

    public void setUniqueAppId(String uniqueAppId) {
        this.uniqueAppId = uniqueAppId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getFfAppId() {
        return ffAppId;
    }

    public void setFfAppId(Long ffAppId) {
        this.ffAppId = ffAppId;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public Long getScope() {
        return scope;
    }

    public void setScope(Long scope) {
        this.scope = scope;
    }
}
