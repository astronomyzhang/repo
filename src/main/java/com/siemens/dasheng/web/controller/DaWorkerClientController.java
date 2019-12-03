package com.siemens.dasheng.web.controller;

import com.siemens.dasheng.web.request.DaWorkerClientPointRequest;
import com.siemens.dasheng.web.request.DaWorkerClientPointSaveRequest;
import com.siemens.dasheng.web.service.DaWorkerClientService;
import com.siemens.ofmcommon.enums.ErrorCodeEnum;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.siemens.ofmcommon.constant.HttpResponseConstant.DETAIL_ERROR_MSG;

/**
 * da-worker client for web API
 *
 * @author ly
 * @date 2019/06/25
 */
@Api(value = "da-client", description = "data access client web api")
@Controller
@RequestMapping("/da-client/api")
public class DaWorkerClientController {

    private static final long MAX_TIMESTAMP = 2540600856000L;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DaWorkerClientService daWorkerClientService;

    @ApiOperation(value = "get real-time points", notes = "get real-time points")
    @PostMapping(value = "/real-time-points/v1")
    @ResponseBody
    @LogEventName(operateType = "DACLIENT", businessDescription = "get real-time points", webUrl = "/da-client/api/real-time-points/v1")
    public ModelMap getRealTimePoints(@RequestBody DaWorkerClientPointRequest request) {
        Map<String, Object> map = new HashMap<>(4);
        List<String> sensorIDList = request.getSensorIDList();
        if (sensorIDList == null || sensorIDList.size() == 0) {
            map.put(DETAIL_ERROR_MSG, "sensor list can't be null or empty.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }
        for (String sensorID : sensorIDList) {
            if (sensorID == null) {
                map.put(DETAIL_ERROR_MSG, "sensorID can't be null.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }
        }
        try {
            return HttpResponseUtil.ofmSuccessResponseMap(daWorkerClientService.getRealTimePoints(request));
        } catch (Exception e) {
            logger.error("invoke method getRealTimePoints() failed:{}", e.getMessage());
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
        }
    }

    @ApiOperation(value = "get historical points", notes = "get historical points")
    @PostMapping(value = "/historical-points/v1")
    @ResponseBody
    @LogEventName(operateType = "DACLIENT", businessDescription = "get historical points", webUrl = "/da-client/api/historical-points/v1")
    public ModelMap getHistoricalPoints(@RequestBody DaWorkerClientPointRequest request) {
        Map<String, Object> map = new HashMap<>(4);
        List<String> sensorIDList = request.getSensorIDList();
        if (sensorIDList == null || sensorIDList.size() == 0) {
            map.put(DETAIL_ERROR_MSG, "sensor list can't be null or empty.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }
        for (String sensorID : sensorIDList) {
            if (sensorID == null) {
                map.put(DETAIL_ERROR_MSG, "sensorID can't be null.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }
        }
        List<DaWorkerClientPointRequest.TimeRequest> timeMapperList = request.getTimeMapperList();
        if (timeMapperList == null || timeMapperList.size() == 0) {
            map.put(DETAIL_ERROR_MSG, "Time range list can't be null or empty.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }
        for (DaWorkerClientPointRequest.TimeRequest timeMapper : timeMapperList) {
            Long bgTime = timeMapper.getBgTime();
            Long endTime = timeMapper.getEndTime();
            if (bgTime == null || endTime == null || bgTime < 0 || endTime < 0) {
                map.put(DETAIL_ERROR_MSG, "BgTime or endTime can't be null or smaller than 0.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }
            // check bgTime and EndTime
            if (bgTime > endTime) {
                map.put(DETAIL_ERROR_MSG, "EndTime should bigger than bgTime.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (bgTime < 1 || bgTime > MAX_TIMESTAMP) {
                map.put(DETAIL_ERROR_MSG, "BgTime should be within the scope of [1,2540600856000].");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (endTime < 1 || endTime > MAX_TIMESTAMP) {
                map.put(DETAIL_ERROR_MSG, "EndTime should be within the scope of [1,2540600856000].");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

        }

        try {
            return HttpResponseUtil.ofmSuccessResponseMap(daWorkerClientService.getHistoricalPoints(request));
        } catch (Exception e) {
            logger.error("invoke method getHistoricalPoints() failed:{}", e.getMessage());
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
        }
    }

    @ApiOperation(value = "get historical points with interval", notes = "get historical points with interval")
    @PostMapping(value = "/historical-points-with-interval/v1")
    @ResponseBody
    @LogEventName(operateType = "DACLIENT", businessDescription = "get historical points with interval", webUrl = "/da-client/api/historical-points-with-interval/v1")
    public ModelMap getHistoricalPointsWithInterval(@RequestBody DaWorkerClientPointRequest request) {
        Map<String, Object> map = new HashMap<>(4);
        List<String> sensorIDList = request.getSensorIDList();
        if (sensorIDList == null || sensorIDList.size() == 0) {
            map.put(DETAIL_ERROR_MSG, "sensor list can't be null or empty.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }
        for (String sensorID : sensorIDList) {
            if (sensorID == null) {
                map.put(DETAIL_ERROR_MSG, "sensorID can't be null.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }
        }
        List<DaWorkerClientPointRequest.TimeRequest> timeMapperList = request.getTimeMapperList();
        Integer interval = request.getInterval();

        if (timeMapperList == null || timeMapperList.size() == 0) {
            map.put(DETAIL_ERROR_MSG, "Time range list can't be null or empty.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }

        if (interval == null || interval < 1) {
            map.put(DETAIL_ERROR_MSG, "Interval can't be null or smaller than 0.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }

        for (DaWorkerClientPointRequest.TimeRequest timeMapper : timeMapperList) {
            Long bgTime = timeMapper.getBgTime();
            Long endTime = timeMapper.getEndTime();
            if (bgTime == null || endTime == null || bgTime < 0 || endTime < 0) {
                map.put(DETAIL_ERROR_MSG, "BgTime or endTime can't be null or smaller than 0.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (bgTime > endTime) {
                map.put(DETAIL_ERROR_MSG, "EndTime should bigger than bgTime.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (bgTime < 1 || bgTime > MAX_TIMESTAMP) {
                map.put(DETAIL_ERROR_MSG, "BgTime should be within the scope of [1,2540600856000].");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (endTime < 1 || endTime > MAX_TIMESTAMP) {
                map.put(DETAIL_ERROR_MSG, "EndTime should be within the scope of [1,2540600856000].");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

        }
        try {
            return HttpResponseUtil.ofmSuccessResponseMap(daWorkerClientService.getHistoricalPointsWithInterval(request));
        } catch (Exception e) {
            logger.error("invoke method getHistoricalPointsWithInterval() failed:{}", e.getMessage());
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
        }
    }

    @ApiOperation(value = "insert historical points", notes = "insert historical points")
    @PostMapping(value = "/historical-points-save/v1")
    @ResponseBody
    @LogEventName(operateType = "DACLIENT", businessDescription = "insert historical points", webUrl = "/da-client/api/historical-points-save/v1")
    public ModelMap insertHistoricalPoints(@RequestBody DaWorkerClientPointSaveRequest request) {
        Map<String, Object> map = new HashMap<>(4);
        List<DaWorkerClientPointSaveRequest.Point> pointList = request.getPointList();
        if (pointList == null || pointList.size() == 0) {
            map.put(DETAIL_ERROR_MSG, "PointList can't be null or smaller than 0.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }

        for (DaWorkerClientPointSaveRequest.Point point : pointList) {
            String sensorID = point.getSensorID();
            Long timestamp = point.getTimestamp();
            Double value = point.getValue();

            if (sensorID == null) {
                map.put(DETAIL_ERROR_MSG, "sensorID can't be null.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (timestamp == null) {
                map.put(DETAIL_ERROR_MSG, "timestamp can't be null.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (timestamp < 1 || timestamp > MAX_TIMESTAMP) {
                map.put(DETAIL_ERROR_MSG, "timestamp should be within the scope of [1,2540600856000].");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }

            if (value == null) {
                map.put(DETAIL_ERROR_MSG, "value can't be null.");
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
            }
        }
        try {
            return HttpResponseUtil.ofmSuccessResponseMap(daWorkerClientService.insertHistoricalPoints(request));
        } catch (Exception e) {
            logger.error("invoke method insertHistoricalPoints() failed:{}", e.getMessage());
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
        }
    }

}
