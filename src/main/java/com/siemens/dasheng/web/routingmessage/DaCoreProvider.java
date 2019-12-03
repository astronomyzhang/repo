package com.siemens.dasheng.web.routingmessage;

import java.util.Set;

/**
 * 
 * @author ly
 * Jan 18, 2019
 *	 数据接收类，用于接收从da_core模块中获取的provider信息
 */
public class DaCoreProvider {
	private Integer providerId;
	private Set<DaCoreConnector> connectorSet;

	public DaCoreProvider() {
	}

	public Integer getProviderId() {
		return providerId;
	}

	public void setProviderId(Integer providerId) {
		this.providerId = providerId;
	}

	public Set<DaCoreConnector> getConnectorSet() {
		return connectorSet;
	}

	public void setConnectorSet(Set<DaCoreConnector> connectorSet) {
		this.connectorSet = connectorSet;
	}

}
