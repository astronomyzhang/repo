package com.siemens.dasheng.web.response;

/**
 * DaConfig sensor sync response
 *
 * @author ly
 * @date 2019/05/15
 */
public class DaConfigSensorSyncResponse {

    /**
     * 测点编码
     */
    private String siecode;

    /**
     * kks code
     */
    private String kkscode;

    /**
     * 测点描述
     */
    private String title;

    /**
     * 测点来自于注册
     */
    private String fromRegister;

    /**
     * 测点单位
     */
    private String systemunit;

    public String getSiecode() {
        return siecode;
    }

    public void setSiecode(String siecode) {
        this.siecode = siecode;
    }

    public String getKkscode() {
        return kkscode;
    }

    public void setKkscode(String kkscode) {
        this.kkscode = kkscode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSystemunit() {
        return systemunit;
    }

    public void setSystemunit(String systemunit) {
        this.systemunit = systemunit;
    }

    public String getFromRegister() {
        return fromRegister;
    }

    public void setFromRegister(String fromRegister) {
        this.fromRegister = fromRegister;
    }
}
