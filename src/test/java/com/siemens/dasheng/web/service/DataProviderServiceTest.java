package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.mapperfactory.DaConfigProviderMapperCommon;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.request.DaConfigProviderRequest;
import com.siemens.dasheng.web.request.ModifyProviderRequest;
import com.siemens.dasheng.web.request.QueryProviderRequest;
import com.siemens.dasheng.web.request.ValidateConnectorsStatusRequest;
import com.siemens.dasheng.web.response.ModifyProviderResponse;
import com.siemens.dasheng.web.response.QueryProviderResponse;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
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
import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;

/**
 * @author xuxin@siemens.com
 * Created by xuxin on 2018/12/7.
 */
public class DataProviderServiceTest extends BaseTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void myBefore() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    private DataProviderService dataProviderService;

    @Mock
    private DaConfigProviderMapper daConfigProviderMapper;

    @Mock
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Mock
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Mock
    private DaConfigDatabaseMapper daConfigDatabaseMapper;

    @Mock
    private DaConfigCategoryMapper daConfigCategoryMapper;

    @Mock
    private DaConfigConnectorTypeMapper daConfigConnectorTypeMapper;

    @Mock
    private DaConfigConnectorClassMapper daConfigConnectorClassMapper;

    @Mock
    private DaConfigProviderMapperCommon daConfigProviderMapperCommon;

    @Autowired
    @Spy
    private DataBaseConf dataBaseConf;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveProvider() {

        //connectorIds已删除
        List<DaConfigConnector> connectors = new ArrayList<>();
        DaConfigConnector daConfigConnector = new DaConfigConnector();
        daConfigConnector.setId(1001L);
        daConfigConnector.setName("delete");
        connectors.add(daConfigConnector);
        DaConfigProviderRequest providerReq = new DaConfigProviderRequest();
        providerReq.setConnectorList(connectors);
        providerReq.setName("save");
        List<Long> connectIds = new ArrayList<>();
        connectIds.add(1001L);
        List<DaConfigConnector> connectors1 = new ArrayList<>();
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds)).thenReturn(connectors1);
        ModelMap modelMap1 = dataProviderService.saveDataProvider(providerReq);
        Assert.assertEquals(false, modelMap1.get(IS_SUCCESS));

        //connectorClass或type被修改
        List<Long> connectIds1 = new ArrayList<>();
        connectIds1.add(1001L);
        List<DaConfigConnector> connectors2 = new ArrayList<>();
        DaConfigConnector con2 = new DaConfigConnector();
        con2.setId(1001L);
        con2.setName("delete");
        con2.setConnectorType("timeser");
        connectors2.add(con2);
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors2);
        ModelMap modelMap2 = dataProviderService.saveDataProvider(providerReq);
        Assert.assertEquals(false, modelMap2.get(IS_SUCCESS));

        //保存dataProvider失败
        List<DaConfigConnector> connectors3 = new ArrayList<>();
        DaConfigConnector con3 = new DaConfigConnector();
        con3.setId(1001L);
        con3.setName("delete");
        con3.setConnectorType("TIME_SERIES_DATA");
        con3.setConnectorClass("ARCHIVED");
        connectors3.add(con3);
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors3);
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        daConfigProvider.setName("save");
        Mockito.when(daConfigProviderMapper.insert(daConfigProvider)).thenReturn(0);
        ModelMap modelMap3 = dataProviderService.saveDataProvider(providerReq);
        Assert.assertEquals(false, modelMap3.get(IS_SUCCESS));

        //保存关联失败
        DaConfigProvider daConfigProvider2 = new DaConfigProvider();
        daConfigProvider2.setName("save");
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors3);
        Mockito.when(daConfigProviderMapper.insert(daConfigProvider2)).thenReturn(1);

        List<DaConfigProviderConnector> pcList = new ArrayList<>();
        DaConfigProviderConnector daConfigProviderConnector1 = new DaConfigProviderConnector();
        daConfigProviderConnector1.setConnectorId(1001L);
        daConfigProviderConnector1.setProviderId(12L);
        pcList.add(daConfigProviderConnector1);
        dataBaseConf.setType(DataBaseType.POSTGRESQL.getType());
        Mockito.when(daConfigProviderMapperCommon.selectId()).thenReturn(1L);
        Mockito.when(daConfigProviderConnectorMapper.batchInsertPG(pcList)).thenReturn(0);
        ModelMap modelMap4 = dataProviderService.saveDataProvider(providerReq);
        Assert.assertEquals(false, modelMap4.get(IS_SUCCESS));

        //保存成功

        List<DaConfigProviderConnector> pcList1 = new ArrayList<>();
        DaConfigProviderConnector daConfigProviderConnector2 = new DaConfigProviderConnector();
        daConfigProviderConnector2.setConnectorId(1001L);
        daConfigProviderConnector2.setProviderId(1L);
        pcList1.add(daConfigProviderConnector2);
        Mockito.when(daConfigProviderConnectorMapper.batchInsertPG(pcList1)).thenReturn(1);
        ModelMap modelMap5 = dataProviderService.saveDataProvider(providerReq);
        Assert.assertEquals(true, modelMap5.get(IS_SUCCESS));


    }


    @Test
    public void queryConnectorByClass() throws Exception {
        List<DaConfigConnector> connectorList = new ArrayList<>();
        DaConfigConnector con = new DaConfigConnector();
        con.setId(1001L);
        con.setConnectorClass("ARCHIVED");
        con.setArchivedDatabase("OSIPI");
        con.setConnectApproach("SQLDASV2016");
        connectorList.add(con);
        String classId = "ARCHIVED";
        Mockito.when(daConfigConnectorMapper.selectConnectorByClass(classId)).thenReturn(connectorList);
        List<DaConfigDatabase> daConfigDatabaseList = new ArrayList<>();
        DaConfigDatabase database = new DaConfigDatabase();
        database.setId("OSIPI");
        database.setName("OSIPI");
        daConfigDatabaseList.add(database);
        Mockito.when(daConfigDatabaseMapper.selectAllList()).thenReturn(daConfigDatabaseList);
        List<DaConfigCategory> daConfigCategoryList = new ArrayList<>();
        DaConfigCategory category = new DaConfigCategory();
        category.setId("SQLDASV2016");
        category.setName("SQLDASV2016");
        daConfigCategoryList.add(category);
        Mockito.when(daConfigCategoryMapper.selectAllList()).thenReturn(daConfigCategoryList);
        ModelMap modelMap1 = dataProviderService.queryConnectorByClass(classId);
        Assert.assertEquals(true, modelMap1.get(IS_SUCCESS));

    }

    @Test
    public void queryConnectorFilter() throws Exception {
        List<DaConfigConnectorType> daConfigConnectorTypeList = new ArrayList<>();
        DaConfigConnectorType conType = new DaConfigConnectorType();
        conType.setId("TIME_SERIES_DATA");
        conType.setName("Time-series");
        conType.setDataType("DATA_PROVIDER");
        daConfigConnectorTypeList.add(conType);
        Mockito.when(daConfigConnectorTypeMapper.selectByDataType()).thenReturn(daConfigConnectorTypeList);
        List<DaConfigConnectorClass> daConfigConnectorClassList = new ArrayList<>();
        DaConfigConnectorClass conClass = new DaConfigConnectorClass();
        conClass.setId("ARCHIVED");
        conClass.setName("Archived");
        conClass.setConnectorTypeId("TIME_SERIES_DATA");
        daConfigConnectorClassList.add(conClass);
        Mockito.when(daConfigConnectorClassMapper.selectAllList()).thenReturn(daConfigConnectorClassList);
        List<DaConfigDatabase> daConfigDatabaseList = new ArrayList<>();
        DaConfigDatabase database = new DaConfigDatabase();
        database.setId("OSIPI");
        database.setName("OSI-PI Archive");
        database.setClassId("ARCHIVED");
        daConfigDatabaseList.add(database);
        Mockito.when(daConfigDatabaseMapper.selectAllList()).thenReturn(daConfigDatabaseList);
        List<DaConfigCategory> daConfigCategoryList = new ArrayList<>();
        DaConfigCategory category = new DaConfigCategory();
        category.setId("SQLDASV2016");
        category.setName("OSI-PI SQLDAS (Version 2016)");
        category.setDatabaseId("OSIPI");
        daConfigCategoryList.add(category);
        Mockito.when(daConfigCategoryMapper.selectAllList()).thenReturn(daConfigCategoryList);
        ModelMap modelMap = dataProviderService.queryConnectorFilter();
        Assert.assertEquals(true, modelMap.get(IS_SUCCESS));

    }

    @Test
    public void queryProviderList() throws Exception {
        QueryProviderRequest request0 = new QueryProviderRequest();
        request0.setStatus(0L);
        List<DaConfigProvider> providerList0 = new ArrayList<>();
        Mockito.when(this.daConfigProviderMapper.queryListByCondition(request0)).thenReturn(providerList0);
        QueryProviderResponse res0 = this.dataProviderService.queryProviderList(request0);
        Assert.assertEquals(0L, (Object) res0.getStatus());


        QueryProviderRequest request1 = new QueryProviderRequest();
        request1.setStatus(1L);
        List<DaConfigProvider> providerList1 = new ArrayList<>();
        DaConfigProvider pro1 = new DaConfigProvider();
        pro1.setConnectorType("TIME_SERIES_DATA");
        pro1.setConnectorClass("ARCHIVED");
        providerList1.add(pro1);
        Mockito.when(daConfigProviderMapper.queryListByCondition(request1)).thenReturn(providerList1);
        Mockito.when(daConfigConnectorClassMapper.selectAllList()).thenReturn(null);
        QueryProviderResponse res1 = dataProviderService.queryProviderList(request1);
        Assert.assertEquals(0L, (Object) res1.getStatus());

        List<DaConfigConnectorClass> classList = new ArrayList<>();
        DaConfigConnectorClass clas = new DaConfigConnectorClass();
        clas.setId("ARCHIVED");
        clas.setName("ARCHIVED");
        classList.add(clas);
        Mockito.when(daConfigConnectorClassMapper.selectAllList()).thenReturn(classList);
        QueryProviderResponse res2 = dataProviderService.queryProviderList(request1);
        Assert.assertEquals(0L, (Object) res2.getStatus());

    }

    @Test
    public void queryConnectorListByProviderId() throws Exception {
        Long providerId = 1347L;
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        daConfigProvider.setId(providerId);

        List<DaConfigConnector> connectorList = new ArrayList<>();
        DaConfigConnector con = new DaConfigConnector();
        con.setId(1001L);
        connectorList.add(con);
        Mockito.when(daConfigProviderMapper.queryById(providerId)).thenReturn(daConfigProvider);
        Mockito.when(daConfigConnectorMapper.queryConnectorListByProviderId(providerId)).thenReturn(connectorList);
        QueryProviderResponse res = dataProviderService.queryConnectorListByProviderId(providerId);
        Assert.assertEquals(0L, (Object) res.getStatus());
    }

    @Test
    public void validateConnectorsStatus() throws Exception {
        ValidateConnectorsStatusRequest v1 = new ValidateConnectorsStatusRequest();
        v1.setStatus(2L);
        Long res1 = dataProviderService.validateConnectorsStatus(v1);
        Assert.assertEquals(1L, (Object) res1);

        ValidateConnectorsStatusRequest v2 = new ValidateConnectorsStatusRequest();
        v2.setStatus(1L);
        Long res2 = dataProviderService.validateConnectorsStatus(v2);
        Assert.assertEquals(2L, (Object) res2);

        ValidateConnectorsStatusRequest v3 = new ValidateConnectorsStatusRequest();
        v3.setStatus(1L);
        List<DaConfigConnector> conList = new ArrayList<>();
        DaConfigConnector con = new DaConfigConnector();
        con.setId(1001L);
        conList.add(con);
        v3.setDaConfigConnectorList(conList);
        Long res3 = dataProviderService.validateConnectorsStatus(v3);
        Assert.assertEquals(3L, (Object) res3);
    }

    @Test
    public void modifyProvider() throws Exception {


        //connectorIds已删除 和 connectorClass或type被修改
        List<DaConfigConnector> connectors = new ArrayList<>();
        DaConfigConnector daConfigConnector = new DaConfigConnector();
        daConfigConnector.setId(1001L);
        daConfigConnector.setName("delete");
        DaConfigConnector daConfigConnector1 = new DaConfigConnector();
        daConfigConnector1.setId(1002L);
        daConfigConnector1.setName("Update");
        connectors.add(daConfigConnector);
        connectors.add(daConfigConnector1);
        ModifyProviderRequest providerReq = new ModifyProviderRequest();
        providerReq.setId(1L);
        providerReq.setConnectorList(connectors);
        providerReq.setName("save");
        List<Long> connectIds1 = new ArrayList<>();
        connectIds1.add(1001L);
        connectIds1.add(1002L);
        List<DaConfigConnector> connectors2 = new ArrayList<>();
        DaConfigConnector con2 = new DaConfigConnector();
        con2.setId(1001L);
        con2.setName("delete");
        con2.setConnectorType("timeser");
        connectors2.add(con2);
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors2);
        ModifyProviderResponse modelMap2 = dataProviderService.modifyProvider(providerReq);
        Assert.assertEquals(1L, (Object) modelMap2.getStatus());


        //connectorIds已删除

        List<DaConfigConnector> connectors1 = new ArrayList<>();
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors1);
        ModifyProviderResponse modelMap1 = dataProviderService.modifyProvider(providerReq);
        Assert.assertEquals(2L, (Object) modelMap1.getStatus());

        //connectorClass或type被修改

        List<DaConfigConnector> connectors3 = new ArrayList<>();
        DaConfigConnector con3 = new DaConfigConnector();
        con3.setId(1001L);
        con3.setName("delete");
        con3.setConnectorType("timeser");
        connectors3.add(con3);
        DaConfigConnector con4 = new DaConfigConnector();
        con4.setId(1002L);
        con4.setName("delete");
        con4.setConnectorType("timeser");
        connectors3.add(con3);
        connectors3.add(con4);
        providerReq.setStatus(1L);
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors3);
        ModifyProviderResponse modelMap4 = dataProviderService.modifyProvider(providerReq);
        Assert.assertEquals(3L, (Object) modelMap4.getStatus());

        //保存失败
        List<DaConfigConnector> connectors4 = new ArrayList<>();
        DaConfigConnector con5 = new DaConfigConnector();
        con5.setId(1001L);
        con5.setName("delete");
        con5.setConnectorType("TIME_SERIES_DATA");
        con5.setConnectorClass("ARCHIVED");
        DaConfigConnector con6 = new DaConfigConnector();
        con6.setId(1002L);
        con6.setName("delete");
        con6.setConnectorType("TIME_SERIES_DATA");
        con6.setConnectorClass("ARCHIVED");
        connectors4.add(con5);
        connectors4.add(con6);
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        BeanUtils.copyProperties(providerReq, daConfigProvider);
        Mockito.when(daConfigConnectorMapper.selectConnectorsByIds(connectIds1)).thenReturn(connectors4);
        Mockito.when(daConfigProviderMapper.updateByPrimaryKeySelective(daConfigProvider)).thenReturn(0);
        ModifyProviderResponse modelMap5 = dataProviderService.modifyProvider(providerReq);
        Assert.assertEquals(5L, (Object) modelMap5.getStatus());

        Mockito.when(daConfigProviderMapper.updateByPrimaryKeySelective(daConfigProvider)).thenReturn(2);
        dataBaseConf.setType(DataBaseType.POSTGRESQL.getType());
        List<DaConfigProviderConnector> pcList1 = new ArrayList<>();
        DaConfigProviderConnector daConfigProviderConnector2 = new DaConfigProviderConnector();
        daConfigProviderConnector2.setConnectorId(1001L);
        daConfigProviderConnector2.setProviderId(1L);
        DaConfigProviderConnector daConfigProviderConnector3 = new DaConfigProviderConnector();
        daConfigProviderConnector3.setConnectorId(1002L);
        daConfigProviderConnector3.setProviderId(1L);
        pcList1.add(daConfigProviderConnector2);
        pcList1.add(daConfigProviderConnector3);
        Mockito.when(daConfigProviderConnectorMapper.batchInsertPG(pcList1)).thenReturn(0);
        ModifyProviderResponse modelMap6 = dataProviderService.modifyProvider(providerReq);
        Assert.assertEquals(6L, (Object) modelMap6.getStatus());

        Mockito.when(daConfigProviderConnectorMapper.batchInsertPG(pcList1)).thenReturn(1);
        ModifyProviderResponse modelMap7 = dataProviderService.modifyProvider(providerReq);
        Assert.assertEquals(0L, (Object) modelMap7.getStatus());
    }


    @Test
    public void queryConnectorListOfPiAf() {

        List<DaConfigConnector> daConfigConnectorList = dataProviderService.queryConnectorListOfPiAf();
        Assert.assertNotNull(daConfigConnectorList);

    }
}