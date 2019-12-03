package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.model.dto.DaConnectorStatus;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/4/25.
 */
public class DaConfigApplicationMapperTest extends BaseTest {

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Test
    public void updatePrivateAppSensorMappingUpdateTimeByPublicAppId() {

        Long publicAppId = 1137L;
        Long updateTime = 20000L;

        int result = daConfigApplicationMapper.updatePrivateAppSensorMappingUpdateTimeByPublicAppId(publicAppId, updateTime);
        System.out.println(result);

    }

    @Test
    public void selectDaConnectorStatusListByFleetFrameAppIdList() {
        String appId = "OFMAPP00010002";
        List<String> appIdList = daConfigApplicationMapper.getPublicFleetFrameAppIdListByPrivateAppId(appId);
        Assert.assertNotNull(appIdList);
    }

    @Test
    public void getPublicFleetFrameAppIdListByPrivateAppId() {

        List<String> appList = new ArrayList<>();
        //appList.add("OFMAPP00010001");
        appList.add("OFMAPP00010002");

        List<DaConnectorStatus> daConnectorStatusList = daConfigApplicationMapper.selectDaConnectorStatusListByFleetFrameAppIdList(appList);
        Assert.assertNotNull(daConnectorStatusList);

    }
}