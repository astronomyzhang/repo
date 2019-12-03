package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.DaCoreErrorCodeEnum;
import com.siemens.dasheng.web.enums.ErrorCodeCommon;
import com.siemens.dasheng.web.enums.ObjectTypeEnum;
import com.siemens.dasheng.web.model.DaAppResourceUsage;
import com.siemens.dasheng.web.model.DaConfigGroupPlus;
import com.siemens.dasheng.web.model.dto.SimpleSensor;
import com.siemens.dasheng.web.request.DaConfigGroupRequest;
import com.siemens.dasheng.web.request.DeleteSensorRequest;
import com.siemens.dasheng.web.request.QuerySensorAppListRequest;
import com.siemens.dasheng.web.request.SensorRegistrationRequest;
import com.siemens.dasheng.web.response.DeleteSensorResponse;
import com.siemens.dasheng.web.response.ModifySensorGroupResponse;
import com.siemens.dasheng.web.response.ModifySensorResponse;
import com.siemens.dasheng.web.response.RemoveSensorGroupResponse;
import com.siemens.dasheng.web.service.DataSensorService;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.CommonConstant.*;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @description: sensor message manage controller
 * @author: ly
 * @create: 2019-04-03 16:29
 */
@Api(value = "dataSensor", description = "sensor")
@Controller
@RequestMapping("/daSensor")
public class DataSensorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private DataSensorService sensorService;

    @ApiOperation(value = "sensor info", notes = "view sensor info")
    @GetMapping(value = "/sensor_info/v1")
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "get sensor info", webUrl = "/daSensor/sensor_info/v1")
    public ModelMap simpleSensorInfo(String sieCode) {
        logger.info("simpleSensorInfo ,sieCode : {}", sieCode);
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        try {
            Map<String, Object> map = sensorService.getSensorInfoBySieCode(sieCode);
            modelMap.put(DATA, map);
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("query sensor group list error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "sensorGroupList", notes = "view sensor group list")
    @RequestMapping(value = "/sensorGroupList", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "get sensor group list", webUrl = "/daSensor/sensorGroupList")
    public ModelMap sensorGroupList(Long appId) {
        logger.info("sensorGroupList ,appId : {}", appId);
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        try {
            List<DaConfigGroupPlus> list = sensorService.getSensorGroupList(appId);
            modelMap.put(DATA, list);
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("query sensor group list error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "sensorList", notes = "view sensor list")
    @RequestMapping(value = "/sensorList", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "get sensor list", webUrl = "/daSensor/sensorList")
    public ModelMap sensorList(Long appId) {
        logger.info("sensorList ,appId : {}", appId);
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        try {
            Map<String, Object> result = new HashMap<>(2);
            List<String> sensorList = sensorService.getSensorList(appId);
            result.put("sensorList", sensorList.stream().map(s -> {
                Map<String, String> map = new HashMap<>(4);
                map.put("key", s);
                map.put("title", s);
                return map;
            }).collect(Collectors.toList()));
            modelMap.put(DATA, result);
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("query sensor list error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "getSensorListByGlobalId", notes = "getSensorListByGlobalId")
    @GetMapping("/getSensorListByGlobalId/{globalId}")
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "get sensor list by global id", webUrl = "/daSensor/getSensorListByGlobalId/{globalId}")
    public ModelMap getSensorListByGlobalId(@PathVariable String globalId) {
        logger.info("getSensorListByGlobalId");

        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, OPERATION_SUCCESS);
        modelMap.put(DATA, sensorService.getSensorListByGlobalId(globalId));
        return modelMap;

    }

    @ApiOperation(value = "addSensorGroup", notes = "add sensor group")
    @RequestMapping(value = "/addSensorGroup", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "add sensor group", webUrl = "/daSensor/addSensorGroup")
    public ModelMap addSensorGroup(@RequestBody DaConfigGroupRequest daConfigGroupRequest) {
        logger.info("addSensorGroup,"+JSON.toJSONString(daConfigGroupRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        try {
            sensorService.addSensorGroup(daConfigGroupRequest);
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, GROUP_NAME_EXIST);
            logger.error("add sensor group error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "viewSensorGroup", notes = "view sensor group")
    @RequestMapping(value = "/viewSensorGroup", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "view sensor group", webUrl = "/daSensor/viewSensorGroup")
    public ModelMap viewSensorGroup(Long groupId) {
        logger.info("viewSensorGroup,groupId:{}", groupId);
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        try {
            modelMap.put(DATA, sensorService.viewSensorGroup(groupId));
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("view sensor group error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "removeSensorGroup", notes = "remove sensor group")
    @RequestMapping(value = "/removeSensorGroup", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "remove sensor group", webUrl = "/daSensor/removeSensorGroup")
    public ModelMap removeSensorGroup(Long groupId) {
        logger.info("removeSensorGroup,groupId : {}",groupId);
        ModelMap modelMap = new ModelMap();
        try {
            RemoveSensorGroupResponse res = sensorService.removeSensorGroup(groupId);
            if (res.getStatus().equals(STATUS0)) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(MSG, OPERATION_SUCCESS);
                modelMap.put(STATUS, STATUS0);
            } else if (res.getStatus().equals(STATUS1)) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(DATA, res.getSensorList());
                modelMap.put(STATUS, STATUS1);
            }
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("remove sensor group error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "modifySensorGroup", notes = "modify sensor group")
    @RequestMapping(value = "/modifySensorGroup", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "modify sensor group", webUrl = "/daSensor/modifySensorGroup")
    public ModelMap modifySensorGroup(@RequestBody DaConfigGroupRequest daConfigGroupRequest) {
        logger.info("modifySensorGroup,"+JSON.toJSONString(daConfigGroupRequest));
        ModelMap modelMap = new ModelMap();
        try {
            ModifySensorGroupResponse res = sensorService.modifySensorGroup(daConfigGroupRequest);
            if (res.getStatus().equals(STATUS0)) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(MSG, OPERATION_SUCCESS);
                modelMap.put(STATUS, STATUS0);
            } else if (res.getStatus().equals(STATUS1)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, GROUP_NAME_EXIST);
            } else if (res.getStatus().equals(STATUS2)) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(DATA, res.getUserdSensorList());
                modelMap.put(STATUS, STATUS2);
            } else if (res.getStatus().equals(STATUS3)) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(DATA, res.getSensorCodeList());
                modelMap.put(STATUS, STATUS3);
            }
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("modify sensor group error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "deleteSensor", notes = "deleteSensor")
    @RequestMapping(value = "/deleteSensor", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "delete sensor", webUrl = "/daSensor/deleteSensor")
    public ModelMap deleteSensor(@RequestBody DeleteSensorRequest deleteSensorRequest) {
        logger.info("deleteSensor,"+JSON.toJSONString(deleteSensorRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        modelMap.put(STATUS, STATUS0);
        try {
            DeleteSensorResponse res = sensorService.deleteSensor(deleteSensorRequest);
            if (res.getStatus().equals(STATUS3)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, SENSOR_NOT_EXIST);
            } else if (res.getStatus().equals(STATUS4)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, WRITEBACK_SENSOR_CANT_DELETE);
            } else if (res.getStatus().equals(STATUS1)) {
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(DATA, res.getUsages());
                modelMap.put(STATUS, STATUS1);
            } else if (res.getStatus().equals(STATUS2)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, OPERATE_FAIL);
            }

        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("deleteSensor error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "querySensorAppList", notes = "querySensorAppList")
    @RequestMapping(value = "/querySensorAppList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "query sensor app list", webUrl = "/daSensor/querySensorAppList")
    public ModelMap querySensorAppList(@RequestBody QuerySensorAppListRequest querySensorAppListRequest) {
        logger.info("querySensorAppList,"+JSON.toJSONString(querySensorAppListRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, OPERATION_SUCCESS);
        try {
            if (querySensorAppListRequest.getObjectType().equals(ObjectTypeEnum.SENSOR.getType())) {
                querySensorAppListRequest.setObjectId(URLDecoder.decode(querySensorAppListRequest.getObjectId(), "utf-8"));
            }
            List<DaAppResourceUsage> res = sensorService.querySensorAppList(querySensorAppListRequest);
            modelMap.put(DATA, res);
        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, OPERATE_FAIL);
            logger.error("querySensorAppList error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    @ApiOperation(value = "daSensor", notes = "daSensor")
    @PutMapping(value = "/daSensor/v1")
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "modify sensor", webUrl = "/daSensor/daSensor/v1")
    public ModelMap modifySensor(@RequestBody SensorRegistrationRequest modifySensorRequest, BindingResult bindingResult, HttpServletRequest request) {
        logger.info("modifySensor,"+JSON.toJSONString(modifySensorRequest));
        ModelMap modelMap = new ModelMap();
        if (checkBindingResult(bindingResult, modelMap)) {
            return modelMap;
        }
        try {
            ModifySensorResponse res = sensorService.modifySensor(modifySensorRequest);
            return struModifySensorResponse(res, modelMap);
        } catch (Exception e) {
            logger.error("querySensorAppList error : " + e.getMessage(),e);
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.OPERATE_FAIL);
        }
    }

    @ApiOperation(value = "update app version and sensor group version", notes = "update app version and sensor group version")
    @PutMapping(value = "/groupversion")
    @ResponseBody
    @LogEventName(operateType = "DASENSOR", businessDescription = "update app and sensor group version", webUrl = "/daSensor/groupversion")
    public ModelMap updateAppAndSensorGroupVersion(@RequestBody SimpleSensor simpleSensor) {
        logger.info("updateAppAndSensorGroupVersion,"+JSON.toJSONString(simpleSensor));
        sensorService.updateAppAndSensorGroupVersion(simpleSensor.getSiecode());
        return HttpResponseUtil.ofmSuccessResponseMap(null);

    }


    private ModelMap struModifySensorResponse(ModifySensorResponse res, ModelMap modelMap) {
        if (res.getStatus().equals(STATUS0)) {
            return HttpResponseUtil.ofmSuccessResponseMap(null);
        } else if (res.getStatus().equals(STATUS1)) {
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.SENSOR_NOT_EXIST);
        } else if (res.getStatus().equals(STATUS3)) {
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.SENSOR_SIECODE_EXIST);
        } else if (res.getStatus().equals(STATUS6)) {
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.WRITEBACK_SENSOR_CANT_MODIFY_CONNECTOR_INFO_AND_TAG);
        } else if (res.getStatus().equals(STATUS2)) {
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.SENSOR_IMPORTED_APP_AND_CANT_MODIFY);
        } else if (res.getStatus().equals(STATUS4)) {
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.DA_CONFIG_TAG_NOT_EXIST);
        } else if (res.getStatus().equals(STATUS5)) {
            return HttpResponseUtil.ofmFailResponseMap(DaCoreErrorCodeEnum.TAG_IMPORTED_SENSOR);
        }
        return modelMap;
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

}
