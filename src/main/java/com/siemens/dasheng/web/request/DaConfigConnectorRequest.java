package com.siemens.dasheng.web.request;


import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.validators.interfaces.CharacterBytesLength;
import com.siemens.dasheng.web.validators.interfaces.NumberBytesLength;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;

/**
 * @author xuxin
 * DaConfigConnectorRequest
 * created by xuxin on 15/11/2018
 */
public class DaConfigConnectorRequest {

    private Long id;

    private String name;

    private String dataType;

    private String tagSetStr;

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagSetStr() {
        return tagSetStr;
    }

    public void setTagSetStr(String tagSetStr) {
        this.tagSetStr = tagSetStr;
    }

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_CONNECTOR_TYPE)
    @CharacterBytesLength(message = CONNECTOR_TYPE_LENGTH_MAXSIZE_30,value = 30)
    private String connectorType;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_ARCHIVED_DATABASE)
    @CharacterBytesLength(message = ARCHIVED_DATABASE_LENGTH_MAXSIZE_30,value = 30)
    private String archivedDatabase;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_CONNECT_APPROACH)
    @CharacterBytesLength(message = CONNECT_APPROACH_LENGTH_MAXSIZE_30,value = 30)
    private String connectApproach;

    private String description;

    @CharacterBytesLength(message = CONNECTOR_SERVER_HOST_LENGTH_MAXSIZE_30,value = 30)
    private String serverHost;

    @CharacterBytesLength(message = PORT_LENGTH_MAXSIZE_10,value = 10)
    private String port;

    @CharacterBytesLength(message = CONNECTOR_SQLDAS_LENGTH_MAXSIZE_30,value = 30)
    private String sqldas;

    @CharacterBytesLength(message = CONNECTOR_USERNAME_LENGTH_MAXSIZE_20,value = 20)
    private String userName;

    @CharacterBytesLength(message = CONNECTOR_PASSWORD_LENGTH_MAXSIZE_20,value = 20)
    private String password;

    private Long connectTimeout;

    private Long commandTimeout;

    private Long reconnectInteval;

    private Long reconnectTimes;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_CONNECT_CLASS)
    @CharacterBytesLength(message = CONNECTOR_CLASS_LENGTH_MAXSIZE_30,value = 30)
    private String connectorClass;

    private String updatedBy;

    private Long updateDate;

    @CharacterBytesLength(message = DB_NAME_LENGTH_MAXSIZE_20,value = 20)
    private String dbName;

    @CharacterBytesLength(message = DATABASE_LENGTH_MAXSIZE_20,value = 20)
    private String database;

    private Long applicationId;

    private List<Long> applicationIdList;

    private String connectorInfo;

    private String url;

    private String daServerName;

    private String hdaServerName;

    private Long gateway;

    public Long getGateway() {
        return gateway;
    }

    public void setGateway(Long gateway) {
        this.gateway = gateway;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDaServerName() {
        return daServerName;
    }

    public void setDaServerName(String daServerName) {
        this.daServerName = daServerName;
    }

    public String getHdaServerName() {
        return hdaServerName;
    }

    public void setHdaServerName(String hdaServerName) {
        this.hdaServerName = hdaServerName;
    }

    public String getConnectorInfo() {
        return connectorInfo;
    }

    public void setConnectorInfo(String connectorInfo) {
        this.connectorInfo = connectorInfo;
    }

    public List<Long> getApplicationIdList() {
        return applicationIdList;
    }

    public void setApplicationIdList(List<Long> applicationIdList) {
        this.applicationIdList = applicationIdList;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public String getArchivedDatabase() {
        return archivedDatabase;
    }

    public void setArchivedDatabase(String archivedDatabase) {
        this.archivedDatabase = archivedDatabase;
    }

    public String getConnectApproach() {
        return connectApproach;
    }

    public void setConnectApproach(String connectApproach) {
        this.connectApproach = connectApproach;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static DaConfigConnectorRequest format(DaConfigConnector daConfigConnector) {
        DaConfigConnectorRequest request = new DaConfigConnectorRequest();
        BeanUtils.copyProperties(daConfigConnector, request);
        return request;
    }
}
