package com.siemens.dasheng.web.response;

import java.util.List;

/**
 * DaConfig group sync response
 *
 * @author ly
 * @date 2019/05/15
 */
public class DaConfigGroupSyncResponse {

    /**
     * 同步的group id
     */
    private Long groupId;

    /**
     * 同步的group version
     */
    private Long groupVersion;

    /**
     * 同步的group name
     */
    private String groupName;

    /**
     * 同步的group description
     */
    private String groupDescription;

    /**
     * 同步的测点列表
     */
    private List<DaConfigSensorSyncResponse>  sensorList;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getGroupVersion() {
        return groupVersion;
    }

    public void setGroupVersion(Long groupVersion) {
        this.groupVersion = groupVersion;
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

    public List<DaConfigSensorSyncResponse> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<DaConfigSensorSyncResponse> sensorList) {
        this.sensorList = sensorList;
    }
}
