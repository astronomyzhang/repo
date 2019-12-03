package com.siemens.dasheng.web.request;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author allan
 * Created by z0041dpv on 4/18/2019.
 */
public class GetCrewSensorGroupRequest {


    @ApiModelProperty("机组id")
    private String crewId;

    @ApiModelProperty("搜索关键字")
    private String searchStr;

    @ApiModelProperty("电厂id")
    private String powerPlantId;

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

    public String getSearchStr() {
        return searchStr;
    }

    public void setSearchStr(String searchStr) {
        this.searchStr = searchStr;
    }
}
