package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.exception.BusinessException;
import com.siemens.dasheng.web.request.ObjectUsageRequest;
import com.siemens.dasheng.web.service.AppThirdService;
import com.siemens.ofmcommon.enums.ErrorCodeEnum;
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
import static com.siemens.ofmcommon.constant.HttpResponseConstant.*;
import static com.siemens.ofmcommon.enums.ErrorCodeEnum.SERVER_UNKNOWN_ERROR;

/**
 * SensorUsageThirdController
 *
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
@Api(value = "sensor usage third service", description = "sensor usage third service")
@Controller
@RequestMapping("/third-party/sensor")
public class SensorUsageThirdController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppThirdService appThirdService;

    @ApiOperation(value = "sensor use", notes = "sensor use")
    @PostMapping(value = "/sensor_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SensorLock", businessDescription = "lock sensor", webUrl = "/third-party/sensor/sensor_lock/v1")
    public ModelMap sensorLock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("sensorLock,"+ JSON.toJSONString(objectUsageRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                modelMap.put(MSG, OPERATE_FAIL);
                modelMap.put(CODE, ErrorCodeEnum.PARAMETER_VALIDATE_ERROR.getCode());
                modelMap.put(SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap = appThirdService.sensorUsage(objectUsageRequest, modelMap, true);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("sensor_use errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        } catch (Exception e) {
            logger.error("sensor_use errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "sensor release", notes = "sensor release")
    @DeleteMapping(value = "/sensor_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SensorLock", businessDescription = "release sensor", webUrl = "/third-party/sensor/sensor_lock/v1")
    public ModelMap sensorUnlock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("sensorUnlock,"+ JSON.toJSONString(objectUsageRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                modelMap.put(MSG, OPERATE_FAIL);
                modelMap.put(CODE, SERVER_UNKNOWN_ERROR);
                modelMap.put(SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap = appThirdService.sensorUsage(objectUsageRequest, modelMap, false);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("sensor_release errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        } catch (Exception e) {
            logger.error("sensor_release errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "sensor batch use", notes = "sensor batch use")
    @PostMapping(value = "/sensor_batch_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SensorLock", businessDescription = "batch lock sensor", webUrl = "/third-party/sensor/sensor_batch_lock/v1")
    public ModelMap sensorBatchLock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("Batch Sensor Lock");
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                modelMap.put(MSG, OPERATE_FAIL);
                modelMap.put(CODE, ErrorCodeEnum.PARAMETER_VALIDATE_ERROR.getCode());
                modelMap.put(SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap = appThirdService.multiSensorUsage(objectUsageRequest, modelMap, true);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("batch sensor_use errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        } catch (Exception e) {
            logger.error("batch errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "sensor batch release", notes = "sensor batch release")
    @DeleteMapping(value = "/sensor_batch_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SensorLock", businessDescription = "batch release sensor", webUrl = "/third-party/sensor/sensor_batch_lock/v1")
    public ModelMap sensorUnBatchlock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                modelMap.put(MSG, OPERATE_FAIL);
                modelMap.put(CODE, ErrorCodeEnum.PARAMETER_VALIDATE_ERROR.getCode());
                modelMap.put(SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap = appThirdService.multiSensorUsage(objectUsageRequest, modelMap, false);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("batch sensor_release errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        } catch (Exception e) {
            logger.error("batch sensor_release errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }
}
