package com.siemens.dasheng.web.generalmodel.dataconnector;

import java.util.List;

/**
 * @author liming
 * @Date: 2019/1/7 14:19
 */
public class DeleteConnectorResponse {

    private List<String> names;

    private Integer checkNo;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public Integer getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(Integer checkNo) {
        this.checkNo = checkNo;
    }
}
