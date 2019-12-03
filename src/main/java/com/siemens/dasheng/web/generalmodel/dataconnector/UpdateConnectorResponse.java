package com.siemens.dasheng.web.generalmodel.dataconnector;

import java.util.List;

/**
 * @author liming
 * @Date: 2019/1/8 15:18
 */
public class UpdateConnectorResponse {

    private Boolean isUpdate;

    private List<String> names;

    private int status;

    private String repeatNames;

    private String matchName;

    private Integer num;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getRepeatNames() {
        return repeatNames;
    }

    public void setRepeatNames(String repeatNames) {
        this.repeatNames = repeatNames;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Boolean getUpdate() {
        return isUpdate;
    }

    public void setUpdate(Boolean update) {
        isUpdate = update;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }
}
