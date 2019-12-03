package com.siemens.dasheng.web.response;

import java.util.List;
import java.util.Map;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class RemoveProviderConnectorResponse {
    private List<String> appNames;

    private String connectorName;

    private Integer nums;

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public List<String> getAppNames() {
        return appNames;
    }

    public void setAppNames(List<String> appNames) {
        this.appNames = appNames;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }
}
