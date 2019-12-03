package com.siemens.dasheng.web.routingmessage;

import java.util.Set;

/**
 * 
 * @author ly
 * Jan 18, 2019
 * 	数据接收类，用于接收从da_core模块中获取的connector信息
 */
public class DaCoreConnector {
	private Integer connectorId;
	private Set<DaCoreSensor> sensorSet;

	public DaCoreConnector() {
	}

	public Integer getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(Integer connectorId) {
		this.connectorId = connectorId;
	}


	public Set<DaCoreSensor> getSensorSet() {
		return sensorSet;
	}

	public void setSensorSet(Set<DaCoreSensor> sensorSet) {
		this.sensorSet = sensorSet;
	}
}
