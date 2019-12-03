package com.siemens.dasheng.web.model.dto;

/**
 * 配置测点实体
 *
 * @author jin.liu.ext@siemens.com
 * @date 2019/7/18
 */
public class DaConfigSensorDTO {

    private String siecode;

    private String unit;

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "DaConfigSensorDTO{" +
                "siecode='" + siecode + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}