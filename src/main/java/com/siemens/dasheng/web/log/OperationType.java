package com.siemens.dasheng.web.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.log.common.BaseLogObject;


/**
 * @author amon
 */
public class OperationType extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        String message = iLoggingEvent.getFormattedMessage();
        try {
            BaseLogObject baseLogObject = JSON.parseObject(message, BaseLogObject.class);
            if (null != baseLogObject && null != baseLogObject.getOperateType()) {
                return baseLogObject.getOperateType();
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }
}
