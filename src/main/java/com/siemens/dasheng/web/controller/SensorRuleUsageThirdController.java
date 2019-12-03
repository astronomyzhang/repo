package com.siemens.dasheng.web.controller;

        import com.alibaba.fastjson.JSON;
        import com.siemens.dasheng.web.exception.BusinessException;
        import com.siemens.dasheng.web.request.ObjectUsageRequest;
        import com.siemens.dasheng.web.service.AppThirdService;
        import com.siemens.ofmcommon.log.anotation.LogEventName;
        import com.siemens.ofmcommon.utils.HttpResponseUtil;
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
        import static com.siemens.ofmcommon.constant.HttpResponseConstant.SUCCESS;
        import static com.siemens.ofmcommon.enums.ErrorCodeEnum.SERVER_UNKNOWN_ERROR;

/**
 * SensorRuleUsageThirdController
 * @author xuxin
 * Created by xuxin on 8/5/2019.
 */
@Api(value = "sensor rule usage third service", description = "sensor rule usage third service")
@Controller
@RequestMapping("/third-party/sensor_rule")
public class SensorRuleUsageThirdController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppThirdService appThirdService;

    @ApiOperation(value = "sensor rule use", notes = "sensor group use")
    @PostMapping(value = "/sensor_rule_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SENSORRULEUSAGE", businessDescription = "add sensor rule use", webUrl = "/third-party/sensor_rule/sensor_rule_lock/v1")
    public ModelMap sensorRuleLock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("sensorRuleLock,"+ JSON.toJSONString(objectUsageRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                return HttpResponseUtil.ofmFailResponseMap(SERVER_UNKNOWN_ERROR);
            }
            modelMap = appThirdService.sensorRuleUsage(objectUsageRequest, modelMap, true);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("sensor_group_use errors:{}", be.getMessage(),be);
            be.printStackTrace();
            return HttpResponseUtil.ofmFailResponseMap(SERVER_UNKNOWN_ERROR);
        }   catch (Exception e) {
            logger.error("sensor group use errors:{}", e.getMessage(),e);
            e.printStackTrace();
            return HttpResponseUtil.ofmFailResponseMap(SERVER_UNKNOWN_ERROR);
        }
    }

    @ApiOperation(value = "sensor rule use", notes = "sensor group use")
    @DeleteMapping(value = "/sensor_rule_lock/v1")
    @ResponseBody
    @LogEventName(operateType = "SENSORRULEUSAGE", businessDescription = "delete sensor rule use", webUrl = "/third-party/sensor_rule/sensor_rule_lock/v1")
    public ModelMap sensorRuleUnlock(@RequestBody ObjectUsageRequest objectUsageRequest, HttpServletRequest httpServletRequest) {
        logger.info("sensorRuleUnlock,"+ JSON.toJSONString(objectUsageRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS, Boolean.TRUE);
        try {
            if (objectUsageRequest == null) {
                return HttpResponseUtil.ofmFailResponseMap(SERVER_UNKNOWN_ERROR);
            }
            modelMap = appThirdService.sensorRuleUsage(objectUsageRequest, modelMap, false);
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
