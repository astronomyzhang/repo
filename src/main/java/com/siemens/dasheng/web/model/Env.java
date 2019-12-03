package com.siemens.dasheng.web.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author xuxin
 * Env
 * created by xuxin on 6/9/2019
 */
@Component
@ConfigurationProperties(prefix="env")
public class Env {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
