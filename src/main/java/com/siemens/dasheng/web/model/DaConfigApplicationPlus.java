package com.siemens.dasheng.web.model;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/5
 */
public class DaConfigApplicationPlus extends DaConfigApplication {
    private List<DaConfigProvider> providerList;

    public List<DaConfigProvider> getProviderList() {
        return providerList;
    }

    public void setProviderList(List<DaConfigProvider> providerList) {
        this.providerList = providerList;
    }
}
