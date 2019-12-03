package com.siemens.dasheng.web.request;



/**
 * @author xuxin
 * DaConfigConnectorType
 * created by xuxin on 25/11/2018
 */
public class DaConfigConnectorTypeRequest {


    private String dataType;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }
}
