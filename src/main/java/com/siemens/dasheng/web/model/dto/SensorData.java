package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/1/2.
 */
@ApiModel(value = "测点数据")
public class SensorData {

    @ApiModelProperty(value = "时间戳")
    private long timeStamp;
    @ApiModelProperty(value = "值")
    private double value;
    @ApiModelProperty(value = "是否被验证")
    private boolean validated;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
