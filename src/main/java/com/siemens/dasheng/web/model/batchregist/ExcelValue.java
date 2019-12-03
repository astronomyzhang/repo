package com.siemens.dasheng.web.model.batchregist;

/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class ExcelValue {

    private String tag;

    private String createTag;

    private String sensorCode;

    private String sensorDescription;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCreateTag() {
        return createTag;
    }

    public void setCreateTag(String createTag) {
        this.createTag = createTag;
    }

    public String getSensorCode() {
        return sensorCode;
    }

    public void setSensorCode(String sensorCode) {
        this.sensorCode = sensorCode;
    }

    public String getSensorDescription() {
        return sensorDescription;
    }

    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }
}
