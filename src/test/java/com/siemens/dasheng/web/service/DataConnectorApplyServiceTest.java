package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.BaseTest;
import com.siemens.dasheng.web.generalmodel.dataconnector.DeleteConnectorRequest;
import com.siemens.dasheng.web.generalmodel.dataconnector.QueryConnectorRequest;
import com.siemens.dasheng.web.generalmodel.dataconnector.QueryConnectorResponse;
import com.siemens.dasheng.web.generalmodel.dataconnector.UpdateConnectorRequest;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigProviderConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigProviderMapper;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.DaConfigProvider;
import org.jasypt.encryption.StringEncryptor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * @author liming
 * @Date: 2019/1/9 9:22
 */


public class DataConnectorApplyServiceTest extends BaseTest{

    @Before
    public void myBefore() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    DataConnectorApplyService dataConnectorApplyService;

    @Mock
    DaConfigConnectorMapper daConfigConnectorMapper;

    @Mock
    DaConfigProviderMapper daConfigProviderMapper;

    @Mock
    DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private StringEncryptor stringEncryptor;



    @Test
    public void queryConnectors() {
        QueryConnectorRequest queryConnectorRequest = new QueryConnectorRequest();
       queryConnectorRequest.setSearchStr("searchStr");
        queryConnectorRequest.setCategory("category");
        queryConnectorRequest.setConnectorClass("class");
        queryConnectorRequest.setConnectorType("type");
        List<QueryConnectorResponse> connectors = new ArrayList<>();
        QueryConnectorResponse queryConnectorResponse = new QueryConnectorResponse();
        queryConnectorResponse.setId(1);
        connectors.add(queryConnectorResponse);

        QueryConnectorResponse queryConnectorResponse2 = new QueryConnectorResponse();
        queryConnectorResponse2.setId(2);
        connectors.add(queryConnectorResponse2);
        Mockito.when(daConfigConnectorMapper.queryConnectors(queryConnectorRequest)).thenReturn(connectors);

        List<DaConfigProvider> daConfigProviders = new ArrayList<>();
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        daConfigProvider.setStatus(1l);
        daConfigProviders.add(daConfigProvider);
        Mockito.when(daConfigProviderMapper.selectByConnectId(1)).thenReturn(daConfigProviders);
        dataConnectorApplyService.queryConnectors(queryConnectorRequest);

    }

    @Test
    public void deleteConnector() {
        DeleteConnectorRequest isNull = new DeleteConnectorRequest();
        dataConnectorApplyService.deleteConnector(isNull);

        DeleteConnectorRequest notExist = new DeleteConnectorRequest();
        notExist.setId(1397l);
        dataConnectorApplyService.deleteConnector(notExist);

        DeleteConnectorRequest needConfirm = new DeleteConnectorRequest();
        needConfirm.setId(0l);

        DaConfigConnector daConfigConnector = new DaConfigConnector();
        Mockito.when(daConfigConnectorMapper.selectById(0l)).thenReturn(daConfigConnector);
        Mockito.when(daConfigConnectorMapper.selectById(1l)).thenReturn(daConfigConnector);
        Mockito.when(daConfigConnectorMapper.selectById(2l)).thenReturn(daConfigConnector);
        List<DaConfigProvider> daConfigProviders = new ArrayList<>();
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        daConfigProvider.setStatus(1l);
        daConfigProviders.add(daConfigProvider);
        DaConfigProvider daConfigProvider2 = new DaConfigProvider();
        daConfigProvider2.setStatus(2l);
        daConfigProviders.add(daConfigProvider2);
        Mockito.when(daConfigProviderMapper.selectByConnectId(0)).thenReturn(daConfigProviders);


        List<DaConfigProvider> daConfigProviders2 = new ArrayList<>();
        DaConfigProvider daConfigProvider3 = new DaConfigProvider();
        daConfigProvider3.setStatus(2l);
        daConfigProviders2.add(daConfigProvider3);
        DaConfigProvider daConfigProvider4 = new DaConfigProvider();
        daConfigProvider4.setStatus(2l);
        daConfigProviders2.add(daConfigProvider4);
        Mockito.when(daConfigProviderMapper.selectByConnectId(2)).thenReturn(daConfigProviders2);
        dataConnectorApplyService.deleteConnector(needConfirm);

        DeleteConnectorRequest cannotDelete = new DeleteConnectorRequest();
        cannotDelete.setId(0l);
        cannotDelete.setConfirm(true);
        dataConnectorApplyService.deleteConnector(cannotDelete);

        DeleteConnectorRequest delete = new DeleteConnectorRequest();
        delete.setId(1l);
        delete.setConfirm(true);
        dataConnectorApplyService.deleteConnector(delete);

        DeleteConnectorRequest delete2= new DeleteConnectorRequest();
        delete2.setId(2l);
        delete2.setConfirm(true);
        dataConnectorApplyService.deleteConnector(delete2);

    }

    @Test
    public void updateConnector() throws Exception {
        UpdateConnectorRequest updateConnectorRequest = new UpdateConnectorRequest();
        updateConnectorRequest.setId(1);
        DaConfigConnector daConfigConnector = new DaConfigConnector();
        Mockito.when(daConfigConnectorMapper.selectById(1l)).thenReturn(daConfigConnector);


        List<DaConfigProvider> daConfigProviders = new ArrayList<>();
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        daConfigProvider.setStatus(1l);
        daConfigProviders.add(daConfigProvider);
        DaConfigProvider daConfigProvider2 = new DaConfigProvider();
        daConfigProvider2.setStatus(2l);
        daConfigProviders.add(daConfigProvider2);
        Mockito.when(daConfigProviderMapper.selectByConnectId(1)).thenReturn(daConfigProviders);
        dataConnectorApplyService.updateConnector(updateConnectorRequest, "userName");

        updateConnectorRequest.setConfirm(true);
        updateConnectorRequest.setName("updatename");
        dataConnectorApplyService.updateConnector(updateConnectorRequest, "userName");

        updateConnectorRequest.setId(2);
        dataConnectorApplyService.updateConnector(updateConnectorRequest, "userName");

    }
}