package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.model.dto.PiWebServiceRequest;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.singleton.conf.AgentApolloConn;
import com.siemens.dasheng.web.singleton.conf.AgentApolloIp;
import com.siemens.dasheng.web.singleton.constant.ModelConstant;
import com.siemens.ofmcommon.log.RestTraceInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * SdkWindowsTestConServiceImpl
 *
 * @author xuxin
 * @date 2019/1/3
 */
@Service
public class SdkWindowsTestConServiceImpl implements ITestConnectorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String SER_NOT_NULL = "serverhost can't be null or empty.";
    private static final String PI_ARICHIVE = "PI Arichive Server:";
    private static final String NOT_EXIST = "does not exist";
    private static final String USERNAME_NOT_NULL = "username can't be null or empty";
    private static final String PASSWD_NOT_NULL = "passwd can't be null or empty";
    private static final String UNAME_PWD_WRONG= "Exception:Cannot connect to the PI Data Archive. Windows authentication trial failed because the authentication method was not tried.";

    @Resource(name = "commonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private SqlDasLinuxTestConServiceImpl sqlDasLinuxTestConServiceImpl;

    @Autowired
    private AgentApolloIp agentApolloIp;

    @Autowired
    private AgentApolloConn agentApolloConn;

    private static final String MAXIMUM_LICENSED_POINT = "Maximum";
    private static final String PI_CLIENT_NO_AUTH = "Access";
    private static final String PI_CLIENT_TIMEOUT = "Timeout";

    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) {


        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost(daConfigConnectorRequest.getServerHost());
        piWebServiceRequest.setPasswd(daConfigConnectorRequest.getPassword());
        piWebServiceRequest.setUsername(daConfigConnectorRequest.getUserName());

        HttpEntity<PiWebServiceRequest> entity =
                new HttpEntity<>(piWebServiceRequest, headers);
        ModelMap modelMap =new ModelMap();
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/piarchive/testconnect";

        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null!=stringResponseEntity) {
            modelMap = (ModelMap) JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
            return structureRetMap(modelMap);
        }
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, CONNECT_FAIL);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;

    }


    private ModelMap structureRetMap(ModelMap modelMap) {

        if((Boolean) modelMap.get(SUCCESS)){
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECT_SUCCESS);
            modelMap.put(DATA, structureRetMap(true));
            return modelMap;
        }
        if(modelMap.get(MSG).toString().contains(SER_NOT_NULL)){
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECTOR_SERVER_HOST_IS_BLANK);
            modelMap.put(DATA, structureRetMap(false));
            return modelMap;
        }
        if(modelMap.get(MSG).toString().contains(PI_ARICHIVE) && modelMap.get(MSG).toString().contains(NOT_EXIST)){
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECTOR_SERVER_HOST_IS_WRONG);
            modelMap.put(DATA, structureRetMap(false));
            return modelMap;
        }
        if(modelMap.get(MSG).toString().contains(USERNAME_NOT_NULL) || modelMap.get(MSG).toString().contains(PASSWD_NOT_NULL)){
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECTOR_USERNAME_OR_PASSWORD_IS_BLANK);
            modelMap.put(DATA, structureRetMap(false));
            return modelMap;
        }
        if(modelMap.get(MSG).toString().contains(UNAME_PWD_WRONG)){
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECTOR_USERNAME_OR_PASSWORD_IS_WRONG);
            modelMap.put(DATA, structureRetMap(false));
            return modelMap;
        }


        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, CONNECT_FAIL);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }

    private Map<String,Object> structureRetMap(boolean flag) {
        Map<String,Object> retMap = new HashMap<String,Object>(4);
        retMap.put("status",flag);
        return retMap;
    }

    @Override
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {

        ModelMap modelMap = new ModelMap();

        long bgTime = System.currentTimeMillis();
        Map<String, String> tagSet = null;
        try {
            tagSet = selectTagList(daConfigConnectorRequest);
        } catch (Exception e) {
            logger.error("sdk selectTagList error : " + e.getMessage(),e);
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
        if (!CollectionUtils.isEmpty(existTagList)) {
            return sqlDasLinuxTestConServiceImpl.updateMockedConnectorSensorRelation(tagSet, existTagList, cs.getConnectorInfo(), modelMap, existCsList, daConfigConnectorRequest.getId(), daConfigConnectorRequest.getApplicationIdList(), sign);
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
        for (DaConfigConnectorSensor cs : existCsList) {
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
                .append(null == daConfigConnectorRequest.getServerHost() ? "" : daConfigConnectorRequest.getServerHost()).append("#");
        return str.toString();
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost(daConfigConnectorRequest.getServerHost());
        piWebServiceRequest.setQuerystr("*");

        HttpEntity<PiWebServiceRequest> entity =
                new HttpEntity<>(piWebServiceRequest, headers);
        ModelMap modelMap =null;
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/pipoint/taglist";

        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = (ModelMap) JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        Map<String, String> resultMap = new HashMap<>(1);
        if (modelMap != null && (Boolean) modelMap.get(SUCCESS)) {
            List<TagVo> tagVoList = JSON.parseArray (modelMap.get(DATA).toString(),TagVo.class);
            resultMap = tagVoList.stream().collect(Collectors.toMap(TagVo::getName,TagVo::getEngunits,(k1,k2)->k1));
        }
        return resultMap;
    }


    private static SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1000000000);
        requestFactory.setReadTimeout(300000000);
        return requestFactory;
    }

    public static class TagVo{
        private String name;

        private String engunits;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEngunits() {
            return engunits;
        }

        public void setEngunits(String engunits) {
            this.engunits = engunits;
        }
    }

    public Map<String, String> objToMap(Object obj) throws Exception {
        Map<String, String> map = new HashMap<String, String>(8);
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj).toString());
        }
        return map;
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, String tag) throws Exception {
        ModelMap modelMap = null;
        HttpEntity<PiWebServiceRequest> entity = assembleHeader(daConfigConnectorReques, tag);
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/pipoint/singletag";

        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        Map<String, String> resultMap = new HashMap<>(1);
        if (modelMap != null && (Boolean) modelMap.get(ModelConstant.SUCCESS)) {
            resultMap.put(tag, tag);
        }
        return resultMap;
    }

    public static void main(String[] args){
        ModelMap modelMap = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost("10.192.30.177");
        List<String> tagList = new ArrayList<>();
        tagList.add("WPU_TXLKE020.WindDir");
        tagList.add("WPU_TXLKE020.WindDi333r");
        piWebServiceRequest.setPointlist(JSON.toJSON(tagList).toString());
        HttpEntity<PiWebServiceRequest> entity =
                new HttpEntity<>(piWebServiceRequest, headers);
        String url = "http://" + "10.193.9.31" + ":" + "7788" + "/pipoint/inserttaglist";

        ResponseEntity<String> stringResponseEntity = restTemplate().postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        Map<String, List<String>> resultMap = new HashMap<>(1);

        if (modelMap != null && (Boolean) modelMap.get(ModelConstant.SUCCESS)) {
            modelMap.put(DATA,JSON.parseArray(modelMap.get(DATA).toString(),String.class));
            //resultMap.put(TAG_LIST, JSON.parseArray(modelMap.get(DATA).toString(),String.class));
        }

    }

    @Bean(name = "ribbonRestTemplate")
    @LoadBalanced
    static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(getSimpleClientHttpRequestFactory());
        restTemplate.setInterceptors(Collections.singletonList(new RestTraceInterceptor()));
        return restTemplate;
    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, List<String> tagList) throws Exception {
        ModelMap modelMap = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost(daConfigConnectorRequest.getServerHost());
        piWebServiceRequest.setPointlist(JSON.toJSON(tagList).toString());
        HttpEntity<PiWebServiceRequest> entity =
                new HttpEntity<>(piWebServiceRequest, headers);
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/pipoint/batchtag";

        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }

        Map<String, List<String>> resultMap = new HashMap<>(1);
        if (modelMap != null && (Boolean) modelMap.get(ModelConstant.SUCCESS)) {
            resultMap.put(TAG_LIST, JSON.parseArray(modelMap.get(DATA).toString(),String.class));
        }
        return resultMap;
    }


    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        ModelMap modelMap = null;
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/pipoint/inserttag";
        String tagName = null;
        for (String tag : tagSet) {
            tagName = tag;
            break;
        }
        HttpEntity<PiWebServiceRequest> entity = assembleHeader(daConfigConnectorReques, tagName);
        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
        }
        if (modelMap != null && (Boolean) modelMap.get(ModelConstant.SUCCESS)) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
        }
        if (modelMap != null && !(Boolean) modelMap.get(ModelConstant.SUCCESS)) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            String message = (String) modelMap.get(ModelConstant.MSG);
            if (message.contains(MAXIMUM_LICENSED_POINT)) {
                modelMap.put(MSG, PI_MAX_LICENCE_LIMIT);
            } else if (message.contains(PI_CLIENT_NO_AUTH)) {
                modelMap.put(MSG, PI_CLIENT_NO_AUTH_MSG);
            } else if (message.contains(PI_CLIENT_TIMEOUT)) {
                modelMap.put(MSG, PI_CONNECT_TIMEOUT_MSG);
            } else {
                modelMap.put(MSG, PI_UNKNOWN_MSG_ERROR);
            }
        }
        return modelMap;
    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorRequest, HashSet<String> tagSet) throws Exception {
        ModelMap modelMap = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost(daConfigConnectorRequest.getServerHost());
        piWebServiceRequest.setPointlist(JSON.toJSON(new ArrayList<>(tagSet)).toString());
        HttpEntity<PiWebServiceRequest> entity =
                new HttpEntity<>(piWebServiceRequest, headers);
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/pipoint/inserttaglist";

        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), ModelMap.class);
            modelMap.put(DATA,JSON.parseArray(modelMap.get(DATA).toString(),String.class));
        }
        return modelMap;
    }

    public static final HttpEntity<PiWebServiceRequest> assembleHeader(DaConfigConnectorRequest daConfigConnectorReques, String tag) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost(daConfigConnectorReques.getServerHost());
        piWebServiceRequest.setPasswd(daConfigConnectorReques.getPassword());
        piWebServiceRequest.setUsername(daConfigConnectorReques.getUserName());
        piWebServiceRequest.setAfdbname(daConfigConnectorReques.getDatabase());
        piWebServiceRequest.setTag(tag);
        return new HttpEntity<>(piWebServiceRequest, headers);
    }

}
