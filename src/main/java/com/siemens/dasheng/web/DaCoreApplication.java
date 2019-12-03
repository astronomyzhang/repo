package com.siemens.dasheng.web;

import com.siemens.dasheng.web.listener.ApplicationStartup;
import com.siemens.ofmcommon.log.RestTraceInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.Collections;

/**
 * yaming.chen@siemens.com
 *
 * @author chenyaming
 * @date 2016/10/17
 */
@Controller
@EnableWebMvc
@EnableWebSocket
@SpringBootApplication
@EnableJms
@EnableAsync
@RestController
@EnableEurekaClient
@EnableDiscoveryClient
@ServletComponentScan
@EnableScheduling
@ComponentScan(basePackages = {"com.siemens.dasheng", "com.siemens.ofm","com.siemens.ofmcommon.log"})
public class DaCoreApplication extends WebMvcConfigurerAdapter {

    private final static int REQUEST_CONNECT_TIMEOUT = 10000;
    private final static int REQUEST_READ_TIMEOUT = 300000;

    public static void main(String[] args) {

        System.setProperty("jasypt.encryptor.password", "dpp123456!@#");
        System.setProperty("jasypt.encryptor.algorithm", "PBEWithMD5AndDES");
        System.setProperty("service-name","ofm-da-core");

        SpringApplication springApplication = new SpringApplication(DaCoreApplication.class);
        springApplication.addListeners(new ApplicationStartup());
        springApplication.run(args);
    }


    @Bean(name = "ribbonRestTemplate")
    @LoadBalanced
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getSimpleClientHttpRequestFactory());
        restTemplate.setInterceptors(Collections.singletonList(new RestTraceInterceptor()));
        return restTemplate;
    }

    @Bean(name = "commonRestTemplate")
    RestTemplate commonRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(getSimpleClientHttpRequestFactory());
        restTemplate.setInterceptors(Collections.singletonList(new RestTraceInterceptor()));
        return restTemplate;
    }

    private SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(REQUEST_CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(REQUEST_READ_TIMEOUT);
        return requestFactory;
    }


}
