package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaConfigProvider;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class QueryProviderResponse {
    private Long status;

    private List<DaConfigProvider> data;

    private DaConfigProvider daConfigProvider;

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public List<DaConfigProvider> getData() {
        return data;
    }

    public void setData(List<DaConfigProvider> data) {
        this.data = data;
    }

    public DaConfigProvider getDaConfigProvider() {
        return daConfigProvider;
    }

    public void setDaConfigProvider(DaConfigProvider daConfigProvider) {
        this.daConfigProvider = daConfigProvider;
    }
}
