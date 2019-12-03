package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author yaming.chen@siemens.com
 *         Created by chenyaming on 2018/1/2.
 */
@ApiModel(value = "验证测点服务输出对象")
public class OutputValidSensorDTO {

    /**
     * 输出已验证的测点数据
     */
    @ApiModelProperty(value = "输出已验证的测点数据")
    private List<ValidSensorDataList> outputSensorDataList;

    public List<ValidSensorDataList> getOutputSensorDataList() {
        return outputSensorDataList;
    }

    public void setOutputSensorDataList(List<ValidSensorDataList> outputSensorDataList) {
        this.outputSensorDataList = outputSensorDataList;
    }

    @Override
    public String toString() {
        return "OutputValidSensorDTO{" +
                "outputSensorDataList=" + outputSensorDataList +
                '}';
    }
}
