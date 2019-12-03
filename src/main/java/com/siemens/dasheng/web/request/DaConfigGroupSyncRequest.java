package com.siemens.dasheng.web.request;

/**
 * DaConfig group sync request
 *
 * @author ly
 * @date 2019/05/15
 */
public class DaConfigGroupSyncRequest {


    /**
     * 需要同步的测点组id
     */
    private Long groupId;

    /**
     * 需要同步的测点版本号
     */
    private Long groupVersion;

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
}
