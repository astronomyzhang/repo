package com.siemens.dasheng.web.request;

import java.util.List;

/**
 * @author ly
 * @date 2019/06/25
 */
public class DaWorkerClientPointRequest {

    private List<String> sensorIDList;

    private List<TimeRequest> timeMapperList;

    private Integer interval;

    public List<String> getSensorIDList() {
        return sensorIDList;
    }

    public void setSensorIDList(List<String> sensorIDList) {
        this.sensorIDList = sensorIDList;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public List<TimeRequest> getTimeMapperList() {
        return timeMapperList;
    }

    public void setTimeMapperList(List<TimeRequest> timeMapperList) {
        this.timeMapperList = timeMapperList;
    }

    /**
     * 时间片段
     */
    public static class TimeRequest {
        private Long bgTime;
        private Long endTime;

        public Long getBgTime() {
            return bgTime;
        }

        public void setBgTime(Long bgTime) {
            this.bgTime = bgTime;
        }

        public Long getEndTime() {
            return endTime;
        }

        public void setEndTime(Long endTime) {
            this.endTime = endTime;
        }
    }
}
