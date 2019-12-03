package com.siemens.dasheng.web.model.dto;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/7/4.
 */
public class ValidSensorInput {

    private long bgTime;
    private long endTime;
    private String validRuleId;
    private int timeInterval;

    public long getBgTime() {
        return bgTime;
    }

    public void setBgTime(long bgTime) {
        this.bgTime = bgTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getValidRuleId() {
        return validRuleId;
    }

    public void setValidRuleId(String validRuleId) {
        this.validRuleId = validRuleId;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }
}
