package com.siemens.dasheng.web.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.ErrorCodeCommon;
import com.siemens.dasheng.web.model.Users;
import com.siemens.dasheng.web.request.DaConfigProviderRequest;
import com.siemens.dasheng.web.request.ModifyProviderRequest;
import com.siemens.dasheng.web.request.QueryProviderRequest;
import com.siemens.dasheng.web.request.ValidateConnectorsStatusRequest;
import com.siemens.dasheng.web.response.ModifyProviderResponse;
import com.siemens.dasheng.web.response.QueryProviderResponse;
import com.siemens.dasheng.web.service.DataProviderService;
import com.siemens.dasheng.web.singleton.conf.ConnectorCache;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siemens.dasheng.web.singleton.constant.ControllerConstant.USER_SESSION;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @author xuxin
 * dataProvider管理接口
 * created by xuxin on 15/11/2018
 */
@Api(value = "dataProvider", description = "数据库连接创建接口")
@Controller
@RequestMapping("/provider")
public class DataProviderController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Long STATUS0 = 0L;
    private static final Long STATUS1 = 1L;
    private static final Long STATUS2 = 2L;
    private static final Long STATUS3 = 3L;
    private static final Long STATUS4 = 4L;
    private static final Long STATUS5 = 5L;
    private static final Long STATUS6 = 6L;
    private static final Long STATUS7 = 7L;
    private static final Long STATUS8 = 8L;
    private static final Long STATUS9 = 9L;

    private static final String CON_NAME = "conName";
    private static final String APP_NAME = "appName";
    private static final String REPEAT_NAME = "repeatName";
    private static final String NUM = "num";
    private static final String RES_LIST = "resList";

    @Autowired
    private DataProviderService dataProviderService;


    @ApiOperation(value = "queryConnectorFilter", notes = "queryConnectorFilter")
    @RequestMapping(value = "/queryConnectorFilter", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "query connector filter", webUrl = "/provider/queryConnectorFilter")
    public ModelMap queryConnectorFilter() {
        logger.info("queryConnectorFilter ");
        ModelMap modelMap = new ModelMap();

        try {
            return dataProviderService.queryConnectorFilter();
        } catch (Exception e) {
            logger.error("queryConnectorFilter Error:" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }

    @ApiOperation(value = "saveDataProvider", notes = "saveDataProvider")
    @RequestMapping(value = "/saveDataProvider", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "save data provider", webUrl = "/provider/saveDataProvider")
    public ModelMap saveDataProvider(@RequestBody @Valid DaConfigProviderRequest daConfigProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("saveDataProvider " + JSON.toJSONString(daConfigProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            daConfigProviderRequest.setUpdatedBy((null == request.getSession().getAttribute(USER_SESSION)) ? null : ((Users) request.getSession().getAttribute(USER_SESSION)).getId().toString());
            daConfigProviderRequest.setUpdateDate(System.currentTimeMillis());
            return dataProviderService.saveDataProvider(daConfigProviderRequest);
        } catch (Exception e) {
            logger.error("saveDataProvider Error:" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, SAVE_FAIL);
            return modelMap;
        }
    }

    @ApiOperation(value = "queryConnectorByClass", notes = "queryConnectorByClass")
    @RequestMapping(value = "/queryConnectorByClass/{classId}", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "query connector by class", webUrl = "/provider/queryConnectorByClass/{classId}")
    public ModelMap queryConnectorByClass(@PathVariable(name = "classId") String classId) {
        logger.info("queryConnectorByClass");
        ModelMap modelMap = new ModelMap();
        if (StringUtils.isBlank(classId)) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, DA_CONFIG_CONNECTOR_NOTBLANK_CONNECT_CLASS);
            return modelMap;
        }
        try {
            return dataProviderService.queryConnectorByClass(classId);
        } catch (Exception e) {
            logger.error("queryConnectorFilter Error:" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }

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

    @ApiOperation(value = "queryConnectorStatus", notes = "queryConnectorStatus")
    @RequestMapping(value = "/queryConnectorStatus", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "query connector status", webUrl = "/provider/queryConnectorStatus")
    public List<Map<String, Object>> queryConnectorStatus() {
        logger.info("queryConnectorStatus");
        List<Map<String, Object>> retList = new ArrayList<>();

        try {
            if (!ConnectorCache.getInstance().getMap().isEmpty()) {
                for (Long key : ConnectorCache.getInstance().getMap().keySet()) {
                    Map<String, Object> retMap = new HashMap<>(16);
                    retMap.put(ID, key);
                    retMap.put(STATUS, ConnectorCache.getInstance().getMap().get(key));
                    retList.add(retMap);
                }
            }
            logger.info("map:" + JSONUtils.toJSONString(retList));
            return retList;
        } catch (Exception e) {
            logger.error("queryConnectorStatus Error:" + e.getMessage(),e);
            return retList;
        }

    }

    @ApiOperation(value = "queryProviderList", notes = "queryProviderList")
    @RequestMapping(value = "/queryProviderList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "query provider list", webUrl = "/provider/queryProviderList")
    public ModelMap queryProviderList(@RequestBody @Valid QueryProviderRequest queryProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("queryProviderList " + JSON.toJSONString(queryProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            QueryProviderResponse data = dataProviderService.queryProviderList(queryProviderRequest);
            structData(modelMap, data);
        } catch (Exception e) {
            logger.error("queryProviderList" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
        }
        return modelMap;

    }

    @ApiOperation(value = "multiQueryProviderList", notes = "multiQueryProviderList")
    @RequestMapping(value = "/multiQueryProviderList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "multi query provider list", webUrl = "/provider/multiQueryProviderList")
    public ModelMap multiQueryProviderList(@RequestBody @Valid QueryProviderRequest queryProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("multiQueryProviderList " + JSON.toJSONString(queryProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            QueryProviderResponse data = dataProviderService.multiQueryProviderList(queryProviderRequest);
            structData(modelMap, data);
        } catch (Exception e) {
            logger.error("queryProviderList" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
        }
        return modelMap;

    }

    @ApiOperation(value = "queryConnectorListOfPiAf", notes = "queryConnectorListOfPiAf")
    @RequestMapping(value = "/connectorList/PIAF", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "query connector list of piAf", webUrl = "/provider/connectorList/PIAF")
    public ModelMap queryConnectorListOfPiAf() {
        logger.info("queryConnectorListOfPiAf");
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, SUCCESS);
        modelMap.put(DATA, dataProviderService.queryConnectorListOfPiAf());
        return modelMap;
    }

    @ApiOperation(value = "queryConnectorListByProviderId", notes = "queryConnectorListByProviderId")
    @RequestMapping(value = "/queryConnectorListByProviderId/{providerId}", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "query connector list by provider id", webUrl = "/provider/queryConnectorListByProviderId/{providerId}")
    public ModelMap queryConnectorListByProviderId(@PathVariable(name = "providerId") String providerId) {
        logger.info("queryConnectorListByProviderId");
        ModelMap modelMap = new ModelMap();
        try {
            long proId ;
            try{
                proId = Long.parseLong(providerId);
            }catch(NumberFormatException e){
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, PROVIDER_IS_NOT_EXIST);
                return modelMap;
            }
            QueryProviderResponse queryProviderResponse = dataProviderService.queryConnectorListByProviderId(proId);
            structProConListMap(modelMap, queryProviderResponse);
        } catch (Exception e) {
            logger.error("queryProviderList" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
        }
        return modelMap;
    }

    private void structProConListMap(ModelMap modelMap, QueryProviderResponse queryProviderResponse) {
        if (STATUS0.equals(queryProviderResponse.getStatus())) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, OPERATION_SUCCESS);
            modelMap.put(DATA, queryProviderResponse.getDaConfigProvider());
        } else if (STATUS7.equals(queryProviderResponse.getStatus())) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, PROVIDER_IS_NOT_EXIST);
        }
    }

    private void structData(ModelMap modelMap, QueryProviderResponse data) {
        if (data.getStatus() == 0L) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, OPERATION_SUCCESS);
            modelMap.put(DATA, data.getData());
        }

    }

    @ApiOperation(value = "validateConnectorsStatus", notes = "validateConnectorsStatus")
    @RequestMapping(value = "/validateConnectorsStatus", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "validate connectors status", webUrl = "/provider/validateConnectorsStatus")
    public ModelMap validateConnectorsStatus(@RequestBody @Valid ValidateConnectorsStatusRequest validateConnectorsStatusRequest) {
        logger.info("validateConnectorsStatus" + JSON.toJSONString(validateConnectorsStatusRequest));
        ModelMap modelMap = new ModelMap();
        try {
            Long retStu = dataProviderService.validateConnectorsStatus(validateConnectorsStatusRequest);
            structvalidateStatus(modelMap, retStu);
        } catch (Exception e) {
            logger.error("validateConnectorsStatus" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
        }
        return modelMap;
    }

    private void structvalidateStatus(ModelMap modelMap, Long retStu) {
        if (STATUS0.equals(retStu)) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, OPERATION_SUCCESS);
        } else if (STATUS1.equals(retStu)) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
        } else if (STATUS2.equals(retStu)) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, NO_CONNECTOR_ACTIVE);
        } else if (STATUS3.equals(retStu)) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, CONNECTORS_IS_UNAVAILABLE);
        }
    }

    @ApiOperation(value = "modifyProvider", notes = "modifyProvider")
    @RequestMapping(value = "/modifyProvider", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "modify provider", webUrl = "/provider/modifyProvider")
    public ModelMap modifyProvider(@RequestBody @Valid ModifyProviderRequest modifyProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("modifyProvider ," + JSON.toJSONString(modifyProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            ModifyProviderResponse res = dataProviderService.modifyProvider(modifyProviderRequest);
            structModifyProviderMap(modelMap, res);
        } catch (Exception e) {
            logger.error("modifyProvider" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, SAVE_FAIL);
        }
        return modelMap;
    }

    private void structModifyProviderMap(ModelMap modelMap, ModifyProviderResponse res) {
        if (STATUS0.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, MODIFY_SUCCESS);
        } else if (STATUS1.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, false);
            Map map = new HashMap(2);
            map.put(CON_NAME, struListToStr(res.getNames()));
            map.put(NUM, res.getNums());
            modelMap.put(ATTACHMENT_MAP, map);
            modelMap.put(MSG, CONNECTOR_NOT_CONNECT_AND_CANT_ACTIVE);
        } else if (STATUS2.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, false);
            Map map = new HashMap(2);
            map.put(APP_NAME, struListToStr(res.getNames()));
            modelMap.put(ATTACHMENT_MAP, map);
            modelMap.put(MSG, PROVIDER_IMPORTED_APP_AND_CANT_UPDATE_INACTIVE);
        } else if (STATUS3.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(REPEAT_NAME, res.getResponsesList());
            modelMap.put(STATUS,STATUS3);
        } else if (STATUS4.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(REPEAT_NAME, res.getNames());
            modelMap.put(STATUS,STATUS4);
        } else if (STATUS5.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(REPEAT_NAME, res.getNames());
            modelMap.put(STATUS,STATUS5);
        } else if (STATUS6.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, MODIFY_FAIL);
        } else if (STATUS7.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, PROVIDER_IS_NOT_EXIST);
        }else if (STATUS8.equals(res.getStatus())) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put("status", 0);
            modelMap.put("repeatConnectors", res.getLists());
        }else if (STATUS9.equals(res.getStatus())) {
            Map map = new HashMap(8);
            map.put("repeatName", res.getRepeatNames());
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(ATTACHMENT_MAP, map);
            modelMap.put(MSG, CONNECTOR_IS_NOT_EXIST);
        }
    }

    private String struListToStr(List<String> names) {
        StringBuilder str = new StringBuilder();
        for(String name : names){
            str.append(name).append(", ");
        }
        return str.substring(0,str.length()-2);
    }


    @GetMapping(value = "/deleteProvider/{providerId}")
    @ResponseBody
    @LogEventName(operateType = "PROVIDER", businessDescription = "delete provider", webUrl = "/provider/deleteProvider/{providerId}")
    public ModelMap deleteProvider(@PathVariable String providerId) {
        logger.info("deleteProvider");

        ModelMap modelMap = new ModelMap();

        if (StringUtils.isBlank(providerId)) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }

        try {
            Long ret = dataProviderService.deleteProvider(Long.valueOf(providerId));
            if(STATUS1.equals(ret)){
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, PROVIDER_IS_NOT_EXIST);
                return modelMap;
            }
            if(STATUS2.equals(ret)){
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, PROVIDER_IS_ACTIVATE_AND_DELETE_FAIL);
                return modelMap;
            }
            if(STATUS3.equals(ret)){
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, PROVIDER_IS_REGISTER_TO_APPLICATION);
                return modelMap;
            }

        } catch (Exception e) {
            logger.info("deleteProvider exception" + e);
            e.printStackTrace();
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
        }

        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        return modelMap;

    }

}
