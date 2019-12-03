package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.model.dto.ValidSensorDTO;
import com.siemens.dasheng.web.model.dto.ValidSensorDataList;
import com.siemens.dasheng.web.model.dto.ValidSensorInput;
import com.siemens.dasheng.web.service.SensorValidationService;
import com.siemens.ofmcommon.enums.ErrorCodeEnum;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siemens.ofmcommon.constant.HttpResponseConstant.DETAIL_ERROR_MSG;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/7/3.
 */
@Controller
@RequestMapping("/sensor_validation")
public class SensorValidationController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SensorValidationService sensorValidationService;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    private static final String OFFLINE_COMPUTING_DEFAULT_VERSION = "1.0.1";
    private static final String OFFLINE_COMPUTING_VERSION_001 = "1.0.1";
    private static final double TEN_MILLIOM = 10000000;
    private static final double TEN_THOUSANDTH = 0.0001;
    private static final double TEN_THOUSAND = 10000;
    private static final long MAX_TIMESTAMP = 2540600856000L;
    private static final long ONE_SECOND = 1000L;
    private static final long MAX_COMPUTING_NUM = 100000;
    private static final long ONE_MONTH = 2592000000L;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0000");


    @ApiOperation(value = "online computing of sensor validation", notes = " Strategy has been configured in DA, and historical data can be acquired by DA in historian DB.  " +
            "In this situation, the input can only be sensor code, sensor strategy as well as the time range of historical data.  ")
    @PostMapping("/online_computing/v1")
    @ResponseBody
    @LogEventName(operateType = "SENSORVALIDATION", businessDescription = "online computing of sensor validation", webUrl = "/sensor_validation/online_computing/v1")
    public ModelMap onlineComputing(@RequestBody ValidSensorInput data, HttpServletRequest request) {
        logger.info("onlineComputing,"+ JSON.toJSONString(data));

        String apiVersion = request.getHeader("api-version");

        if (null == apiVersion) {
            apiVersion = OFFLINE_COMPUTING_DEFAULT_VERSION;
        }

        if (null != apiVersion && apiVersion.equals(OFFLINE_COMPUTING_VERSION_001)) {

            if (data.getBgTime() == 0 || data.getEndTime() == 0 || data.getTimeInterval() == 0) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "BgTime,endTime,timeInterval can't be null or 0.");
            }

            if (data.getTimeInterval() < 1) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "TimeInterval should be bigger than 0.");
            }

            // check validRuleId
            if (StringUtils.isBlank(data.getValidRuleId())) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "ValidRuleId can't be null or empty.");
            } else {
                String validRuleId = daConfigSensorMapper.selectValidSensorRule(data.getValidRuleId());
                if (null == validRuleId) {
                    return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "ValidRuleId doesn't exsist.");
                }
            }


            // check bgTime and EndTime
            if (data.getBgTime() > data.getEndTime()) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "EndTime should bigger than bgTime.");
            } else {

                if (data.getBgTime() < 1 || data.getBgTime() > MAX_TIMESTAMP) {
                    return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "BgTime should be within the scope of [1,2540600856000].");
                }

                if (data.getEndTime() < 1 || data.getEndTime() > MAX_TIMESTAMP) {
                    return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "EndTime should be within the scope of [1,2540600856000].");
                }

                if (data.getEndTime() - data.getBgTime() > ONE_MONTH) {
                    return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Time scope should be within 1 month.");
                }

                if (data.getEndTime() - data.getBgTime() < data.getTimeInterval() * ONE_SECOND) {
                    return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Time scope should bigger than timeInterval.");
                }

                if ((data.getEndTime() - data.getBgTime()) / (data.getTimeInterval() * ONE_SECOND) > MAX_COMPUTING_NUM) {
                    return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Return points should less than " + MAX_COMPUTING_NUM + ".");
                }

            }


            return HttpResponseUtil.ofmSuccessResponseMap(sensorValidationService.sensorOnlineValidComputing(data));
        }

        return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.API_VERSION_ERROR);

    }


    @ApiOperation(value = "Offline computing of sensor validation", notes = "As \"Sensor Validation Engine\": DA get historical data and detailed " +
            "strategy configuration data, and return the validated data as result.")
    @PostMapping("/offline_computing/v1")
    @ResponseBody
    @LogEventName(operateType = "SENSORVALIDATION", businessDescription = "Offline computing of sensor validation", webUrl = "/sensor_validation/offline_computing/v1")
    public ModelMap offlineComputing(@RequestBody ValidSensorDTO data, HttpServletRequest request) {
        logger.info("offlineComputing,"+ JSON.toJSONString(data));

        String apiVersion = request.getHeader("api-version");

        if (null == apiVersion) {
            apiVersion = OFFLINE_COMPUTING_DEFAULT_VERSION;
        }

        if (null != apiVersion && apiVersion.equals(OFFLINE_COMPUTING_VERSION_001)) {

            ModelMap map = offlineComputingParameterErrorChecker(data);
            if (map != null) {
                return map;
            }

            return HttpResponseUtil.ofmSuccessResponseMap(sensorValidationService.sensorOfflineValidComputing(data));
        }

        return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.API_VERSION_ERROR);

    }

    /**
     * Error checker
     *
     * @param data
     * @return
     */
    private ModelMap offlineComputingParameterErrorChecker(@RequestBody ValidSensorDTO data) {
        Map map = new HashMap<>(2);

        if (null == data) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Input data is null.");
        }

        // check valid parameter
        if (null == data.getLockedactive()) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Lockedactive parameter can't be null.");
        } else {
            if (lockDetailCheck(data, map)) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, (String) map.get(DETAIL_ERROR_MSG));
            }
        }

        if (null == data.getErraticactive()) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Erraticactive parameter can't be null.");
        } else {
            if (erraticDetailCheck(data, map)) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, (String) map.get(DETAIL_ERROR_MSG));
            }
        }

        if (null == data.getFailureupperlimitactive()) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Failureupperlimitactive parameter can't be null.");
        } else {
            if (failureUpperLimitDetailCheck(data, map)) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, (String) map.get(DETAIL_ERROR_MSG));
            }
        }

        if (null == data.getFailurelowerlimitactive()) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Failurelowerlimitactive parameter can't be null.");
        } else {
            if (failureLowerLimitDetailCheck(data, map)) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, (String) map.get(DETAIL_ERROR_MSG));
            }
        }

        if (null == data.getGradactive()) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Gradactive parameter can't be null.");
        } else {
            if (gradDetailCheck(data, map)) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, (String) map.get(DETAIL_ERROR_MSG));
            }
        }

        // check input sensor list
        if (checkInputSensorList(data, map)) {
            return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, (String) map.get(DETAIL_ERROR_MSG));
        }

        if (data.getFailureupperlimitactive().shortValue() == 1 && data.getFailurelowerlimitactive().shortValue() == 1) {
            if (data.getFailurelowerlimit().doubleValue() > data.getFailureupperlimit().doubleValue()) {
                return HttpResponseUtil.ofmThreePartyFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, "Failurelowerlimit shouldn't bigger than failureupperlimit when failureupperlimitactive and failurelowerlimitactive are all in active status.");
            }
        }

        return null;
    }

    private boolean gradDetailCheck(@RequestBody ValidSensorDTO data, Map map) {
        if (data.getGradactive().shortValue() == 1) {
            if (null == data.getFailureabsgradthresh() || null == data.getFailurerelgradthresh()) {
                map.put(DETAIL_ERROR_MSG, "Failureabsgradthresh and failurerelgradthresh parameter can't be null when failurelowerlimitactive is active.");
                return true;
            } else {
                if (data.getFailureabsgradthresh().doubleValue() > TEN_MILLIOM || data.getFailureabsgradthresh().doubleValue() < TEN_THOUSANDTH) {
                    map.put(DETAIL_ERROR_MSG, "Failureabsgradthresh should be within the scope of [0.0001,10000000].");
                    return true;
                } else {
                    data.setFailureabsgradthresh(new BigDecimal(DECIMAL_FORMAT.format(data.getFailureabsgradthresh().doubleValue())));
                }
                if (data.getFailurerelgradthresh().doubleValue() > TEN_MILLIOM || data.getFailurerelgradthresh().doubleValue() < TEN_THOUSANDTH) {
                    map.put(DETAIL_ERROR_MSG, "Failurerelgradthresh should be within the scope of [0.0001,10000000].");
                    return true;
                } else {
                    data.setFailurerelgradthresh(new BigDecimal(DECIMAL_FORMAT.format(data.getFailurerelgradthresh().doubleValue())));
                }
            }
        }
        return false;
    }

    private boolean failureLowerLimitDetailCheck(@RequestBody ValidSensorDTO data, Map map) {
        if (data.getFailurelowerlimitactive().shortValue() == 1) {
            if (null == data.getFailurelowerlimit()) {
                map.put(DETAIL_ERROR_MSG, "Failurelowerlimit parameter can't be null when failurelowerlimitactive is active.");
                return true;
            } else {

                if (data.getFailurelowerlimit().doubleValue() > TEN_MILLIOM || data.getFailurelowerlimit().doubleValue() < -TEN_MILLIOM) {
                    map.put(DETAIL_ERROR_MSG, "Failurelowerlimit should be within the scope of [-10000000,10000000].");
                    return true;
                } else {
                    data.setFailurelowerlimit(new BigDecimal(DECIMAL_FORMAT.format(data.getFailurelowerlimit().doubleValue())));
                }

            }
        }
        return false;
    }

    private boolean failureUpperLimitDetailCheck(@RequestBody ValidSensorDTO data, Map map) {
        if (data.getFailureupperlimitactive().shortValue() == 1) {
            if (null == data.getFailureupperlimit()) {
                map.put(DETAIL_ERROR_MSG, "Failureupperlimit parameter can't be null when failureupperlimitactive is active.");
                return true;
            } else {

                if (data.getFailureupperlimit().doubleValue() > TEN_MILLIOM || data.getFailureupperlimit().doubleValue() < -TEN_MILLIOM) {
                    map.put(DETAIL_ERROR_MSG, "Failureupperlimit should be within the scope of [-10000000,10000000].");
                    return true;
                } else {
                    data.setFailureupperlimit(new BigDecimal(DECIMAL_FORMAT.format(data.getFailureupperlimit().doubleValue())));
                }

            }
        }
        return false;
    }

    private boolean erraticDetailCheck(@RequestBody ValidSensorDTO data, Map map) {
        if (data.getErraticactive().shortValue() == 1) {
            if (null == data.getErraticdbwidth() || null == data.getErraticrdbbreaks() || null == data.getErratictime()) {
                map.put(DETAIL_ERROR_MSG, "Erraticdbwidth, erraticrdbbreaks and erratictime parameter can't be null when erratic is active.");
                return true;
            } else {
                if (data.getErratictime() > TEN_THOUSAND || data.getErratictime() < 1) {
                    map.put(DETAIL_ERROR_MSG, "Erratictime should be within the scope of [1,10000].");
                    return true;
                }
                if (data.getErraticrdbbreaks() > TEN_THOUSAND || data.getErraticrdbbreaks() < 1) {
                    map.put(DETAIL_ERROR_MSG, "Erraticrdbbreaks should be within the scope of [1,10000].");
                    return true;
                }
                if (data.getErraticdbwidth().doubleValue() > TEN_THOUSAND || data.getErraticdbwidth().doubleValue() < TEN_THOUSANDTH) {
                    map.put(DETAIL_ERROR_MSG, "Erraticdbwidth should be within the scope of [0.0001,10000].");
                    return true;
                } else {
                    data.setErraticdbwidth(new BigDecimal(DECIMAL_FORMAT.format(data.getErraticdbwidth().doubleValue())));
                }
            }
        }
        return false;
    }

    private boolean lockDetailCheck(@RequestBody ValidSensorDTO data, Map map) {
        if (data.getLockedactive().shortValue() == 1) {
            if (null == data.getLockeddbwidth() || null == data.getLocktime()) {
                map.put(DETAIL_ERROR_MSG, "Lockeddbwidth and locktime parameter can't be null when locked is active.");
                return true;
            } else {

                if (data.getLocktime() > TEN_THOUSAND || data.getLocktime() < 1) {
                    map.put(DETAIL_ERROR_MSG, "Locktime should be within the scope of [1,10000].");
                    return true;
                }

                if (data.getLockeddbwidth().doubleValue() > TEN_THOUSAND || data.getLockeddbwidth().doubleValue() < TEN_THOUSANDTH) {
                    map.put(DETAIL_ERROR_MSG, "Lockeddbwidth should be within the scope of [0.0001,10000].");
                    return true;
                } else {
                    data.setLockeddbwidth(new BigDecimal(DECIMAL_FORMAT.format(data.getLockeddbwidth().doubleValue())));
                }
            }
        }
        return false;
    }

    private boolean checkInputSensorList(@RequestBody ValidSensorDTO data, Map map) {
        if (null == data.getInputSensorDataList()) {
            map.put(DETAIL_ERROR_MSG, "InputSensorDataList parameter can't be null.");
            return true;
        } else {

            List<ValidSensorDataList> validSensorDataListList = data.getInputSensorDataList();
            if (validSensorDataListList.size() == 0) {
                map.put(DETAIL_ERROR_MSG, "At least one sensor should be in the InputSensorDataList");
                return true;
            } else {
                for (ValidSensorDataList sensorDataList : validSensorDataListList) {
                    if (null != sensorDataList) {
                        if (StringUtils.isBlank(sensorDataList.getSensorCode())) {
                            map.put(DETAIL_ERROR_MSG, "SensorCode in InputSensorDataList can't be null or blank");
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

}