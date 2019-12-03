package com.siemens.dasheng.web.model;

/**
 * @author allan
 * Created by ofm on 2019/4/3.
 */
public class DaCrewConfigGroup {
    private Long id;

    private String crewId;

    private Long groupId;

    private Long appId;

    private String powerPlantId;

    public DaCrewConfigGroup(Long id, String crewId, Long groupId, Long appId, String powerPlantId) {
        this.id = id;
        this.crewId = crewId;
        this.groupId = groupId;
        this.appId = appId;
        this.powerPlantId = powerPlantId;
    }

    public String getPowerPlantId() {
        return powerPlantId;
    }

    public void setPowerPlantId(String powerPlantId) {
        this.powerPlantId = powerPlantId;
    }

    public DaCrewConfigGroup() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewId(String crewId) {
        this.crewId = crewId == null ? null : crewId.trim();
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }
}