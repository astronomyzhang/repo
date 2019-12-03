package com.siemens.dasheng.web.response;

/**
 * @author liming
 * @Date: 2019/4/8 10:43
 */
public class AplicationName {

    private Long id;

    private String name;

    private String appid;

    private String globalAppId;

    private Integer type;

    public String getGlobalAppId() {
        return globalAppId;
    }

    public void setGlobalAppId(String globalAppId) {
        this.globalAppId = globalAppId;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
