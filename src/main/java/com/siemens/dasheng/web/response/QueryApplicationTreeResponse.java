package com.siemens.dasheng.web.response;

import com.siemens.dasheng.web.model.DaConfigApplication;
import com.siemens.dasheng.web.model.DaConfigApplicationPlus;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class QueryApplicationTreeResponse {
    private List<DaConfigApplicationPlus> applicationList;

    private Integer status;

    public List<DaConfigApplicationPlus> getApplicationList() {
        return applicationList;
    }

    public void setApplicationList(List<DaConfigApplicationPlus> applicationList) {
        this.applicationList = applicationList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
