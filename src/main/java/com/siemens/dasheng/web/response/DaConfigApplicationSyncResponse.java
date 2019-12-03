package com.siemens.dasheng.web.response;

import java.util.List;

/**
 * DaConfig application sync response
 *
 * @author ly
 * @date 2019/05/15
 */
public class DaConfigApplicationSyncResponse {

    /**
     * 需要同步的appId
     */
    private String globalAppId;

    /**
     * 需要同步的appId(内部)
     */
    private Long appId;

    /**
     * 需要同步的app类型(公有、私有)
     */
    private Long type;

    /**
     * 需要同步的app版本号
     */
    private Long appVersion;

    /**
     * 当需要以测点同步时，返回测点列表
     */
    private List<DaConfigSensorSyncResponse> sensorList;

    /**
     * 当需要以测点组同步时，返回测点组列表
     */
    private List<DaConfigGroupSyncResponse> groupList;

    public String getGlobalAppId() {
        return globalAppId;
    }

    public void setGlobalAppId(String globalAppId) {
        this.globalAppId = globalAppId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Long appVersion) {
        this.appVersion = appVersion;
    }

    public List<DaConfigSensorSyncResponse> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<DaConfigSensorSyncResponse> sensorList) {
        this.sensorList = sensorList;
    }

    public List<DaConfigGroupSyncResponse> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<DaConfigGroupSyncResponse> groupList) {
        this.groupList = groupList;
    }
}
