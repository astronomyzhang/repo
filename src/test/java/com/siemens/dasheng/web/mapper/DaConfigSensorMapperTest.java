package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.mapperfactory.DaConfigSensorMapperCommon;
import com.siemens.dasheng.web.model.DaConfigSensor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/5/31.
 */
public class DaConfigSensorMapperTest extends BaseTest {

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DaConfigSensorMapperCommon daConfigSensorMapperCommon;

    @Autowired
    private  DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Test
    public void updateValidRuleBySieCode() {

        String oldSiecode = "AamontestWPU_TXLKE020.WindDir";
        String newSiecode = "AamontestWPU_TXLKE020.WindDirNew";

        int updateNum = daConfigSensorMapper.updateValidRuleBySieCode(oldSiecode, newSiecode);
        Assert.assertNotEquals(0, updateNum);

    }

    @Test
    public void testBatchInsert() {
        List<DaConfigSensor> sensors = new ArrayList<>();
        for (int i = 0; i <= 39999; i++) {
            DaConfigSensor sensor = new DaConfigSensor();
            sensor.setSiecode("dolceSiecode"+i);
            sensor.setTag("dolcetag");
            sensor.setConnectorId(1000L);
            sensor.setAppId(1000L);
            sensor.setStatus(1L);
            sensor.setFromRegist(1);
            sensors.add(sensor);
        }
        List<DaConfigSensor> retList = new ArrayList<>();
        // set batch insert size 4000 (8000*4<32767)
        int batchSize = 3000;
        int insertNum = 0;
        int currentIndex = 0;
        for (DaConfigSensor sensor : sensors) {
            if (currentIndex == batchSize) {
                insertNum += daConfigSensorMapperCommon.insertList(retList);
                currentIndex = 0;
                retList = new ArrayList<>();
            }
            retList.add(sensor);
            currentIndex++;
        }
        // insert last records
        if (retList.size() > 0) {
            insertNum += daConfigSensorMapperCommon.insertList(retList);
        }
        System.out.println(insertNum);
    }

    @Test
    public void testBatchInsertAndDelete() {
        List<DaConfigSensor> sensors = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            DaConfigSensor sensor = new DaConfigSensor();
            sensor.setSiecode("dolceSiecode"+i);
            sensor.setTag("dolcetag" + i);
            sensor.setConnectorId(1000L);
            sensor.setAppId(1000L);
            sensor.setStatus(1L);
            sensor.setFromRegist(1);
            sensors.add(sensor);
        }
        daConfigSensorMapperCommon.insertList(sensors);

        Set<String> wronglist = new HashSet<>();
        for (int i = 0; i <= 4; i++) {
            wronglist.add("dolcetag" + i);
        }
        daConfigSensorMapper.deleteBatchByConnectorId(1000L, wronglist);

    }

}