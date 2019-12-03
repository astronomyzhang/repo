package com.siemens.dasheng.web.request;

import java.util.List;

/**
 * @description: data config sensor group request
 * @author: ly
 * @create: 2019-04-04 15:24
 */
public class DaConfigGroupRequest {

    private Long groupId;

    private String groupName;

    private String groupDescription;

    private List<String> sensorList;

    private Long appId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public List<String> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<String> sensorList) {
        this.sensorList = sensorList;
    }

    @Override
    public String toString() {
        return "DaConfigGroupRequest={groupId : " + groupId + ", groupName : " + groupName
                + ", groupDescription : " + groupDescription + ", appId : " + appId + ", sensorList"
                + sensorList + "}";
    }
}
