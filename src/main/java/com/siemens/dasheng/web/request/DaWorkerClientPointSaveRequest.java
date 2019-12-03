package com.siemens.dasheng.web.request;

import java.util.List;

/**
 * @author ly
 * @date 2019/07/02
 */
public class DaWorkerClientPointSaveRequest {

    private List<Point> pointList;

    public List<Point> getPointList() {
        return pointList;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }

    public static class Point {
        private String sensorID;

        private Double value;

        private Long timestamp;

        public String getSensorID() {
            return sensorID;
        }

        public void setSensorID(String sensorID) {
            this.sensorID = sensorID;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
