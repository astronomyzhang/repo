package com.siemens.dasheng.web.model.dto;

/**
 * @author ly
 * Jan 18, 2019
 * 数据接收类，用于接收从da_core模块中获取的connector信息
 */
public class DaCoreConnector {

    private Integer connectorId;

    private Integer providerId;

    private String name;

    private String archivedDatabase;

    private String connectApproach;

    private String connectorClass;

    private String serverHost;

    private String port;

    private String sqldas;

    private String userName;

    private String password;

    private Long connectTimeout;

    private Long commandTimeout;

    private Long reconnectInteval;

    private Long reconnectTimes;

    private Long status;

    private Long updateDate;

    public DaCoreConnector() {
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectApproach() {
        return connectApproach;
    }

    public void setConnectApproach(String connectApproach) {
        this.connectApproach = connectApproach;
    }

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSqldas() {
        return sqldas;
    }

    public void setSqldas(String sqldas) {
        this.sqldas = sqldas;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Long getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(Long commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public Long getReconnectInteval() {
        return reconnectInteval;
    }

    public void setReconnectInteval(Long reconnectInteval) {
        this.reconnectInteval = reconnectInteval;
    }

    public Long getReconnectTimes() {
        return reconnectTimes;
    }

    public void setReconnectTimes(Long reconnectTimes) {
        this.reconnectTimes = reconnectTimes;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getArchivedDatabase() {
        return archivedDatabase;
    }

    public void setArchivedDatabase(String archivedDatabase) {
        this.archivedDatabase = archivedDatabase;
    }
}
