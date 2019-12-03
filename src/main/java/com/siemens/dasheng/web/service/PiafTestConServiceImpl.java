package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.model.dto.PiWebServiceRequest;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.singleton.conf.AgentApolloConn;
import com.siemens.dasheng.web.singleton.conf.AgentApolloIp;
import com.siemens.ofmcommon.log.RestTraceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * PiafTestConServiceImpl
 *
 * @author xuxin
 * @date 2019/1/24
 */
@Service("piafTestConServiceImpl")
public class PiafTestConServiceImpl implements ITestConnectorService{

    private static final String SER_NOT_NULL = "serverhost can't be null or empty.";
    private static final String AFSERVER = "AFServer";
    private static final String NOT_EXIST = "does not exist";
    private static final String USERNAME_NOT_NULL = "username can't be null or empty";
    private static final String PASSWD_NOT_NULL = "passwd can't be null or empty";
    private static final String UNAME_PWD_WRONG= "Exception:The server has rejected the client credentials.";
    private static final String AFDATABASE = "AFDatabase";

    @Resource(name = "commonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    @Autowired
    private AgentApolloIp agentApolloIp;

    @Autowired
    private AgentApolloConn agentApolloConn;

    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        PiWebServiceRequest piWebServiceRequest = new PiWebServiceRequest();
        piWebServiceRequest.setServerhost(daConfigConnectorRequest.getServerHost());
        piWebServiceRequest.setPasswd(daConfigConnectorRequest.getPassword());
        piWebServiceRequest.setUsername(daConfigConnectorRequest.getUserName());
        piWebServiceRequest.setAfdbname(daConfigConnectorRequest.getDatabase());

        HttpEntity<PiWebServiceRequest> entity =
                new HttpEntity<>(piWebServiceRequest, headers);
        ModelMap modelMap = new ModelMap();
        String url = "http://" + agentApolloConn.getAgentip() + ":" + agentApolloConn.getAgentport() + "/piaf/testconnect";

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
        if(modelMap.get(MSG).toString().contains(AFSERVER) && modelMap.get(MSG).toString().contains(NOT_EXIST)){
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
        if(modelMap.get(MSG).toString().contains(AFDATABASE) && modelMap.get(MSG).toString().contains(NOT_EXIST)){
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, AF_DB_NAME_DONT_EXIST);
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
        retMap.put(STATUS,flag);
        return retMap;
    }

    @Override
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {
        return null;
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
                .append(null == daConfigConnectorRequest.getPort() ? "" : daConfigConnectorRequest.getPort()).append("#")
                .append(null == daConfigConnectorRequest.getDatabase() ? "" : daConfigConnectorRequest.getDatabase()).append("#");
        return str.toString();
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, String tag) throws Exception {
        return null;
    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, List<String> tag) throws Exception {
        return null;
    }

    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        return null;
    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        return null;
    }

}
