package com.siemens.dasheng.web.request;

import javax.validation.constraints.NotNull;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_CONNECTOR_ID;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_SENSOR_CODE;

/**
 * 批量导入 连接信息
 * @author zhanglimg
 * @date 2019/3/8
 */
public class ConnectInfoRequest {

    private Long connectorId;

    private Long providerId;

    public Long getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Long connectorId) {
        this.connectorId = connectorId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

}
