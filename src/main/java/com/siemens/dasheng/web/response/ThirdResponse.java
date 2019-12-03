package com.siemens.dasheng.web.response;

import java.util.List;

/**
 * @author allan
 * Created by z0041dpv on 5/7/2019.
 */
public class ThirdResponse {
    /**
     * 不合法sensor
     */
    private List<String> inValidSensorList;
    /**
     *合法sensor
     */
    private List<String> vaildSensorList;
    /**
     * 保存成功sensor
     */
    private List<String> okSensorList;
    /**
     * 保存失败sensor
     */
    private List<String> failSensorList;

    public List<String> getInValidSensorList() {
        return inValidSensorList;
    }

    public void setInValidSensorList(List<String> inValidSensorList) {
        this.inValidSensorList = inValidSensorList;
    }

    public List<String> getVaildSensorList() {
        return vaildSensorList;
    }

    public void setVaildSensorList(List<String> vaildSensorList) {
        this.vaildSensorList = vaildSensorList;
    }

    public List<String> getOkSensorList() {
        return okSensorList;
    }

    public void setOkSensorList(List<String> okSensorList) {
        this.okSensorList = okSensorList;
    }

    public List<String> getFailSensorList() {
        return failSensorList;
    }

    public void setFailSensorList(List<String> failSensorList) {
        this.failSensorList = failSensorList;
    }
}
