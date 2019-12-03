package com.siemens.dasheng.web.response;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * da data access response
 *
 * @author ly
 * @date 2019/06/25
 */
public class DaWorkerClientPointResponse {

    /**
     * 正常返回的数据
     */
    private Map<String, List<DaWorkerClientPoint>> normalPointList = new HashMap<>(0);

    /**
     * 查询中不存在的测点ID列表
     */
    private List<String> notExistSensorIDList = new ArrayList<>(0);

    /**
     * 查询历史间隔读取中，查询结果不符合时间间隔的测点ID列表
     */
    private List<String> errorIntervalSensorIDList = new ArrayList<>(0);

    public Map<String, List<DaWorkerClientPoint>> getNormalPointList() {
        return normalPointList;
    }

    public void setNormalPointList(Map<String, List<DaWorkerClientPoint>> normalPointList) {
        this.normalPointList = normalPointList;
    }

    public List<String> getNotExistSensorIDList() {
        return notExistSensorIDList;
    }

    public void setNotExistSensorIDList(List<String> notExistSensorIDList) {
        this.notExistSensorIDList = notExistSensorIDList;
    }

    public List<String> getErrorIntervalSensorIDList() {
        return errorIntervalSensorIDList;
    }

    public void setErrorIntervalSensorIDList(List<String> errorIntervalSensorIDList) {
        this.errorIntervalSensorIDList = errorIntervalSensorIDList;
    }

    /**
     * one point of da data
     *
     * @author ly
     * @date 2019/06/25
     */
    public static class DaWorkerClientPoint {

        private String sensorID;

        private String value;

        private Long timestamp;

        private boolean quality;

        public String getSensorID() {
            return sensorID;
        }

        public void setSensorID(String sensorID) {
            this.sensorID = sensorID;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public boolean getQuality() {
            return quality;
        }

        public void setQuality(boolean quality) {
            this.quality = quality;
        }
    }
}
