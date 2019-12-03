package com.siemens.dasheng.web.conf;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zheng.wenjing@siemens.com
 * on 1/4/2018
 * @author Z003W5DZ
 */

@Configuration
public class MonitoringConfig {

    @Bean
    ServletRegistrationBean servletRegistrationBean() {

        return new ServletRegistrationBean(new MetricsServlet(), "/metrics");

    }
}
