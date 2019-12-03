package com.siemens.dasheng.web.singleton.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuxin
 *         Created by xuxin on 2019/1/29.
 */
@Component
@ConfigurationProperties(prefix = "tag")
public class ConnectorTagMatch {

    private Double matchRate;

    public Double getMatchRate() {
        return matchRate;
    }

    public void setMatchRate(Double matchRate) {
        this.matchRate = matchRate;
    }

}
