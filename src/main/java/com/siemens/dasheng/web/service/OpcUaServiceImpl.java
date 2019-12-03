package com.siemens.dasheng.web.service;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.OpcuaGateway;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.ofm.opcua.Client;
import com.siemens.ofm.opcua.config.UaClientConfiguration;
import com.siemens.ofm.opcua.model.UaEvent;
import com.siemens.ofm.opcua.model.UaTag;
import com.siemens.ofm.opcua.service.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.CONNECTOR_URL_IS_BLANK;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.CONNECT_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * OpcUaServiceImpl
 *
 * @author xuxin
 * @date 2019/7/23
 */
@Service
public class OpcUaServiceImpl implements ITestConnectorService {

    private static final String URL = "opc.tcp://10.192.30.177:48050";

    private static final String DA_SERVER_NAME = "OSI.DA.1";

    private static final String HDA_SERVER_NAME = "OSI.HDA.1";

    private static final String SPLIT = "#,";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SqlDasLinuxTestConServiceImpl sqlDasLinuxTestConServiceImpl;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private Client client;



    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {

        ModelMap modelMap = new ModelMap();
        if (StringUtils.isBlank(daConfigConnectorRequest.getUrl())) {
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_URL_IS_BLANK);
        }
        UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(daConfigConnectorRequest.getUrl(),daConfigConnectorRequest.getUserName(),daConfigConnectorRequest.getPassword(),daConfigConnectorRequest.getDaServerName(),daConfigConnectorRequest.getHdaServerName());
        uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().toString().equals(daConfigConnectorRequest.getGateway().toString()));
        VerifyConnectionResult isSuc = client.verify(uaClientConfiguration);
        if (isSuc.getSuccess()) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECT_SUCCESS);
            modelMap.put(DATA, structureRetMap(true));
        }else{
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, isSuc.getCode());
            Map<String, Object> data = structureRetMap(false);
            data.put(MSG, isSuc.getDetail());
            modelMap.put(DATA, data);
        }
        return modelMap;
    }

    private ModelMap getErrorConnectMap(ModelMap modelMap, Boolean b, String msg) {
        modelMap.put(IS_SUCCESS, b);
        modelMap.put(MSG, msg);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }

    private Map<String, Object> structureRetMap(boolean flag) {
        Map<String, Object> retMap = new HashMap<String, Object>(4);
        retMap.put(STATUS, flag);
        return retMap;
    }

    public static void main(String[] args){
        try{
            UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(URL,"administrator","ofm123##",DA_SERVER_NAME,HDA_SERVER_NAME);
            uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().equals(0L));
            Client client = new Client();
            List<String> tagList = new ArrayList<>();
            tagList.add("WPU_TXLKE050.YawPos.Siemens.4627");

            VerifyConnectionResult isSuc = client.verify(uaClientConfiguration);
            Map<String, UaTag> tagMap = client.browse(uaClientConfiguration);
            List<String> existTagList = client.checkExist(uaClientConfiguration,tagList);
            Map<String, List<String>> retMap = new HashMap<>();
            retMap.put(TAG_LIST,existTagList);
        }catch(Exception e){

            System.out.println(e);
        }



        UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(URL,"","",DA_SERVER_NAME,HDA_SERVER_NAME);
        uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().equals(1L));
        Client client = new Client();
        NodeService ns = new NodeServiceImpl();
        ReadService rs = new ReadServiceImpl();
        WriteService ws = new WriteServiceImpl();
        client.setNodeService(ns);
        client.setReadService(rs);
        client.setWriteService(ws);
        try {
            Map<String, UaTag> tagMap = client.browse(uaClientConfiguration);
            System.out.println(tagMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {
        ModelMap modelMap = new ModelMap();

        long bgTime = System.currentTimeMillis();

        Map<String,String> tagSet = selectTagList(daConfigConnectorRequest);
        logger.info("-----------selectTagList size-----------------"+ tagSet.size());
        long endTime = System.currentTimeMillis();
        logger.info("Select taglist use time:{}ms, DaConfigConnector infos:{}", endTime - bgTime, JSON.toJSONString(daConfigConnectorRequest));
        if (CollectionUtils.isEmpty(tagSet)) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        }

        //query exist tagList
        DaConfigConnectorSensor cs = new DaConfigConnectorSensor();
        cs.setConnectorId(daConfigConnectorRequest.getId());
        List<DaConfigConnectorSensor> existCsList = daConfigConnectorSensorMapper.select(cs);
        List<String> existTagList = struExistTagList(existCsList);
        if(!CollectionUtils.isEmpty(existTagList)){
            sqlDasLinuxTestConServiceImpl.updateMockedConnectorSensorRelation(tagSet,existTagList,cs.getConnectorInfo(),modelMap,existCsList, daConfigConnectorRequest.getId(), daConfigConnectorRequest.getApplicationIdList(), sign);
            //更新prefix
            for(DaConfigConnectorSensor consen : existCsList){
                if(tagSet.get(consen.getTag()) != null){
                    String[] values = tagSet.get(consen.getTag()).split(SPLIT);
                    if(!values[0].equals(consen.getDaPrefix()) || !values[1].equals(consen.getHdaPrefix())){
                        daConfigConnectorSensorMapper.updatePrefixById(consen.getId(),values[0],values[1]);
                    }
                }
            }
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        }

        bgTime = System.currentTimeMillis();
        int ret = sqlDasLinuxTestConServiceImpl.insertMockedConnectorSensorRelation(tagSet, cs.getConnectorInfo(), daConfigConnectorRequest.getId(),sign);
        endTime = System.currentTimeMillis();
        logger.info("Insert snesorMapping use time:{}ms, DaConfigConnector infos:{}", endTime - bgTime, JSON.toJSONString(daConfigConnectorRequest));
        if (ret > 0) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } else {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    private List<String> struExistTagList(List<DaConfigConnectorSensor> existCsList) {
        List<String> tagList = new ArrayList<>();
        for ( DaConfigConnectorSensor cs : existCsList){
            tagList.add(cs.getTag());
        }
        return tagList;
    }

    @Override
    public ModelMap deleteConnectorSensorRelation(Long connectorId) throws Exception {
        return null;
    }

    @Override
    public String structureConnectorInfo(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append(daConfigConnectorRequest.getConnectorType()).append("#").append(daConfigConnectorRequest.getConnectorClass()).append("#")
                .append(daConfigConnectorRequest.getArchivedDatabase()).append("#").append(daConfigConnectorRequest.getConnectApproach()).append("#")
                .append(null == daConfigConnectorRequest.getUrl() ? "" :daConfigConnectorRequest.getUrl()).append("#")
                .append(null == daConfigConnectorRequest.getDaServerName() ? "" : daConfigConnectorRequest.getDaServerName()).append("#")
                .append(null == daConfigConnectorRequest.getHdaServerName() ? "" : daConfigConnectorRequest.getHdaServerName()).append("#")
                .append(null == daConfigConnectorRequest.getGateway() ? "" : daConfigConnectorRequest.getGateway()).append("#");
        return str.toString();
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(daConfigConnectorRequest.getUrl(),daConfigConnectorRequest.getUserName(),daConfigConnectorRequest.getPassword(),daConfigConnectorRequest.getDaServerName(),daConfigConnectorRequest.getHdaServerName());
        uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().toString().equals(daConfigConnectorRequest.getGateway().toString()));
        Map<String, UaTag> tagMap = client.browse(uaClientConfiguration);
        if(tagMap.isEmpty()){
            return null;
        }
        Map<String,String> retMap = new HashMap<>(4);
        for(Map.Entry<String, UaTag> map : tagMap.entrySet()){
            retMap.put(map.getValue().getTagName(), map.getValue().getDaPrefix()+SPLIT+map.getValue().getHdaPrefix()+SPLIT+map.getValue().getUnit());
        }
        return retMap;
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, String tag) throws Exception {
        UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(daConfigConnectorRequest.getUrl(),daConfigConnectorRequest.getUserName(),daConfigConnectorRequest.getPassword(),daConfigConnectorRequest.getDaServerName(),daConfigConnectorRequest.getHdaServerName());
        uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().toString().equals(daConfigConnectorRequest.getGateway().toString()));
        List<String> tagList = new ArrayList<>();
        tagList.add(tag);
        List<String> existTagList = client.checkExist(uaClientConfiguration,tagList);
        Map<String,String> retMap = existTagList.stream().collect(Collectors.toMap(a ->{return  a;},a->{return "";}, (key1, key2) -> key2));
        return retMap;
    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, List<String> tagList) throws Exception {
        UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(daConfigConnectorRequest.getUrl(),daConfigConnectorRequest.getUserName(),daConfigConnectorRequest.getPassword(),daConfigConnectorRequest.getDaServerName(),daConfigConnectorRequest.getHdaServerName());
        uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().toString().equals(daConfigConnectorRequest.getGateway().toString()));
        List<String> existTagList = client.checkExist(uaClientConfiguration,tagList);
        Map<String, List<String>> retMap = new HashMap<>();
        retMap.put(TAG_LIST,existTagList);
        return retMap;
    }

    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        return null;
    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        return null;
    }

    public List<UaEvent> queryOpcUaEvents(DaConfigConnector connector, long start, long end, String nodeId) throws Exception {

        UaClientConfiguration uaClientConfiguration = new UaClientConfiguration(connector.getUrl(),connector.getUserName(), connector.getPassword(), connector.getDaServerName(),connector.getHdaServerName());
        uaClientConfiguration.setUseBridge(OpcuaGateway.YES.getType().toString().equals(connector.getGateway().toString()));

        List<UaEvent> events = client.readHistoryEvents(uaClientConfiguration, nodeId, start, end);

        return events;
    }
}
