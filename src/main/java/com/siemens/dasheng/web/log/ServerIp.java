package com.siemens.dasheng.web.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author amon
 */
public class ServerIp extends ClassicConverter {
    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
       String hostAddress = "";
       try {
           hostAddress = InetAddress.getLocalHost().getHostAddress();
       } catch (UnknownHostException e) {
           e.printStackTrace();
       }
       return hostAddress;
    }

}
