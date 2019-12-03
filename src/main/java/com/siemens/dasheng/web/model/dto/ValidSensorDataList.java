package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/1/2.
 */
@ApiModel(value = "单个测点的验证数据" )
public class ValidSensorDataList {

    @ApiModelProperty(value = "测点编码")
    private String sensorCode;

    @ApiModelProperty(value = "测点历史数据")
    private List<SensorData> sensorDataList;

    public String getSensorCode() {
        return sensorCode;
    }

    public void setSensorCode(String sensorCode) {
        this.sensorCode = sensorCode;
    }

    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }

    public void setSensorDataList(List<SensorData> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }
}
