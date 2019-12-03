package com.siemens.dasheng.web.singleton.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yuanxin.zhang@siemens.com
 *         Created by zhangyuanxin on 2018/1/29.
 */
@Component
@ConfigurationProperties(prefix = "database")
public class DataBaseConf {

    private String type;

    public String getType() {
        //this.type = "postgresql";
        return type;
    }

    public void setType(String type) {
        //this.type = "oracle";
        this.type = type;
    }
}
