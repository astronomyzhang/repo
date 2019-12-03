package com.siemens.dasheng.web.apolloconfig;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/1/16.
 */
public class ApolloAppConfig {

    private String dppCoreAppName;
    private String dppEarlywarningAppName;
    private String dppUserAppName;
    private String dppSensorAppName;
    private String dppAlarmControllerAppName;
    private String dppSchedulerAppName;
    private String dppCwsAppName;
    private String ofmDaConfigAgentAppName;
    private String ssoServerName;

    private static ApolloAppConfig ourInstance = new ApolloAppConfig();

    public static ApolloAppConfig getInstance() {
        return ourInstance;
    }

    private ApolloAppConfig() {

        Config springCloudAppNameConfig = ConfigService.getConfig("DPP.SPRINGCLOUD.NAMESPACE");
        dppCoreAppName = springCloudAppNameConfig.getProperty("appname.dpp-core", "dpp-core");
        dppEarlywarningAppName = springCloudAppNameConfig.getProperty("appname.dpp-data-earlywarning", "dpp-data-earlywarning");
        dppUserAppName = springCloudAppNameConfig.getProperty("appname.dpp-user", "dpp-user");
        dppSensorAppName = springCloudAppNameConfig.getProperty("appname.dpp-sensor", "dpp-sensor");
        dppAlarmControllerAppName = springCloudAppNameConfig.getProperty("appname.dpp-alarm-controller", "dpp-alarm-controller");
        dppSchedulerAppName = springCloudAppNameConfig.getProperty("appname.dpp-scheduler", "dpp-scheduler");
        dppCwsAppName = springCloudAppNameConfig.getProperty("appname.dpp-cws", "dpp-cws");
        ofmDaConfigAgentAppName = springCloudAppNameConfig.getProperty("appname.ofm-da-core-agent", "ofm-da-core-agent");
        ssoServerName = springCloudAppNameConfig.getProperty("appname.sso-server", "sso-server");

    }

    public String getDppUserAppName() {
        return dppUserAppName;
    }

    public void setDppUserAppName(String dppUserAppName) {
        this.dppUserAppName = dppUserAppName;
    }

    public String getDppSensorAppName() {
        return dppSensorAppName;
    }

    public void setDppSensorAppName(String dppSensorAppName) {
        this.dppSensorAppName = dppSensorAppName;
    }

    public String getDppAlarmControllerAppName() {
        return dppAlarmControllerAppName;
    }

    public void setDppAlarmControllerAppName(String dppAlarmControllerAppName) {
        this.dppAlarmControllerAppName = dppAlarmControllerAppName;
    }

    public String getDppSchedulerAppName() {
        return dppSchedulerAppName;
    }

    public void setDppSchedulerAppName(String dppSchedulerAppName) {
        this.dppSchedulerAppName = dppSchedulerAppName;
    }

    public String getDppCwsAppName() {
        return dppCwsAppName;
    }

    public void setDppCwsAppName(String dppCwsAppName) {
        this.dppCwsAppName = dppCwsAppName;
    }

    public String getDppCoreAppName() {
        return dppCoreAppName;
    }

    public void setDppCoreAppName(String dppCoreAppName) {
        this.dppCoreAppName = dppCoreAppName;
    }

    public String getDppEarlywarningAppName() {
        return dppEarlywarningAppName;
    }

    public void setDppEarlywarningAppName(String dppEarlywarningAppName) {
        this.dppEarlywarningAppName = dppEarlywarningAppName;
    }

    public String getOfmDaConfigAgentAppName() {
        return ofmDaConfigAgentAppName;
    }

    public void setOfmDaConfigAgentAppName(String ofmDaConfigAgentAppName) {
        this.ofmDaConfigAgentAppName = ofmDaConfigAgentAppName;
    }

    public String getSsoServerName() {
        return ssoServerName;
    }

    public void setSsoServerName(String ssoServerName) {
        this.ssoServerName = ssoServerName;
    }
}
