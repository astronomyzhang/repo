package com.siemens.dasheng.web.response;

import java.util.List;

/**
 * @description: data config group response
 * @author: ly
 * @create: 2019-04-04 16:43
 */
public class DaConfigGroupResponse {


    private Long groupId;

    private String groupName;

    private String groupDescription;

    private List<String> sensorList;

    private List<String> usedSensorList;

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

    public List<String> getUsedSensorList() {
        return usedSensorList;
    }

    public void setUsedSensorList(List<String> usedSensorList) {
        this.usedSensorList = usedSensorList;
    }

}
