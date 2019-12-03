package com.siemens.dasheng.web.model.dto;

import java.util.List;

/**
 * @author xuxin
 * Created by xuxin on 9/8/2019.
 */
public class PiWebServiceRequest {

    private String serverhost;
    private String passwd;
    private String username;
    private String afdbname;
    private String tag;
    private String querystr;
    private String pointlist;

    public String getPointlist() {
        return pointlist;
    }

    public void setPointlist(String pointlist) {
        this.pointlist = pointlist;
    }

    public String getQuerystr() {
        return querystr;
    }

    public void setQuerystr(String querystr) {
        this.querystr = querystr;
    }

    public String getServerhost() {
        return serverhost;
    }

    public void setServerhost(String serverhost) {
        this.serverhost = serverhost;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAfdbname() {
        return afdbname;
    }

    public void setAfdbname(String afdbname) {
        this.afdbname = afdbname;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
