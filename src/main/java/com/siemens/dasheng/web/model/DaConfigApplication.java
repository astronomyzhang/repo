package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
public class DaConfigApplication {
    private Long id;

    private Long ffAppId;

    private Long type;

    private Long registerDate;

    private String registerUserId;

    private Long sensormappingUpdateTime;

    /**
     * fleet frame 全局唯一appid
     */
    private String globalAppId;

    public DaConfigApplication(Long id, Long ffAppId, Long type, Long registerDate, String registerUserId,
                               Long sensormappingUpdateTime, String globalAppId) {
        this.id = id;
        this.ffAppId = ffAppId;
        this.type = type;
        this.registerDate = registerDate;
        this.registerUserId = registerUserId;
        this.sensormappingUpdateTime = sensormappingUpdateTime;
        this.globalAppId = globalAppId;
    }

    public Long getSensormappingUpdateTime() {
        return sensormappingUpdateTime;
    }

    public void setSensormappingUpdateTime(Long sensormappingUpdateTime) {
        this.sensormappingUpdateTime = sensormappingUpdateTime;
    }

    public DaConfigApplication() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFfAppId() {
        return ffAppId;
    }

    public void setFfAppId(Long ffAppId) {
        this.ffAppId = ffAppId;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public Long getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Long registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterUserId() {
        return registerUserId;
    }

    public void setRegisterUserId(String registerUserId) {
        this.registerUserId = registerUserId;
    }

    public String getGlobalAppId() {
        return globalAppId;
    }

    public void setGlobalAppId(String globalAppId) {
        this.globalAppId = globalAppId;
    }
}