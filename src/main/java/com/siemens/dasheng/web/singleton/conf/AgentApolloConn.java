package com.siemens.dasheng.web.singleton.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuxin
 *         Created by xuxin on 2019/8/29.
 */
@Component
@ConfigurationProperties(prefix = "agentcon")
public class AgentApolloConn {

    private String agentip;

    private String agentport;

    public String getAgentip() {
        return agentip;
    }

    public void setAgentip(String agentip) {
        this.agentip = agentip;
    }

    public String getAgentport() {
        return agentport;
    }

    public void setAgentport(String agentport) {
        this.agentport = agentport;
    }


}
