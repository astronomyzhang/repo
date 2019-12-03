package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.ErrorCodeCommon;
import com.siemens.dasheng.web.request.*;
import com.siemens.dasheng.web.response.QueryApplicationTreeResponse;
import com.siemens.dasheng.web.service.*;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * DataApplicationController
 *
 * @author xuxin
 * @date 2019/3/5
 */
@Api(value = "dataApplication", description = "application")
@Controller
@RequestMapping("/application")
public class DataApplicationController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataApplicationService dataApplicationService;

    @Autowired
    private DataProviderService dataProviderService;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DataSensorService dataSensorService;

    @Autowired
    private LicenseClientService licenseClientService;

    private static final String LICENSE_NUM = "licenseNum";

    private static final String SENSOR_NUM = "sensorNum";

    @ApiOperation(value = "queryApplicationTree", notes = "queryApplicationTree")
    @RequestMapping(value = "/queryApplicationTree", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "query application tree", webUrl = "/application/queryApplicationTree")
    public ModelMap queryApplicationTree(@RequestBody @Valid DaConfigConnectorRequest daConfigConnectorRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("queryApplicationTree,"+JSON.toJSONString(daConfigConnectorRequest));
        ModelMap modelMap = new ModelMap();
        QueryApplicationTreeResponse queryApplicationTreeResponse = dataApplicationService.queryApplicationTree(request);
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(DATA, queryApplicationTreeResponse.getApplicationList());
        return modelMap;
    }

    @ApiOperation(value = "initConnectorTag", notes = "initConnectorTag")
    @RequestMapping(value = "/initConnectorTag", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "init connector tag", webUrl = "/application/initConnectorTag")
    public ModelMap initConnectorTag(@RequestBody @Valid QueryApplicationProviderRequest queryApplicationProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("initConnectorTag,"+JSON.toJSONString(queryApplicationProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            dataApplicationService.initConnectorTag(queryApplicationProviderRequest.getApplicationId());
        } catch (RuntimeException e) {
            logger.error("initConnectorTag error :" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, TAG_POOL_IS_UPDATING);
            return modelMap;
        } catch (Exception e) {
            logger.error("initConnectorTag error :" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        return modelMap;
    }

    @ApiOperation(value = "queryApplicationList", notes = "queryApplicationList")
    @RequestMapping(value = "/queryApplicationList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "query application list", webUrl = "/application/queryApplicationList")
    public ModelMap queryApplicationList(@RequestBody @Valid QueryApplicationListRequest queryApplicationListRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("queryApplicationList,"+JSON.toJSONString(queryApplicationListRequest));
        ModelMap modelMap = new ModelMap();
        try {
            try{
                Long.parseLong(queryApplicationListRequest.getApplicationId());
            }catch(NumberFormatException e){
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, APPLICATION_NOT_EXIST);
                return modelMap;
            }
            modelMap.put(DATA, dataApplicationService.queryApplicationList(queryApplicationListRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } catch (Exception e) {
            logger.error("queryApplicationList Error URI",e);
            e.printStackTrace();
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }

    @ApiOperation(value = "queryApplicationProvider", notes = "queryApplicationProvider")
    @RequestMapping(value = "/queryApplicationProvider", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "query application provider", webUrl = "/application/queryApplicationProvider")
    public ModelMap queryApplicationProvider(@RequestBody @Valid QueryApplicationProviderRequest queryApplicationProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("queryApplicationProvider,"+JSON.toJSONString(queryApplicationProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            modelMap.put(DATA, dataProviderService.queryApplicationProvider(queryApplicationProviderRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } catch (Exception e) {
            logger.error("queryApplicationProvider ",e);
            e.printStackTrace();
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
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


    @ApiOperation(value = "validateSieCodeIsImported", notes = "validateSieCodeIsImported")
    @RequestMapping(value = "/validateSieCodeIsImported", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "validate siecode is imported", webUrl = "/application/validateSieCodeIsImported")
    public ModelMap validateSieCodeIsImported(@RequestBody @Valid ValidateSieCodeIsImportedRequest validateSieCodeIsImportedRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("validateSieCodeIsImported,"+JSON.toJSONString(validateSieCodeIsImportedRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {

            modelMap.put(DATA, dataApplicationService.validateSieCodeIsImported(validateSieCodeIsImportedRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } catch (Exception e) {
            logger.error("validateSieCodeIsImportedRequest",e);
            e.printStackTrace();
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }


    @ApiOperation(value = "importTagToSensor", notes = "importTagToSensor")
    @RequestMapping(value = "/importTagToSensor", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "import tag to sensor", webUrl = "/application/importTagToSensor")
    public ModelMap importTagToSensor(@RequestBody @Valid ImportTagToSensorRequest importTagToSensorRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("importTagToSensor,"+JSON.toJSONString(importTagToSensorRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            modelMap.put(DATA, dataApplicationService.importTagToSensor(importTagToSensorRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } catch (Exception e) {
            logger.error("importTagToSensorRequest",e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }

    @ApiOperation(value = "querySensorList", notes = "querySensorList")
    @RequestMapping(value = "/querySensorList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "query sensor list", webUrl = "/application/querySensorList")
    public ModelMap querySensorList(@RequestBody @Valid QuerySensorListRequest querySensorListRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("querySensorList,"+JSON.toJSONString(querySensorListRequest));
        ModelMap modelMap = new ModelMap();
        try {
            modelMap.put(DATA, dataApplicationService.querySensorList(querySensorListRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } catch (Exception e) {
            logger.error("querySensorList Error ",e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }


    @ApiOperation(value = "queryProviderConnectorInfo", notes = "queryProviderConnectorInfo")
    @RequestMapping(value = "/queryProviderConnectorInfo", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "query provider connector info", webUrl = "/application/queryProviderConnectorInfo")
    public ModelMap queryProviderConnectorInfo(@RequestBody @Valid QueryApplicationProviderRequest queryApplicationProviderRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("queryProviderConnectorInfo,"+JSON.toJSONString(queryApplicationProviderRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            modelMap.put(DATA, dataProviderService.assembleProConMap(queryApplicationProviderRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } catch (Exception e) {
            logger.error("queryProviderConnectorInfo error :" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }

    @ApiOperation(value = "verifyCanBeCreated", notes = "verifyCanBeCreated")
    @RequestMapping(value = "/verifyCanBeCreated", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "verify can be created", webUrl = "/application/verifyCanBeCreated")
    public ModelMap verifyCanBeCreated(@RequestBody @Valid CheckTagExistRequest checkTagExistRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("verifyCanBeCreated,"+JSON.toJSONString(checkTagExistRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            if(dataConnectorService.isConnectorOp(checkTagExistRequest.getConnectorId())){
                checkTagExistRequest.setTag(dataSensorService.struOpTag(checkTagExistRequest.getConnectorId())+checkTagExistRequest.getTag().toUpperCase());
            }
            modelMap = dataConnectorService.checkTagExistByConnectorId(checkTagExistRequest.getConnectorId()
                    , checkTagExistRequest.getTag(), checkTagExistRequest.getToBeCreated(), checkTagExistRequest.getApplicationId(), modelMap);
            return modelMap;
        } catch (Exception e) {
            logger.error("verifyCanBeCreated Error :" + e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }


    @ApiOperation(value = "saveSensorRegistration", notes = "saveSensorRegistration")
    @RequestMapping(value = "/saveSensorRegistration", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "APPLICATION", businessDescription = "save sensor registration", webUrl = "/application/saveSensorRegistration")
    public ModelMap registSensorAndTag(@RequestBody @Valid SensorRegistrationRequest sensorRegistrationRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("registSensorAndTag,"+JSON.toJSONString(sensorRegistrationRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            if (sensorRegistrationRequest == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            //regist logic
            modelMap = dataSensorService.saveSensorRegistration(sensorRegistrationRequest, modelMap);
            return modelMap;
        } catch (Exception e) {
            logger.error("saveSensorRegistration error :" + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            return modelMap;
        }
    }
}
