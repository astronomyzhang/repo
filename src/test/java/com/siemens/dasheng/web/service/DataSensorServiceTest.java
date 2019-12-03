package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.request.DaConfigGroupRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ly
 * @date 2019/4/11
 */
public class DataSensorServiceTest  extends BaseTest {

    @Autowired
    private DataSensorService dataSensorService;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    /*@Test
    public void maidddn(){
        List<DaConfigSensor> sensorList = new ArrayList<>();
        for(int i=37100 ;i<39000 ;i++){
            DaConfigSensor sensor = new DaConfigSensor();
            sensor.setSiecode(i+"");
            sensor.setAppId(1314L);
            sensor.setConnectorId(5329L);
            sensor.setStatus(1L);
            sensor.setTag("WPU_TXLKE033.GenWatts");
            sensorList.add(sensor);
        }

        daConfigSensorMapper.insertList(sensorList);
    }*/

    @Test
    public void testGetSensorInfoBySieCode() {
        Map<String, Object> map = dataSensorService.getSensorInfoBySieCode("zlm_WPU_TXLKE013.RotorRPM");
        Assert.assertNotEquals(null, map);
        System.out.println(map);
    }

    @Test
    public void testGetSensorGroupList() {
        List<DaConfigGroupPlus> list = dataSensorService.getSensorGroupList(null);
        Assert.assertNotEquals(null, list);
    }

    @Test
    public void testGetSensorList() {
        List<String> list = dataSensorService.getSensorList(1L);
        Assert.assertNotEquals(null, list);
    }

    @Test
    public void testAddSensorGroup() {
        DaConfigGroupRequest daConfigGroupRequest = new DaConfigGroupRequest();
        daConfigGroupRequest.setGroupName("hello world!");
        daConfigGroupRequest.setGroupDescription("welcome to java world...");
        List<String> usedSensorList = new ArrayList<>(10);
        usedSensorList.add("siemens001");
        usedSensorList.add("siemens002");
        usedSensorList.add("siemens003");
        usedSensorList.add("siemens004");
        usedSensorList.add("siemens005");
        daConfigGroupRequest.setSensorList(usedSensorList);
        dataSensorService.addSensorGroup(daConfigGroupRequest);
    }

    @Test
    public void testRemoveSensorGroup() {
        dataSensorService.removeSensorGroup(1L);
    }

    @Test
    public void testModifySensorGroup() {
        DaConfigGroupRequest daConfigGroupRequest = new DaConfigGroupRequest();
        daConfigGroupRequest.setGroupId(1000L);
        daConfigGroupRequest.setGroupName("hello world!");
        daConfigGroupRequest.setGroupDescription("welcome to java world...");
        List<String> usedSensorList = new ArrayList<>(10);
        usedSensorList.add("siemens001");
        usedSensorList.add("siemens002");
        usedSensorList.add("siemens003");
        usedSensorList.add("siemens004");
        usedSensorList.add("siemens005");
        daConfigGroupRequest.setSensorList(usedSensorList);
        dataSensorService.addSensorGroup(daConfigGroupRequest);
    }

    @Test
    public void testViewSensorGroup() {
        Object obj = dataSensorService.viewSensorGroup(19L);
        Assert.assertNotEquals(null, obj);
    }


    @Test
    public void testSelectAvailableSensorListByAppID() {
        List<DaConfigSensorPlus> list = dataSensorService.selectAvailableSensorListByAppId(1730L);
        list.stream().map(DaConfigSensorPlus::getSiecode).forEach(System.out::println);
        Assert.assertNotEquals(null, list);
    }

}
