package com.siemens.dasheng.web.generalmodel.dataconnector;

/**
 * @author liming
 * @Date: 2019/1/7 8:26
 */
public class QueryConnectorResponse {

    private Integer id;

    private String databaseName;

    private String approach;

    private String name;

    private String address;

    private Integer quoteNo;

    private Integer activateNo;

    private String description;

    private String sqldas;

    private String port;

    private Long status;

    private String archivedDatabase;

    private String approachId;

    private String database;

    private String dbName;

    private String url;

    private String daServerName;

    private String hdaServerName;

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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getArchivedDatabase() {
        return archivedDatabase;
    }

    public void setArchivedDatabase(String archivedDatabase) {
        this.archivedDatabase = archivedDatabase;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }



    public String getApproachId() {
        return approachId;
    }

    public void setApproachId(String approachId) {
        this.approachId = approachId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getSqldas() {
        return sqldas;
    }

    public void setSqldas(String sqldas) {
        this.sqldas = sqldas;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getApproach() {
        return approach;
    }

    public void setApproach(String approach) {
        this.approach = approach;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(Integer quoteNo) {
        this.quoteNo = quoteNo;
    }

    public Integer getActivateNo() {
        return activateNo;
    }

    public void setActivateNo(Integer activateNo) {
        this.activateNo = activateNo;
    }
}
