package com.siemens.dasheng.web.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;

/**
 * yaming.chen@siemens.com
 *
 * @author chenyaming
 * @date 2016/11/13
 */
@ConditionalOnWebApplication
@Configuration
public class WebSocketConfig {

    @Autowired
    private ServletContext servletContext;

    @Bean
    public ServerEndpointExporter serverEndpointExporter (){

        // 为了通过测试用例的特殊处理
        ServerContainer serverContainer =(ServerContainer) servletContext.getAttribute("javax.websocket.server.ServerContainer");
        if (null!=serverContainer){
            return new ServerEndpointExporter();
        }else{
            return null;
        }

    }

}
