package com.siemens.dasheng.web.event;

import com.siemens.dasheng.web.mapper.DaConfigProviderMapper;
import com.siemens.dasheng.web.model.DaConfigProvider;
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
public class DaProviderVersionListener implements SmartApplicationListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == DaProviderUpdateEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return sourceType == String.class;
    }


    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        logger.info("ProviderVersionListener get new messge:" + event.getSource() + " time:" + event.getTimestamp());
        String note = (String) event.getSource();

        if (null != note) {

            DaConfigProvider daCoreProviderDatabase = daConfigProviderMapper.selectByPrimaryKey(Long.valueOf(note));
            if (null != daCoreProviderDatabase) {
                daCoreProviderDatabase.setSensormappingUpdateTime(System.currentTimeMillis());
                daConfigProviderMapper.updateByPrimaryKeySelective(daCoreProviderDatabase);
                logger.info("Update provider version success, provierId:{},new version:{}", note, daCoreProviderDatabase.getSensormappingUpdateTime());
            }

        }

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
