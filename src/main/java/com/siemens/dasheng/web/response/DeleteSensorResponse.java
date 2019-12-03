package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaAppResourceUsage;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/5/28
 */
public class DeleteSensorResponse {

    private Integer status;

    private List<DaAppResourceUsage> usages;

    public List<DaAppResourceUsage> getUsages() {
        return usages;
    }

    public void setUsages(List<DaAppResourceUsage> usages) {
        this.usages = usages;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
