package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigProviderMapper;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xuxin
 * @Date: 11/28/2018 6:29 PM
 */
@Component
public class DaConfigProviderMapperCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;


    public Long selectId() {

        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigProviderMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigProviderMapper.selectIdOR();
        }
        return id;
    }
}
