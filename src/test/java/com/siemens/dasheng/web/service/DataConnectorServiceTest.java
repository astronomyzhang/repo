package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSONObject;
import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.client.LicenseClientSingleton;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.DaConfigConnectorSensorPlus;
import com.siemens.dasheng.web.model.dto.DaMonitorConnector;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import org.jarbframework.utils.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.DATA;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;

/**
 * @author xuxin@siemens.com
 * Created by xuxin on 2018/11/20.
 */
public class DataConnectorServiceTest extends BaseTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Mock
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void myBefore() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void saveConnector() {

        DaConfigConnectorRequest successConnectorReq = new DaConfigConnectorRequest();
        successConnectorReq.setName("success");

        DaConfigConnectorRequest failConnectorReq = new DaConfigConnectorRequest();
        failConnectorReq.setName("fail");

        DaConfigConnectorRequest exceptionConnectorReq = new DaConfigConnectorRequest();
        exceptionConnectorReq.setName("exception");

        DaConfigConnector successConnector = new DaConfigConnector();
        DaConfigConnector failConnector = new DaConfigConnector();
        DaConfigConnector exceptionConnector = new DaConfigConnector();
        BeanUtils.copyProperties(successConnectorReq, successConnector);
        BeanUtils.copyProperties(failConnectorReq, failConnector);
        BeanUtils.copyProperties(exceptionConnectorReq, exceptionConnector);


        Mockito.when(daConfigConnectorMapper.insert(Matchers.eq(successConnector))).thenReturn(1);
        Mockito.when(daConfigConnectorMapper.insert(Matchers.eq(failConnector))).thenReturn(0);
        Mockito.doThrow(new RuntimeException()).when(this.daConfigConnectorMapper).insert(Matchers.eq(exceptionConnector));


        ModelMap successResult = dataConnectorService.saveConnector(successConnectorReq);
        Assert.assertEquals(true, (Boolean) ((HashMap<String, Object>) successResult.get(DATA)).get("status"));

        ModelMap failedResult = dataConnectorService.saveConnector(failConnectorReq);
        Assert.assertEquals(false, (Boolean) ((HashMap<String, Object>) failedResult.get(DATA)).get("status"));

        ModelMap exceptionResult = dataConnectorService.saveConnector(exceptionConnectorReq);
        Assert.assertEquals(false, (Boolean) ((HashMap<String, Object>) exceptionResult.get(DATA)).get("status"));

    }

    @Test
    public void testConnector() throws Exception {
        DaConfigConnectorRequest successConnectorReq = new DaConfigConnectorRequest();
        successConnectorReq.setSqldas("10.192.30.135");
        successConnectorReq.setServerHost("localhost");
        successConnectorReq.setUserName("siemens-pdt");
        successConnectorReq.setPassword("12345678");
        DaConfigConnectorRequest failConnectorReq = new DaConfigConnectorRequest();
        failConnectorReq.setSqldas("10.192.30.135");
        failConnectorReq.setServerHost("localhost");
        failConnectorReq.setUserName("siemens-pdt");
        failConnectorReq.setPassword("1");

        DaConfigConnectorRequest failConnectorReq1 = new DaConfigConnectorRequest();
        failConnectorReq1.setSqldas("10.192.30.135");

        ModelMap failedResult1 = dataConnectorService.testConnector(failConnectorReq1);
        Assert.assertEquals(false, (Boolean) ((HashMap<String, Object>) failedResult1.get(DATA)).get("status"));

        DaConfigConnectorRequest failConnectorReq2 = new DaConfigConnectorRequest();
        failConnectorReq2.setSqldas("10.192.30.1352");
        ModelMap failedResult2 = dataConnectorService.testConnector(failConnectorReq2);
        Assert.assertEquals(false, (Boolean) ((HashMap<String, Object>) failedResult2.get(DATA)).get("status"));

        DaConfigConnectorRequest failConnectorReq3 = new DaConfigConnectorRequest();
        failConnectorReq3.setSqldas("10.192.30.135");
        ModelMap failedResult3 = dataConnectorService.testConnector(failConnectorReq3);
        Assert.assertEquals(false, (Boolean) ((HashMap<String, Object>) failedResult3.get(DATA)).get("status"));


        ModelMap successResult = dataConnectorService.testConnector(successConnectorReq);
        Assert.assertEquals(true, (Boolean) ((HashMap<String, Object>) successResult.get(DATA)).get("status"));

        ModelMap failedResult = dataConnectorService.testConnector(failConnectorReq);
        Assert.assertEquals(false, (Boolean) ((HashMap<String, Object>) failedResult.get(DATA)).get("status"));

    }


    @Test
    public void getDaMonitorConnectorListFromDaRouting() {

        String appId = "OFMAPP00010001";
        List<DaMonitorConnector> daMonitorConnectorList = dataConnectorService.getDaMonitorConnectorListFromDaRouting(appId);
        Assert.assertNotNull(daMonitorConnectorList);

    }

    @Test
    public void getDaMonitorConnectorListFromDaAf() {

        List<DaMonitorConnector> daMonitorConnectorList = dataConnectorService.getDaMonitorConnectorListFromDaAf();
        Assert.assertNotNull(daMonitorConnectorList);

    }

    @Test
    public void testVerifyLogic() throws Exception {
        ModelMap modelMap = new ModelMap();
        //
        DaConfigConnector connector = new DaConfigConnector();
        Map<String, String> resultData = new HashMap<>(1);
        DaConfigConnectorRequest daConfigConnectorRequest = new DaConfigConnectorRequest();
        List<DaConfigConnectorSensorPlus> connectorSensorList = new ArrayList<>(1);

        //Mockito.when(daConfigConnectorMapper.selectConnectorById(null)).thenReturn(connector);
        Mockito.when(daConfigConnectorSensorMapper.selectListByConnectorIdAndSiecode(null, null)).thenReturn(connectorSensorList);
        //Mockito.when(connectorService.selectTagListExact(null, null)).thenReturn(resultData);

        DaConfigConnectorSensorPlus tag = new DaConfigConnectorSensorPlus();
        connectorSensorList.add(tag);
        dataConnectorService.checkTagExistByConnectorId(5604L,"aaa",true, 1000L, modelMap);
        Assert.assertEquals(true, (Boolean) modelMap.get(IS_SUCCESS));

        dataConnectorService.checkTagExistByConnectorId(5604L,null,false, 1000L, modelMap);
        Assert.assertEquals(false, (Boolean) modelMap.get(IS_SUCCESS));
    }

    @Test
    public void testLicense() throws Exception {
        LicenseClientSingleton instance = LicenseClientSingleton.getInstance();
        JSONObject jsonObject = instance.licenseDetails();
        JSONObject platformObject = jsonObject.getJSONObject("Platform");

        int sensorScale = 0;
        if (platformObject != null) {
            String sensorScaleStr = (String) platformObject.get("sensorScale");
            if (StringUtils.isNotBlank(sensorScaleStr)) {
                sensorScale = Integer.parseInt(sensorScaleStr);
                System.out.println(sensorScale);
            }
        }


    }

}