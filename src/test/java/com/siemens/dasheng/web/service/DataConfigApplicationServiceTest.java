package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.enums.AppScopeEnum;
import com.siemens.dasheng.web.mapper.DaConfigAppExtensionMapper;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.mapperfactory.DaConfigSensorMapperCommon;
import com.siemens.dasheng.web.model.DaConfigAppExtension;
import com.siemens.dasheng.web.model.DaConfigConnectorSensorPlus;
import com.siemens.dasheng.web.model.DaConfigSensorPlus;
import com.siemens.dasheng.web.page.PageRespone;
import com.siemens.dasheng.web.request.QueryApplicationListRequest;
import com.siemens.dasheng.web.request.QuerySensorListRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;

/**
 * Created by xuxin on 2019-04-11 10:22
 */
public class DataConfigApplicationServiceTest extends BaseTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InjectMocks
    private DataApplicationService dataApplicationService;

    @Mock
    private DaConfigSensorMapper daConfigSensorMapper;

    @Mock
    private DaConfigAppExtensionMapper daConfigAppExtensionMapper;

    @Mock
    private DaConfigSensorMapperCommon daConfigSensorMapperCommon;

    @Before
    public void myBefore() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void querySensorList() {
        QuerySensorListRequest querySensorListRequest1 = new QuerySensorListRequest();
        querySensorListRequest1.setAppId(1L);
        querySensorListRequest1.setScope(AppScopeEnum.APP_PRIVATE.getType());
        List<Long> extendIds = new ArrayList<>();
        extendIds.add(2L);
        querySensorListRequest1.setExtendAppIds(extendIds);
        Mockito.when(daConfigSensorMapper.selectCountByCondition(1L,1L,AppScopeEnum.APP_PRIVATE.getType(),"",extendIds)).thenReturn(0);

        PageRespone<DaConfigConnectorSensorPlus> pageRespone = new PageRespone<DaConfigConnectorSensorPlus>();
        pageRespone = dataApplicationService.querySensorList(querySensorListRequest1);
        Assert.assertEquals(0L, Long.parseLong(pageRespone.getTotal().toString()));

        QuerySensorListRequest querySensorListRequest2 = new QuerySensorListRequest();
        querySensorListRequest2.setAppId(2L);
        querySensorListRequest2.setStatus(1L);
        querySensorListRequest2.setScope(AppScopeEnum.APP_PUBLIC.getType());
        List<Long> extendIds2 = new ArrayList<>();
        extendIds2.add(3L);
        querySensorListRequest2.setExtendAppIds(extendIds2);

        DaConfigAppExtension appExt = new DaConfigAppExtension();
        appExt.setAppId(querySensorListRequest2.getAppId());
        List<DaConfigAppExtension> appExtList = new ArrayList<>();
        DaConfigAppExtension cae = new DaConfigAppExtension();
        cae.setAppId(querySensorListRequest2.getAppId());
        cae.setExtensionAppId(4L);
        appExtList.add(cae);
        Mockito.when(daConfigAppExtensionMapper.select(appExt)).thenReturn(appExtList);

        List<Long> extendIds3 = new ArrayList<>();
        extendIds3.add(cae.getExtensionAppId());
        extendIds3.add(querySensorListRequest2.getAppId());
        Mockito.when(daConfigSensorMapper.selectCountByCondition(querySensorListRequest2.getAppId(),querySensorListRequest2.getStatus(),querySensorListRequest2.getScope(),querySensorListRequest2.getSearchContent(),extendIds3)).thenReturn(1);

        List<DaConfigSensorPlus> csplusList = new ArrayList<>();
        DaConfigSensorPlus csplus = new DaConfigSensorPlus();
        csplus.setAppId(2L);
        csplusList.add(csplus);
        Mockito.when(daConfigSensorMapperCommon.selectListByCondition(querySensorListRequest2.getAppId(),1L,AppScopeEnum.APP_PUBLIC.getType(),"",extendIds3,1,10)).thenReturn(csplusList);

        PageRespone<DaConfigConnectorSensorPlus> pageRespone2 = new PageRespone<DaConfigConnectorSensorPlus>();
        pageRespone2 = dataApplicationService.querySensorList(querySensorListRequest2);
        Assert.assertEquals(0L, Long.parseLong(pageRespone2.getTotal().toString()));


    }

    @Test
    public void queryApplicationList() {
        QueryApplicationListRequest request = new QueryApplicationListRequest();
        request.setApplicationId("1093");
        PageRespone response = dataApplicationService.queryApplicationList(request);
        Assert.assertNotNull(response.getData());
    }

}
