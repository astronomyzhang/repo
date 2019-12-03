package com.siemens.dasheng.web.event;

import com.siemens.dasheng.web.enums.AppScopeEnum;
import com.siemens.dasheng.web.mapper.DaConfigApplicationMapper;
import com.siemens.dasheng.web.model.DaConfigApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/2/27.
 */
@Component
public class DaAppVersionListener implements SmartApplicationListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == DaAppUpdateEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == String.class;
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        logger.info("DaAppVersionListener get new messge:" + event.getSource() + " time:" + event.getTimestamp());
        String note = (String) event.getSource();

        if (null != note) {

            Long appId = Long.valueOf(note);
            Long updateTime = System.currentTimeMillis();

            DaConfigApplication daConfigApplication = daConfigApplicationMapper.selectById(appId);
            if (null != daConfigApplication) {

                daConfigApplication.setSensormappingUpdateTime(updateTime);
                daConfigApplicationMapper.updateSensorMappingUpdateTimeByPrimaryKey(daConfigApplication);
                logger.info("Update daApp version success, appId:{},new version:{}", note, daConfigApplication.getSensormappingUpdateTime());


                if (daConfigApplication.getType().intValue() == AppScopeEnum.APP_PUBLIC.getType().intValue()) {
                    // update apps who extends this public app
                    int result = daConfigApplicationMapper.updatePrivateAppSensorMappingUpdateTimeByPublicAppId(appId, updateTime);
                    logger.info("Update daApp related app versions success, publicAppId:{},new version:{},updateSize:{}", note, daConfigApplication.getSensormappingUpdateTime(), result);
                }

            }

        }

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
