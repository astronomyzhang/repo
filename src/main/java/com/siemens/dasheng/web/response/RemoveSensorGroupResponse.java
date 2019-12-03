package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaAppResourceUsage;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/5/28
 */
public class RemoveSensorGroupResponse {

    private Integer status;

    private List<DaAppResourceUsage> sensorList;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<DaAppResourceUsage> getSensorList() {
        return sensorList;
    }

    public void setSensorList(List<DaAppResourceUsage> sensorList) {
        this.sensorList = sensorList;
    }
}
