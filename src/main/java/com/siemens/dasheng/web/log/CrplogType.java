package com.siemens.dasheng.web.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author xuxin
 * dataProvider管理接口
 * created by xuxin on 15/11/2018
 */
public class CrplogType extends ClassicConverter {
    private final String ACTIVITY = "Activity";
    private final String GENERAL = "General";
    private final String SERCURITY = "Security";
    private final String OTHER = "Other";
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return GENERAL;
    }
}
