package com.siemens.dasheng.web.request;

/**
 * @author xuxin
 * DaConfigProviderRequest
 * created by xuxin on 15/5/2019
 */
public class QuerySensorAppListRequest {
    private String objectId;

    private String objectType;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}
