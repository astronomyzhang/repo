package com.siemens.dasheng.web.request;

import java.util.List;

/**
 * DaConfig application sync request
 *
 * @author ly
 * @date 2019/05/15
 */
public class DaConfigApplicationSyncRequest {

    /**
     * 需要同步的appId
     */
    private String globalAppId;

    /**
     * 需要同步的app版本号
     */
    private Long appVersion;

    /**
     * 需要同步的测点组（当需要以测点组同步时，同步方需要发送该信息，否则为null即可）
     */
    private List<DaConfigGroupSyncRequest> groupList;

    public String getGlobalAppId() {
        return globalAppId;
    }

    public void setGlobalAppId(String globalAppId) {
        this.globalAppId = globalAppId;
    }

    public Long getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Long appVersion) {
        this.appVersion = appVersion;
    }

    public List<DaConfigGroupSyncRequest> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<DaConfigGroupSyncRequest> groupList) {
        this.groupList = groupList;
    }

}
