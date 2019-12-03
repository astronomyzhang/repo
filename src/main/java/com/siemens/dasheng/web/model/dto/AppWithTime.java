package com.siemens.dasheng.web.model.dto;

import com.siemens.dasheng.web.model.DaConfigApplication;

/**
 * @author liming
 * @Date: 2019/4/8 13:03
 */
public class AppWithTime  extends DaConfigApplication {

    private Long dateTime;

    private String userName;

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
