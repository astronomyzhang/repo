package com.siemens.dasheng.web.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/5/28.
 */
@Component
@ConfigurationProperties(prefix = "dpp")
public class DppModeConf {

    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
