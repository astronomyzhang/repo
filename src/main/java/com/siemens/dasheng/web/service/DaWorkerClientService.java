package com.siemens.dasheng.web.service;

import com.siemens.da.entity.DataItem;
import com.siemens.da.entity.HistoricalDataReturnSet;
import com.siemens.da.entity.TimeMapper;
import com.siemens.da.provider.TimeSeriesDbServiceProvider;
import com.siemens.da.provider.TimeSeriesDbServiceProviderFactory;
import com.siemens.dasheng.web.request.DaWorkerClientPointRequest;
import com.siemens.dasheng.web.request.DaWorkerClientPointSaveRequest;
import com.siemens.dasheng.web.response.DaWorkerClientPointResponse;
import com.siemens.dasheng.web.response.DaWorkerClientPointSaveResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * data access api service
 *
 * @author ly
 * @date 2019/06/25
 */
@Service
public class DaWorkerClientService {

    /**
     * am module global app id
     */
    private static final String APP_ID = "OFM1125281313222352891";

    /**
     * get da data access service provider
     */
    private final TimeSeriesDbServiceProvider provider = TimeSeriesDbServiceProviderFactory.newInstance().getTimeSeriesDbServiceProvider();


    /**
     * real-time data query interface
     *
     * @param request
     * @return
     * @throws Exception
     */
    public DaWorkerClientPointResponse getRealTimePoints(DaWorkerClientPointRequest request) throws Exception {
        List<String> sensorList = request.getSensorIDList();
        if (sensorList == null || sensorList.size() == 0) {
            return new DaWorkerClientPointResponse();
        }
        HistoricalDataReturnSet returnSet = provider.batchReadRealtimeData(APP_ID, sensorList);
        return transferHistoricalDataReturnSet2DaWorkerClientPointResponse(returnSet);

    }

    /**
     * historical data query interface
     *
     * @param request
     * @return
     * @throws Exception
     */
    public DaWorkerClientPointResponse getHistoricalPoints(DaWorkerClientPointRequest request) throws Exception {
        List<String> list = request.getSensorIDList();
        List<DaWorkerClientPointRequest.TimeRequest> timeMapperList = request.getTimeMapperList();
        if (list == null || timeMapperList == null || list.size() == 0 || timeMapperList.size() == 0) {
            return new DaWorkerClientPointResponse();
        }

        List<TimeMapper> timeMapperList2 = timeMapperList.stream().map(req -> {
            TimeMapper timeMapper = new TimeMapper();
            timeMapper.setBgTime(req.getBgTime());
            timeMapper.setEndTime(req.getEndTime());
            return timeMapper;
        }).collect(Collectors.toList());

        HistoricalDataReturnSet returnSet = provider.batchReadHistoricalData(APP_ID, list, timeMapperList2);
        return transferHistoricalDataReturnSet2DaWorkerClientPointResponse(returnSet);
    }

    /**
     * historical data with interval query interface
     *
     * @param request
     * @return
     * @throws Exception
     */
    public DaWorkerClientPointResponse getHistoricalPointsWithInterval(DaWorkerClientPointRequest request) throws Exception {
        List<String> list = request.getSensorIDList();
        List<DaWorkerClientPointRequest.TimeRequest> timeMapperList = request.getTimeMapperList();
        Integer interval = request.getInterval();
        if (list == null || timeMapperList == null || interval == null || list.size() == 0 || timeMapperList.size() == 0 || interval <= 0) {
            return new DaWorkerClientPointResponse();
        }

        List<TimeMapper> timeMapperList2 = timeMapperList.stream().map(req -> {
            TimeMapper timeMapper = new TimeMapper();
            timeMapper.setBgTime(req.getBgTime());
            timeMapper.setEndTime(req.getEndTime());
            return timeMapper;
        }).collect(Collectors.toList());

        HistoricalDataReturnSet returnSet = provider.batchReadHistoricalDataWithInterval(APP_ID, list, timeMapperList2, interval);
        return transferHistoricalDataReturnSet2DaWorkerClientPointResponse(returnSet);
    }

    /**
     * historical data insert interface
     *
     * @param request
     * @return
     * @throws Exception
     */
    public DaWorkerClientPointSaveResponse insertHistoricalPoints(DaWorkerClientPointSaveRequest request) throws Exception {
        List<DaWorkerClientPointSaveRequest.Point> pointList = request.getPointList();
        List<DataItem> dataItemList = pointList.stream().map(point -> {
            DataItem dataItem = new DataItem();
            dataItem.setSensorID(point.getSensorID());
            dataItem.setValue(String.valueOf(point.getValue()));
            dataItem.setTimestamp(point.getTimestamp());
            return dataItem;
        }).collect(Collectors.toList());
        DaWorkerClientPointSaveResponse response = new DaWorkerClientPointSaveResponse();
        long count = provider.batchInsertPointList(APP_ID, dataItemList);
        response.setCount(count);
        return response;
    }

    /**
     * 将查询结果转换为传输对象
     *
     * @param returnSet
     * @return
     */
    private DaWorkerClientPointResponse transferHistoricalDataReturnSet2DaWorkerClientPointResponse(HistoricalDataReturnSet returnSet) {
        Map<String, List<DataItem>> normalMap = returnSet.getNormalDataMap();
        List<String> notExistList = returnSet.getNotExsitSensorList();
        List<String> errorIntervalList = returnSet.getErrorSensorList();
        Map<String, List<DaWorkerClientPointResponse.DaWorkerClientPoint>> normalMap2 = normalMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().stream().map(this::transferDataItem2DaWorkerClientPoint).collect(Collectors.toList())
                , (oldValue, newValue) -> newValue));
        DaWorkerClientPointResponse response = new DaWorkerClientPointResponse();
        response.setNormalPointList(normalMap2);
        response.setNotExistSensorIDList(notExistList);
        response.setErrorIntervalSensorIDList(errorIntervalList);
        return response;
    }

    /**
     * 将DataItem转换为传输的点对象
     *
     * @param dataItem
     * @return
     */
    private DaWorkerClientPointResponse.DaWorkerClientPoint transferDataItem2DaWorkerClientPoint(DataItem dataItem) {
        DaWorkerClientPointResponse.DaWorkerClientPoint point = new DaWorkerClientPointResponse.DaWorkerClientPoint();
        point.setSensorID(dataItem.getSensorID());
        point.setTimestamp(dataItem.getTimestamp());
        point.setValue(dataItem.getValue());
        point.setQuality(dataItem.getQuality());
        return point;
    }
}
