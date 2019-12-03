package com.siemens.dasheng.web.request;

import java.util.List;

/**
 * @author allan
 * Created by z0041dpv on 4/18/2019.
 */
public class DaCrewGroupSaveRequest {

    private String powerPlantId;

    private String crewId;

    private List<Long> groupIds;

    public String getPowerPlantId() {
        return powerPlantId;
    }

    public void setPowerPlantId(String powerPlantId) {
        this.powerPlantId = powerPlantId;
    }

    public String getCrewId() {
        return crewId;
    }

    public void setCrewId(String crewId) {
        this.crewId = crewId;
    }

    public List<Long> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<Long> groupIds) {
        this.groupIds = groupIds;
    }
}
