package com.siemens.dasheng.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.ErrorCodeCommon;
import com.siemens.dasheng.web.generalmodel.dataconnector.*;
import com.siemens.dasheng.web.model.Users;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.service.DataConnectorApplyService;
import com.siemens.dasheng.web.service.DataConnectorService;
import com.siemens.dasheng.web.singleton.constant.CommonConstant;
import com.siemens.ofmcommon.log.anotation.LogEventName;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static com.siemens.dasheng.web.singleton.constant.ControllerConstant.USER_SESSION;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @author xuxin
 * dataConnector管理接口
 * created by xuxin on 15/11/2018
 */
@Api(value = "dataConnector", description = "数据库连接创建接口")
@Controller
@RequestMapping("/connector")
@SuppressWarnings({"all"})
public class DataConnectorController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int NAME_DEFAULT_LENGTH = 30;

    private static final int DES_DEFAULT_LENGTH = 600;

    private static final int CHECKNO1 = 1;

    private static final int CHECKNO2 = 2;

    private static final int CHECKNO3 = 3;

    private static final int CHECKNO4 = 4;

    private static final int CHECKNO5 = 5;

    private static final String PROVIDERLIST = "providerlist";

    private static final String REPEAT_NAME = "repeatName";

    private static final String MATCH_NAME = "matchName";

    private static final String APP_NAMES = "appNames";

    private static final String PROVIDER_NAME = "providerName";

    private static final String IS_MATCH = "isMatch";

    private static final String PROVIDER_NUM = "providerNum";

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DataConnectorApplyService dataConnectorApplyService;


    @ApiOperation(value = "get single app's connectors status", notes = "get single app's connectors status")
    @GetMapping(value = "/monitor/{appId}")
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "get single app's connectors status", webUrl = "/connector/monitor/{appId}")
    public ModelMap appConnectors(@PathVariable String appId) {

        logger.info("get single app's connectors status, appid : {}",appId);
        ModelMap modelMap = new ModelMap();
        if (StringUtils.isEmpty(appId)) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, PARAMETER_ILLEGAL);
        } else {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(DATA, dataConnectorService.getDaMonitorConnectorList(appId));
        }

        return modelMap;
    }


    @ApiOperation(value = "view connect detail", notes = "view connect detail")
    @RequestMapping(value = "/viewConnector")
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "view connect detail", webUrl = "/connector/viewConnector")
    public ModelMap viewConnector(String connectorID, HttpServletRequest request) {
        logger.info("get single app's connectors status,connectorID : {}",connectorID);
        ModelMap modelMap = new ModelMap();
        Map<String, Object> resultMap;
        if (StringUtils.isBlank(connectorID)) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, DA_CONFIG_NOTBLANK_CONNECTOR_ID);
            return modelMap;
        }

        try {
            resultMap = dataConnectorService.getConnectorWithReferencedProvider(connectorID);
            if (Integer.valueOf(resultMap.get(STATUS).toString()).equals(CHECKNO1)) {
                //该连接不存在
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CONNECTOR_NOT_EXIST);
                return modelMap;
            }
        } catch (Exception e) {
            logger.error("viewConnector failed cause : " + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(DATA, resultMap);
        logger.info("viewConnector");
        return modelMap;
    }

    @ApiOperation(value = "test connect status", notes = "test connect status")
    @RequestMapping(value = "/testConnector", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "test connect status", webUrl = "/connector/testConnector")
    public ModelMap testConnector(@RequestBody @Valid DaConfigConnectorRequest daConfigConnectorRequest, BindingResult bindingResult, HttpServletRequest request) {
        ModelMap modelMap = new ModelMap();
        logger.info("testConnector " + JSON.toJSONString(daConfigConnectorRequest));
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            return dataConnectorService.testConnector(daConfigConnectorRequest);
        } catch (Exception e) {
            logger.error("test Connector Error:" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, CONNECT_FAIL);
            modelMap.put(DATA, structureRetMap(false));
            return modelMap;
        }

    }

    private ModelMap getErrorConnectMap(ModelMap modelMap, Boolean b, String msg) {
        modelMap.put(IS_SUCCESS, b);
        modelMap.put(MSG, msg);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }

    private Map<String, Object> structureRetMap(boolean flag) {
        Map<String, Object> retMap = new HashMap<String, Object>(4);
        retMap.put("status", flag);
        return retMap;
    }

    @ApiOperation(value = "save connector", notes = "save connector")
    @RequestMapping(value = "/saveConnector", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "save connector", webUrl = "/connector/saveConnector")
    public ModelMap saveConnector(@RequestBody @Valid DaConfigConnectorRequest daConfigConnectorRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("saveConnector " + JSON.toJSONString(daConfigConnectorRequest));
        ModelMap modelMap = new ModelMap();
        if (null == request) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, DPPRESOURCE_FAIL_NULLINPUT);
            return modelMap;
        }
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        if (StringUtils.isBlank(daConfigConnectorRequest.getName())) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, DA_CONFIG_CONNECTOR_NOTBLANK_NAME);
            return modelMap;
        }
        if (StringUtils.isBlank(daConfigConnectorRequest.getDbName())) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, DA_CONFIG_CONNECTOR_NOTBLANK_DB_NAME);
            return modelMap;
        }
        try {
            if (daConfigConnectorRequest.getName().getBytes(CommonConstant.UTF8).length > NAME_DEFAULT_LENGTH) {
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, CONNECTOR_NAME_LENGTH_MAXSIZE_30);
                return modelMap;
            }
            if (!StringUtils.isBlank(daConfigConnectorRequest.getDescription()) && daConfigConnectorRequest.getDescription().getBytes(CommonConstant.UTF8).length > DES_DEFAULT_LENGTH) {
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, CONNECTOR_DESCRIPTION_LENGTH_MAXSIZE_600);
                return modelMap;
            }
        } catch (UnsupportedEncodingException e) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, CONNECTOR_NAME_LENGTH_MAXSIZE_30);
            return modelMap;
        }
        daConfigConnectorRequest.setUpdatedBy(((Users) request.getSession().getAttribute(USER_SESSION)).getId().toString());
        daConfigConnectorRequest.setUpdateDate(System.currentTimeMillis());
        return dataConnectorService.saveConnector(daConfigConnectorRequest);
    }

    private boolean checkBindingResult(BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, bindingResult.getAllErrors().get(0).getDefaultMessage());
            modelMap.put(ERRORCODE, ErrorCodeCommon.DataCheckError.getErrorCode());
            return true;
        }
        return false;
    }

    @ApiOperation(value = "query connector list", notes = "query connector list")
    @RequestMapping(value = "/queryConnectors", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "query connector list", webUrl = "/connector/queryConnectors")
    public ModelMap queryConnectors(@RequestBody QueryConnectorRequest request) {
        logger.info("queryConnectors " + JSON.toJSONString(request));
        ModelMap modelMap = new ModelMap();
        modelMap.put(DATA, dataConnectorApplyService.queryConnectors(request));
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);

        return modelMap;
    }

    @ApiOperation(value = "delete connector", notes = "delete connector")
    @RequestMapping(value = "/deleteConnector", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "delete connector", webUrl = "/connector/deleteConnector")
    public ModelMap deleteConnector(@RequestBody DeleteConnectorRequest request) {
        logger.info("deleteConnector " + JSON.toJSONString(request));
        ModelMap modelMap = new ModelMap();
        DeleteConnectorResponse deleteConnectorResponse = dataConnectorApplyService.deleteConnector(request);
        Integer checkNo = deleteConnectorResponse.getCheckNo();
        if (checkNo == 0) {
            modelMap.put(DATA, deleteConnectorResponse.getNames());
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
        } else if (checkNo == CHECKNO1) {
            //该连接不存在
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CONNECTOR_NOT_EXIST);
        } else if (checkNo == CHECKNO2) {
            //该连接的饮用者已经激活，不能删除
            modelMap.put(DATA, deleteConnectorResponse.getNames());
            Map map = new HashMap(2);
            map.put(PROVIDERLIST, deleteConnectorResponse.getNames());
            modelMap.put(ATTACHMENT_MAP, map);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, REFERRER_ACTIVATED);
        } else if (checkNo == CHECKNO3) {
            //需要确认才能删除
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, NEED_BE_CONFIRM);
            modelMap.put(DATA, deleteConnectorResponse.getNames());
        } else if (checkNo == CHECKNO4) {
            //已被导入sensor
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CONNECTOR_IMPORTED_SENSOR);
        }
        return modelMap;
    }

    @ApiOperation(value = "query opcua events", notes = "query opcua events")
    @RequestMapping(value = "/events", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "update connector", webUrl = "/connector/events")
    public ModelMap queryOpcuaEvents(@RequestBody OpcUaEventsQueryRequest request) {

        logger.info("queryOpcUaEvents, params: {}", JSON.toJSONString(request));

        ModelMap modelMap = new ModelMap();

        return dataConnectorService.queryOpcUaEvents(request, modelMap);
    }


    @ApiOperation(value = "update connector", notes = "update connector")
    @RequestMapping(value = "/updateConnector", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "Connector", businessDescription = "update connector", webUrl = "/connector/updateConnector")
    public ModelMap updateConnector(@RequestBody @Valid UpdateConnectorRequest updateConnectorRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("updateConnector " + JSON.toJSONString(updateConnectorRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }

        if (checkRequest(request, modelMap)) {
            return modelMap;
        }

        UpdateConnectorResponse response = null;
        try {
            response = dataConnectorApplyService.updateConnector(updateConnectorRequest, ((Users) request.getSession().getAttribute(USER_SESSION)).getUsername());
        } catch (Exception e) {
            logger.error("update connector error :{}",e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, UPDATE_CONNECT_EROR);
            return modelMap;
        }

        if (null == response) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CONNECTOR_NOT_EXIST);
            modelMap.put(ERRORCODE, ErrorCodeCommon.DataCheckError.getErrorCode());
        } else if (!response.getUpdate()) {
            if (response.getStatus() == CHECKNO5) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                Map map = new HashMap(2);
                map.put(PROVIDER_NAME,struListToStr(response.getNames()));
                map.put(PROVIDER_NUM,response.getNames().size());
                modelMap.put(ATTACHMENT_MAP, map);
                modelMap.put(MSG, DATA_CONNECTOR_IMPORTED_ACTIVED_PROVIDER);
                modelMap.put(ERRORCODE, ErrorCodeCommon.DataCheckError.getErrorCode());
                return modelMap;
            } else if (response.getStatus() == CHECKNO1) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(REPEAT_NAME, response.getNames());
                modelMap.put(STATUS,CHECKNO1);
                return modelMap;
            } else if (response.getStatus() == CHECKNO2) {
                modelMap.put(REPEAT_NAME, response.getNames());
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(STATUS,CHECKNO2);
                return modelMap;
            }else if (response.getStatus() == CHECKNO3) {
                Map map = new HashMap(2);
                map.put(MATCH_NAME, response.getMatchName());
                map.put(APP_NAMES, struListToStr(response.getNames()));
                modelMap.put(ATTACHMENT_MAP, map);
                modelMap.put(MSG, DATA_CONNECTOR_IMPORTED_APP);
                modelMap.put(IS_MATCH,Boolean.FALSE);
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }else if (response.getStatus() == CHECKNO4) {
                modelMap.put(IS_MATCH,Boolean.TRUE);
                modelMap.put(MATCH_NAME,response.getMatchName());
                modelMap.put(APP_NAMES, response.getNames());
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                return modelMap;
            }
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, NEED_BE_CONFIRM);
            modelMap.put(DATA, response);
        } else {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
            modelMap.put(DATA, response);
        }

        return modelMap;
    }

    private String struListToStr(List<String> names) {
        StringBuilder str = new StringBuilder();
        for(String name : names){
            str.append(name).append(", ");
        }
        return str.substring(0,str.length()-2);
    }

    private boolean checkRequest(HttpServletRequest request, ModelMap modelMap) {
        if (null == request.getSession().getAttribute(USER_SESSION)) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, INVALID_PERMISSION);
            modelMap.put(ERRORCODE, ErrorCodeCommon.SessionTimeoutError.getErrorCode());
            return true;
        }
        return false;
    }


}


