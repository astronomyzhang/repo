package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.model.DaConfigSensor;
import com.siemens.dasheng.web.model.DaConfigSensorPlus;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public class DaConfigSensorMapperCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    public Long selectId() {
        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigSensorMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigSensorMapper.selectIdOR();
        }
        return id;
    }


    public List<DaConfigSensorPlus> selectListByCondition(Long appId, Long status, Integer scope, String searchContent, List<Long> extendAppIds, int i, Integer pageSize) {
        List<DaConfigSensorPlus> csPlusList = new ArrayList<>();
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            csPlusList = daConfigSensorMapper.selectListByConditionPG(appId, status, scope, searchContent, extendAppIds, i, pageSize);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            csPlusList = daConfigSensorMapper.selectListByConditionORAC(appId, status, scope, searchContent, extendAppIds, i, pageSize);
        }
        return csPlusList;
    }

    public int insertList(List<DaConfigSensor> daConfigSensorList) {
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            return daConfigSensorMapper.insertList(daConfigSensorList);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            return daConfigSensorMapper.saveListOracle(daConfigSensorList);
        } else {
            return 0;
        }
    }

}
