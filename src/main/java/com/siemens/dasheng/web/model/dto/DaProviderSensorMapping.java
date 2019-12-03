package com.siemens.dasheng.web.model.dto;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/2/22.
 */
public class DaProviderSensorMapping {

    private String sieCode;
    private int connectorId;
    private String tag;

    public String getSieCode() {
        return sieCode;
    }

    public void setSieCode(String sieCode) {
        this.sieCode = sieCode;
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
