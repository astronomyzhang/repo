package com.siemens.dasheng.web.log;

import ch.qos.logback.classic.PatternLayout;

/**
 * @author amon
 */
public class MyPatternLayout extends PatternLayout {

    static {
        defaultConverterMap.put("serverIp", ServerIp.class.getName());
        defaultConverterMap.put("serverName", ServerName.class.getName());
        defaultConverterMap.put("crplogtype", CrplogType.class.getName());
        defaultConverterMap.put("operationType", OperationType.class.getName());
        defaultConverterMap.put("operationContiant", CrplogType.class.getName());
    }

}
