package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/5
 */
public class DaConfigConnectorSensor {
    private Long id;

    private String connectorInfo;

    private String tag;

    /**
     * 关联的最小的一个connectorId
     */
    private Long connectorId;

    /**
     * 0,unimport；1，imported;2,unexist
     */
    private Long status;

    private String unit;

    /**
     * 1,registed in system;0:not registed in system
     */
    private Integer fromRegist;

    private String daPrefix;

    private String hdaPrefix;

    public DaConfigConnectorSensor(Long id, String connectorInfo, String tag, Long status, String unit, Long connectorId,Integer fromRegist, String daPrefix,String hdaPrefix) {
        this.id = id;
        this.connectorInfo = connectorInfo;
        this.tag = tag;
        this.status = status;
        this.unit = unit;
        this.connectorId = connectorId;
        this.fromRegist= fromRegist;
        this.daPrefix = daPrefix;
        this.hdaPrefix = hdaPrefix;
    }

    public DaConfigConnectorSensor() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConnectorInfo() {
        return connectorInfo;
    }

    public void setConnectorInfo(String connectorInfo) {
        this.connectorInfo = connectorInfo;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public Integer getFromRegist() {
        return fromRegist;
    }

    public void setFromRegist(Integer fromRegist) {
        this.fromRegist = fromRegist;
    }

    public String getDaPrefix() {
        return daPrefix;
    }

    public void setDaPrefix(String daPrefix) {
        this.daPrefix = daPrefix;
    }

    public String getHdaPrefix() {
        return hdaPrefix;
    }

    public void setHdaPrefix(String hdaPrefix) {
        this.hdaPrefix = hdaPrefix;
    }
}