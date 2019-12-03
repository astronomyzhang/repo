package com.siemens.dasheng.web.generalmodel.dataconnector;

import com.siemens.dasheng.web.validators.interfaces.CharacterBytesLength;
import com.siemens.dasheng.web.validators.interfaces.NumberBytesLength;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;

/**
 * @author liming
 * @Date: 2019/1/8 10:53
 */
public class UpdateConnectorRequest {

    @NotNull(message = ID_NOT_BLANK)
    private Integer id;

    @NotBlank(message = NAME_NOT_BLANK)
    @CharacterBytesLength(message = NAME_LENGTH_MAXSIZE_30,value = 30)
    private String name;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_ARCHIVED_DATABASE)
    @CharacterBytesLength(message = ARCHIVED_DATABASE_LENGTH_MAXSIZE_30,value = 30)
    private String archivedDatabase;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_CONNECT_APPROACH)
    @CharacterBytesLength(message = CONNECT_APPROACH_LENGTH_MAXSIZE_30,value = 30)
    private String connectApproach;

    @CharacterBytesLength(message = DESCRIPTION_LENGTH_MAXSIZE_600,value = 600)
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

    private Boolean isConfirm;

    private Long status;

    private String connectorType;

    private String connectorClass;

    @NotBlank(message = DA_CONFIG_CONNECTOR_NOTBLANK_DB_NAME)
    @CharacterBytesLength(message = DB_NAME_LENGTH_MAXSIZE_20,value = 20)
    private String dbName;

    @CharacterBytesLength(message = DATABASE_LENGTH_MAXSIZE_20,value = 20)
    private String database;

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

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    public String getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(String connectorClass) {
        this.connectorClass = connectorClass;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getConfirm() {
        return isConfirm;
    }

    public void setConfirm(Boolean confirm) {
        isConfirm = confirm;
    }
}
