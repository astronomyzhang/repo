package com.siemens.dasheng.web.listener;

import com.siemens.dasheng.web.client.LicenseClientSingleton;
import com.siemens.dasheng.web.conf.CustomSpringConfigurator;
import com.siemens.dasheng.web.conf.DppModeConf;
import com.siemens.dasheng.web.enums.DppModeType;
import com.siemens.dasheng.web.enums.ModuleName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;

/**
 * 应用启动成功后执行
 *
 * @Author: fan.bian.ext@siemens.com
 * @Date: Created in 2018/7/18.
 */
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private DppModeConf dppModeConf;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        CustomSpringConfigurator configurator = new CustomSpringConfigurator();
        try {
            dppModeConf = configurator.getEndpointInstance(DppModeConf.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
            return;
        }

        // do license register
        LicenseClientSingleton instance = LicenseClientSingleton.getInstance();

        HashMap<String, String> moduleNameMap = new HashMap<>(3);
        moduleNameMap.put("anomaly", ModuleName.ANOMALY.getName());
        if (DppModeType.ICM.getType().equals(dppModeConf.getMode())) {
            moduleNameMap.put("cws", ModuleName.CWS.getName());
        }
        moduleNameMap.put("platform", ModuleName.Platform.getName());

        instance.setModuleName(moduleNameMap);

    }
}