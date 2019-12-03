package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/15/2019.
 */
public class MultiConnectorInfo {
    /**
     * 重复connectorName
     */
    private String  connectorName;
    /**
     *重复providername
     */
    private String repeatProviderName;

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getRepeatProviderName() {
        return repeatProviderName;
    }

    public void setRepeatProviderName(String repeatProviderName) {
        this.repeatProviderName = repeatProviderName;
    }
}
