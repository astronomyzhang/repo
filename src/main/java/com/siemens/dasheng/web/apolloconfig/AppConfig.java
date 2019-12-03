package com.siemens.dasheng.web.apolloconfig;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.siemens.dasheng.web.singleton.conf.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yaming.chen@siemens.com
 *         Created by chenyaming on 2018/1/16.
 */
@Configuration
@EnableApolloConfig
public class AppConfig {

    @Bean
    public DataBaseConf dataBaseConf() {
        return new DataBaseConf();
    }

}
