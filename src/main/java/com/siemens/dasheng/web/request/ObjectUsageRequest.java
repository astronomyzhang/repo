package com.siemens.dasheng.web.request;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
public class ObjectUsageRequest {
    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("objectId")
    private String objectId;

    @ApiModelProperty("objectId")
    private List<String> objectIds;

    @ApiModelProperty("objectType")
    private String objectType;

    @ApiModelProperty("opertionType")
    private String opertionType;

    @ApiModelProperty("消费对象，eg：mdodel")
    private String consumerObject;


    @ApiModelProperty("消费对象描述")
    private String consumerDescription;

    @ApiModelProperty("消费对象id")
    private String consumerObjectId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

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

    public String getOpertionType() {
        return opertionType;
    }

    public void setOpertionType(String opertionType) {
        this.opertionType = opertionType;
    }

    public String getConsumerObject() {
        return consumerObject;
    }

    public void setConsumerObject(String consumerObject) {
        this.consumerObject = consumerObject;
    }

    public String getConsumerDescription() {
        return consumerDescription;
    }

    public void setConsumerDescription(String consumerDescription) {
        this.consumerDescription = consumerDescription;
    }

    public String getConsumerObjectId() {
        return consumerObjectId;
    }

    public void setConsumerObjectId(String consumerObjectId) {
        this.consumerObjectId = consumerObjectId;
    }

    public List<String> getObjectIds() {
        return objectIds;
    }

    public void setObjectIds(List<String> objectIds) {
        this.objectIds = objectIds;
    }
}
