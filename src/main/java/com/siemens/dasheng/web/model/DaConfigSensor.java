package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/7
 */
public class DaConfigSensor {
    private String siecode;

    private Long connectorId;

    private String tag;

    private Long appId;

    private Long status;

    private String description;

    private String unit;
    /**
     * 1,registed in system;0:not registed in system
     */
    private Integer fromRegist;


    public DaConfigSensor(String siecode, Long connectorId, String tag, Long appId, Long status, String description, String unit, Integer fromRegist) {
        this.siecode = siecode;
        this.connectorId = connectorId;
        this.tag = tag;
        this.appId = appId;
        this.status = status;
        this.description = description;
        this.unit = unit;
        this.fromRegist = fromRegist;
    }

    public DaConfigSensor() {
        super();
    }

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode == null ? null : siecode.trim();
    }

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getFromRegist() {
        return fromRegist;
    }

    public void setFromRegist(Integer fromRegist) {
        this.fromRegist = fromRegist;
    }
}