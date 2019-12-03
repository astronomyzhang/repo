package com.siemens.dasheng.web.enums;

/**
 * @author ly
 * @date 2019/07/11
 */
public enum DataExcelEnum {

    /**
     * 导出excel表名
     */
    TABLE_NAME("sensor detail view", "测点详细一览表"),

    /**
     * 导出excel sheet名
     */
    SHEET_NAME("sensor detail sheet", "测点详细表"),

    /**
     * 导出excel测点编码
     */
    SENSOR_CODE("Sensor Code", "测点编码"),

    /**
     * 导出excel测点标签
     */
    SENSOR_TAG("Tag", "标签"),

    /**
     * 导出excel测点时间
     */
    SENSOR_TIME("Time", "时间"),

    /**
     * 导出excel表名
     */
    SENSOR_VALUE("Value", "值"),

    /**
     * 导出excel测点质量
     */
    SENSOR_QUALITY("Quality", "质量");

    DataExcelEnum(String ecode, String ccode) {
        this.ecode = ecode;
        this.ccode = ccode;
    }

    /**
     * excel表中英文码
     */
    private final String ecode;

    /**
     * excel表中中文码
     */
    private final String ccode;

    public String getEcode() {
        return ecode;
    }

    public String getCcode() {
        return ccode;
    }
}
