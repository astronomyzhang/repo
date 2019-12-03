package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xuxin
 * @Date: 11/23/2018 6:29 PM
 */
@Component
public class DaConfigConnectorMapperCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;


    public Long selectId() {

        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigConnectorMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigConnectorMapper.selectIdOR();
        }
        return id;
    }
}
