package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.exception.BusinessException;
import com.siemens.dasheng.web.request.ObjectUsageRequest;
import com.siemens.dasheng.web.service.AppThirdService;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.OPERATE_FAIL;
import static com.siemens.ofmcommon.constant.HttpResponseConstant.CODE;
import static com.siemens.ofmcommon.constant.HttpResponseConstant.MSG;
import static com.siemens.ofmcommon.constant.HttpResponseConstant.SUCCESS;
import static com.siemens.ofmcommon.enums.ErrorCodeEnum.SERVER_UNKNOWN_ERROR;

/**
 * SensorGroupUsageThirdController
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
@Api(value = "sensor group usage third service", description = "sensor group usage third service")
@Controller
@RequestMapping("/third-party/sensor/group")
public class SensorGroupUsageThirdController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppThirdService appThirdService;

    @ApiOperation(value = "sensor group use", notes = "sensor group use")
    @PostMapping(value = "/sensor_group_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SENSORGROUPUSAGE", businessDescription = "add sensor group use", webUrl = "/third-party/sensor/group/sensor_group_lock/v1")
    public ModelMap sensorGroupLock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("sensorGroupLock,"+ JSON.toJSONString(objectUsageRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                modelMap.put(MSG, OPERATE_FAIL);
                modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
                modelMap.put(SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap = appThirdService.sensorGroupUsage(objectUsageRequest, modelMap, true);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("sensor_group_use errors:{}", be.getMessage());
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }   catch (Exception e) {
            logger.error("sensor group use errors:{}", e.getMessage());
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "sensor group use", notes = "sensor group use")
    @DeleteMapping(value = "/sensor_group_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SENSORGROUPUSAGE", businessDescription = "delete sensor group use", webUrl = "/third-party/sensor/group/sensor_group_lock/v1")
    public ModelMap sensorGroupUnlock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("sensorGroupUnlock,"+ JSON.toJSONString(objectUsageRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                modelMap.put(MSG, OPERATE_FAIL);
                modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
                modelMap.put(SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap = appThirdService.sensorGroupUsage(objectUsageRequest, modelMap, false);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("sensor_group_release errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }   catch (Exception e) {
            logger.error("sensor_group_release errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }
}
