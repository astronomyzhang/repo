package com.siemens.dasheng.web.model;


import com.siemens.dasheng.web.validators.ConnectorValidator;

/**
 * @author xuxin DaConnectorConfig created by xuxin on 15/11/2018
 * 
 * da_worder connector配置类
 */
public class DaConnectorConfig {

	private Long id;

	private String name;

	private String dataType;

	private String connectorType;

	private String archivedDatabase;

	private String connectApproach;

	private String description;

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

	private String connectorClass;

	private String databaseName;

	private String categoryName;

	private String updatedBy;

	private Long updateDate;

	private String dbName;

	private String database;

	private String connectorInfo;

	private String url;

	private String daServerName;

	private String hdaServerName;

	private Long gateway;


	public DaConnectorConfig() {
	}

	public DaConnectorConfig(Long id, String name, String dataType, String connectorType, String archivedDatabase,
                             String connectApproach, String description, String serverHost, String port, String sqldas, String userName,
                             String password, Long connectTimeout, Long commandTimeout, Long reconnectInteval, Long reconnectTimes,
                             Long status, String connectorClass, String updatedBy, Long updateDate) {
		this.id = id;
		this.name = name;
		this.dataType = dataType;
		this.connectorType = connectorType;
		this.archivedDatabase = archivedDatabase;
		this.connectApproach = connectApproach;
		this.description = description;
		this.serverHost = serverHost;
		this.port = port;
		this.sqldas = sqldas;
		this.userName = userName;
		this.password = password;
		this.connectTimeout = connectTimeout;
		this.commandTimeout = commandTimeout;
		this.reconnectInteval = reconnectInteval;
		this.reconnectTimes = reconnectTimes;
		this.status = status;
		this.connectorClass = connectorClass;
		this.updatedBy = updatedBy;
		this.updateDate = updateDate;

	}


	
	


	/**
	 * 仅在内部使用
	 * @return
	 */
	@Override
	public int hashCode() {
		return (null == this.id) ? 0 : this.id.hashCode();
	}

	/**
	 * 仅在worker内部调用，被调用的入口仅为缓存类ConnectorPoolMappingCache，
	 * 外部使用会发生问题
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof DaConnectorConfig)) {
			return false;
		}
		DaConnectorConfig connector = (DaConnectorConfig) obj;

		return connector.getId().equals(this.id);
	}

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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType == null ? null : dataType.trim();
	}

	public String getConnectorType() {
		return connectorType;
	}

	public void setConnectorType(String connectorType) {
		this.connectorType = connectorType == null ? null : connectorType.trim();
	}

	public String getArchivedDatabase() {
		return archivedDatabase;
	}

	public void setArchivedDatabase(String archivedDatabase) {
		this.archivedDatabase = archivedDatabase == null ? null : archivedDatabase.trim();
	}

	public String getConnectApproach() {
		return connectApproach;
	}

	public void setConnectApproach(String connectApproach) {
		this.connectApproach = connectApproach == null ? null : connectApproach.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost == null ? null : serverHost.trim();
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port == null ? null : port.trim();
	}

	public String getSqldas() {
		return sqldas;
	}

	public void setSqldas(String sqldas) {
		this.sqldas = sqldas == null ? null : sqldas.trim();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
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

	public String getConnectorClass() {
		return connectorClass;
	}

	public void setConnectorClass(String connectorClass) {
		this.connectorClass = connectorClass;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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
}