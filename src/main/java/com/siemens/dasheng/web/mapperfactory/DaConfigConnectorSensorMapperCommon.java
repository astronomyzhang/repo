package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.model.DaConfigConnectorSensorPlus;
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
public class DaConfigConnectorSensorMapperCommon {
    @Autowired
    private DataBaseConf dataBaseConf;
    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    public Long selectId() {
        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigConnectorSensorMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigConnectorSensorMapper.selectIdOR();
        }
        return id;
    }

    public List<DaConfigConnectorSensorPlus> selectListByCondition(Long applicationId, List<Long> connectorIds, Long connectorStatus, Long status, String searchContent, int i, int pageSize) {
        List<DaConfigConnectorSensorPlus> csPlusList = new ArrayList<>();
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            csPlusList = daConfigConnectorSensorMapper.selectListByConditionPG(applicationId, connectorIds, connectorStatus, status, searchContent, i, pageSize);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            csPlusList = daConfigConnectorSensorMapper.selectListByConditionORAC(applicationId, connectorIds, connectorStatus, status, searchContent, i, pageSize);
        }
        return csPlusList;
    }

    public int insertList(List<DaConfigConnectorSensor> retList) {
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            return daConfigConnectorSensorMapper.insertList(retList);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            return daConfigConnectorSensorMapper.saveListOracle(retList);
        } else {
            return 0;
        }
    }

}
