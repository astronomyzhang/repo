package com.siemens.dasheng.web.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author amon
 */
public class OperationContiant extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        return iLoggingEvent.getFormattedMessage();
    }
}
