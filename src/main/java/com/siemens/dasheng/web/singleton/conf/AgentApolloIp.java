package com.siemens.dasheng.web.singleton.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuxin
 *         Created by xuxin on 2019/8/29.
 */
@Component
@ConfigurationProperties(prefix = "agent")
public class AgentApolloIp {

    private String ip;

    private String port;



    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
