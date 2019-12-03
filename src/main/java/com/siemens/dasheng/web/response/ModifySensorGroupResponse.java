package com.siemens.dasheng.web.response;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/5/28
 */
public class ModifySensorGroupResponse {
    private Integer status;

    private List<String> sensorCodeList;

    private List<Map<String,Object>> userdSensorList;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getSensorCodeList() {
        return sensorCodeList;
    }

    public void setSensorCodeList(List<String> sensorCodeList) {
        this.sensorCodeList = sensorCodeList;
    }

    public List<Map<String, Object>> getUserdSensorList() {
        return userdSensorList;
    }

    public void setUserdSensorList(List<Map<String, Object>> userdSensorList) {
        this.userdSensorList = userdSensorList;
    }
}
