package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.singleton.conf.AgentApolloIp;
import com.siemens.ofmcommon.log.RestTraceInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.TAG_LIST;

/**
 * SqlDasWindowsTestConServiceImpl
 *
 * @author xuxin
 * @date 2019/1/3
 */
@Service
public class SqlDasWindowsTestConServiceImpl implements ITestConnectorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MAXIMUM_LICENSED_POINT = "Maximum";
    private static final String PI_CLIENT_NO_AUTH = "Access";
    private static final String PI_CLIENT_TIMEOUT = "Timeout";


    @Resource(name = "commonRestTemplate")
    private RestTemplate commonRestTemplate;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private SqlDasLinuxTestConServiceImpl sqlDasLinuxTestConServiceImpl;

    @Autowired
    private AgentApolloIp agentApolloIp;

    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("sqldas",daConfigConnectorRequest.getSqldas());
        parameters.add("connectApproach",daConfigConnectorRequest.getConnectApproach());
        parameters.add("password",daConfigConnectorRequest.getPassword());
        parameters.add("serverHost",daConfigConnectorRequest.getServerHost());
        parameters.add("userName",daConfigConnectorRequest.getUserName());
        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters);
        ModelMap modelMap =null;
        String url = "http://" + agentApolloIp.getIp() + ":" + agentApolloIp.getPort() + "/ofm-da-core-agent/v2/provider/testConnectorsSqlDas";

        ResponseEntity<String> stringResponseEntity = commonRestTemplate.postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = (ModelMap) JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        return modelMap;
    }

    @Override
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) {

        ModelMap modelMap = new ModelMap();

        long bgTime = System.currentTimeMillis();
        Map<String,String> tagSet = null;
        try {
            tagSet = selectTagList(daConfigConnectorRequest);
        } catch (Exception e) {
            logger.error("selectTagList error :" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
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
            return sqlDasLinuxTestConServiceImpl.updateMockedConnectorSensorRelation(tagSet,existTagList,cs.getConnectorInfo(),modelMap,existCsList, daConfigConnectorRequest.getId(), daConfigConnectorRequest.getApplicationIdList(), sign);
        }

        bgTime = System.currentTimeMillis();
        int ret = sqlDasLinuxTestConServiceImpl.insertMockedConnectorSensorRelation(tagSet, cs.getConnectorInfo(), daConfigConnectorRequest.getId(), sign);
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
                .append(null == daConfigConnectorRequest.getServerHost() ? "" :daConfigConnectorRequest.getServerHost()).append("#")
                .append(null == daConfigConnectorRequest.getSqldas() ? "" : daConfigConnectorRequest.getSqldas()).append("#");
        return str.toString();
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("sqldas",daConfigConnectorRequest.getSqldas());
        parameters.add("password",daConfigConnectorRequest.getPassword());
        parameters.add("serverHost",daConfigConnectorRequest.getServerHost());
        parameters.add("userName",daConfigConnectorRequest.getUserName());
        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters, headers);
        ModelMap modelMap =
                null;
        String url = "http://" + agentApolloIp.getIp() + ":" + agentApolloIp.getPort() + "/ofm-da-core-agent/v2/provider/selectTagList";

        ResponseEntity<String> stringResponseEntity = commonRestTemplate.postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        if(null != modelMap && null != modelMap.get(IS_SUCCESS) && (Boolean) modelMap.get(IS_SUCCESS)){
            return (Map<String, String>)modelMap.get(DATA);
        }
        return null;
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, String tag) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("sqldas",daConfigConnectorRequest.getSqldas());
        parameters.add("password",daConfigConnectorRequest.getPassword());
        parameters.add("serverHost",daConfigConnectorRequest.getServerHost());
        parameters.add("userName",daConfigConnectorRequest.getUserName());
        parameters.add("tag",tag);
        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters, headers);
        daConfigConnectorRequest.setTag(tag);
        ModelMap modelMap =
                null;
        String url = "http://" + agentApolloIp.getIp() + ":" + agentApolloIp.getPort() + "/ofm-da-core-agent/v2/provider/selectTagListExact";

        ResponseEntity<String> stringResponseEntity = commonRestTemplate.postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        if(null != modelMap && null != modelMap.get(IS_SUCCESS) && (Boolean) modelMap.get(IS_SUCCESS)){
            return (Map<String, String>)modelMap.get(DATA);
        }
        return null;
    }

    public static void main(String[] args){
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        Set<String> tagList = new HashSet<>();
        tagList.add("xx01");
        tagList.add("xx02");
        parameters.add("sqldas","10.192.30.135");
        parameters.add("password","12345678");
        parameters.add("serverHost","10.192.30.135");
        parameters.add("userName","siemens-pdt");
        parameters.add("tagSet",tagList.stream().collect(Collectors.joining(",")));
        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters, headers);
        ModelMap modelMap =
                null;
        String url = "http://" + "127.0.0.1" + ":" + "8096" + "/ofm-da-core-agent/v2/provider/insertTagDataSqlDas";

        ResponseEntity<String> stringResponseEntity = commonRestTemplate().postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        if(null != modelMap && null != modelMap.get(IS_SUCCESS) && (Boolean) modelMap.get(IS_SUCCESS)){
            Map<String, String> ret = JSON.parseObject(modelMap.get(DATA).toString(),java.util.Map.class);
        }


    }

    @Bean(name = "commonRestTemplate")
    static RestTemplate commonRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(getSimpleClientHttpRequestFactory());
        restTemplate.setInterceptors(Collections.singletonList(new RestTraceInterceptor()));
        return restTemplate;
    }

    private static SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000000000);
        requestFactory.setReadTimeout(300000000);
        return requestFactory;
    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, List<String> tagList) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("sqldas",daConfigConnectorRequest.getSqldas());
        parameters.add("password",daConfigConnectorRequest.getPassword());
        parameters.add("serverHost",daConfigConnectorRequest.getServerHost());
        parameters.add("userName",daConfigConnectorRequest.getUserName());
        parameters.add("tagList", JSON.toJSONString(tagList));
        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters, headers);
        daConfigConnectorRequest.setTag(JSON.toJSONString(tagList));
        ModelMap modelMap =
                null;
        String url = "http://" + agentApolloIp.getIp() + ":" + agentApolloIp.getPort() + "/ofm-da-core-agent/v2/provider/batchSelectTagListExact";

        ResponseEntity<String> stringResponseEntity = commonRestTemplate.postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        if(null != modelMap && null != modelMap.get(IS_SUCCESS) && (Boolean) modelMap.get(IS_SUCCESS)){
            return (Map<String, List<String>>)modelMap.get(DATA);
        }
        return null;
    }

    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorRequest, HashSet<String> tagSet) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("sqldas",daConfigConnectorRequest.getSqldas());
        parameters.add("password",daConfigConnectorRequest.getPassword());
        parameters.add("serverHost",daConfigConnectorRequest.getServerHost());
        parameters.add("userName",daConfigConnectorRequest.getUserName());
        parameters.add("tagSet",tagSet.stream().collect(Collectors.joining(",")));
        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters, headers);
        daConfigConnectorRequest.setTagSetStr(tagSet.stream().collect(Collectors.joining(",")));
        ModelMap modelMap =
                null;
        String url = "http://" + agentApolloIp.getIp() + ":" + agentApolloIp.getPort() + "/ofm-da-core-agent/v2/provider/insertTagDataSqlDas";

        ResponseEntity<String> stringResponseEntity = commonRestTemplate.postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        return modelMap;
    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        ModelMap modelMap = insertTagData(daConfigConnectorReques,tagSet);
        if (!(Boolean) modelMap.get(IS_SUCCESS)) {
            throw new Exception((String)modelMap.get(MSG));
        }
        Map<String, List<String>> existTagMap = batchSelectTagListExact(daConfigConnectorReques,new ArrayList(tagSet));
        modelMap.put(SUCCESS, true);
        modelMap.put(DATA,existTagMap.get(TAG_LIST));
        return modelMap;
    }


}
