package com.siemens.dasheng.web.log.common;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/10/29.
 */
public class BaseLogObject {

    private String userName;
    private String employeenum;
    private String logTime;
    private String operateType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getEmployeenum() {
        return employeenum;
    }

    public void setEmployeenum(String employeenum) {
        this.employeenum = employeenum;
    }
}
