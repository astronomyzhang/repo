package com.siemens.dasheng.web.model.dto;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/4/30.
 */
public class DaMonitorConnector {

    private Long id;
    private String name;

    /**
     * true:connected ; false: not connected
     */
    private Boolean connected;
    /**
     * 0:not available(已分配资源，不可用) / 1:available（已分配资源，可用） / 2: undistributed（未分配资源）
     */
    private Integer availability;
    /**
     * eg: ip:port
     */
    private String workerInfo;

    private String connectApproach;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    public String getWorkerInfo() {
        return workerInfo;
    }

    public void setWorkerInfo(String workerInfo) {
        this.workerInfo = workerInfo;
    }

    public String getConnectApproach() {
        return connectApproach;
    }

    public void setConnectApproach(String connectApproach) {
        this.connectApproach = connectApproach;
    }
}
