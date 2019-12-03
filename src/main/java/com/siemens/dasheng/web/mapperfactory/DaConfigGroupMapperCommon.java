package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigApplicationMapper;
import com.siemens.dasheng.web.mapper.DaConfigGroupMapper;
import com.siemens.dasheng.web.model.DaConfigGroup;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author allan
 * Created by ofm on 2019/4/8.
 */
@Component
public class DaConfigGroupMapperCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigGroupMapper daConfigGroupMapper;

    /**
     * 获取ID
     * @return
     * @param daConfigGroup
     */
    public Integer insertSelective(DaConfigGroup daConfigGroup) {

        int num =0;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            num = daConfigGroupMapper.insertSelectivePg(daConfigGroup);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            num = daConfigGroupMapper.insertSelectiveOrac(daConfigGroup);
        }
        return num;
    }

    public int insertSensorList(List<Map<String, Object>> list) {
        int num =0;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            num = daConfigGroupMapper.insertSensorListPg(list);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            num = daConfigGroupMapper.insertSensorListOrac(list);
        }
        return num;
    }
}
