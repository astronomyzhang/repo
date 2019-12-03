package com.siemens.dasheng.web.model.dto;

/**
 * @author allan
 * Created by z0041dpv on 5/14/2019.
 */
public class MultiProivderInfo {

    /**
     * 重复providername
     */
    private String  preName;
    /**
     *重复providername
     */
    private String nextName;
    /**
     * 重复连接器
     */
    private String repeatConnectorName;

    public String getPreName() {
        return preName;
    }

    public void setPreName(String preName) {
        this.preName = preName;
    }

    public String getNextName() {
        return nextName;
    }

    public void setNextName(String nextName) {
        this.nextName = nextName;
    }

    public String getRepeatConnectorName() {
        return repeatConnectorName;
    }

    public void setRepeatConnectorName(String repeatConnectorName) {
        this.repeatConnectorName = repeatConnectorName;
    }
}
