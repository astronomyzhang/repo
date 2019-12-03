package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigApplicationMapper;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author allan
 * Created by ofm on 2019/4/8.
 */
@Component
public class DaConfigApplicationMapperCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    /**
     * 获取ID
     * @return
     */
    public Long selectId() {

        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigApplicationMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigApplicationMapper.selectIdOR();
        }
        return id;
    }
}
