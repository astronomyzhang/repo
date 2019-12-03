package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.DataExcelEnum;
import com.siemens.dasheng.web.enums.DataExcelErrorCodeEnum;
import com.siemens.dasheng.web.model.DaConfigSensor;
import com.siemens.dasheng.web.request.DaWorkerClientPointRequest;
import com.siemens.dasheng.web.response.DaWorkerClientPointResponse;
import com.siemens.dasheng.web.service.DaWorkerClientService;
import com.siemens.dasheng.web.service.DataSensorService;
import com.siemens.ofmcommon.enums.ErrorCodeEnum;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.siemens.ofmcommon.constant.HttpResponseConstant.DETAIL_ERROR_MSG;

/**
 * @author ly
 * @date 2019/07/09
 */

@Api(value = "da-data", description = "data access and export")
@Controller
@RequestMapping("/da-data/data")
public class DaDataExportController {

    private static final long MAX_TIMESTAMP = 2540600856000L;

    /**
     * 质量码良好
     */
    private static final String QUALITY_NORMAL = "Normal";

    /**
     * 质量码不好
     */
    private static final String QUALITY_ABNORMAL = "Abnormal";

    /**
     * 最大excel导出行数
     */
    private static final int MAX_ROW_NUM = 300_000;

    /**
     * 最大excel导出行数报告消息
     */
    private static final String MAX_ROW_NUM_MSG = "data export can not be bigger than 300000";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DaWorkerClientService daWorkerClientService;

    @Autowired
    DataSensorService dataSensorService;

    @ApiOperation(value = "get available sensor list", notes = "get available sensor list")
    @GetMapping(value = "/available-sensors/v1")
    @ResponseBody
    @LogEventName(operateType = "DADATA", businessDescription = "get available sensor list", webUrl = "/da-data/data/available-sensors/v1")
    public ModelMap getAvailableSensorList(Long appId) {
        logger.info("getAvailableSensorList,appId : {}",appId);
        Map<String, Object> map = new HashMap<>(2);
        if (appId == null) {
            map.put(DETAIL_ERROR_MSG, "app ID can't be null.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.PARAMETER_VALIDATE_ERROR, map);
        }
        try {
            return HttpResponseUtil.ofmSuccessResponseMap(dataSensorService.selectAvailableSensorListByAppId(appId));
        } catch (Exception e) {
            logger.error("invoke getAvailableSensorList method error : {}", e.getMessage(),e);
            map.put(DETAIL_ERROR_MSG, "query data failed cause internal exception.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
        }
    }

    @ApiOperation(value = "get historical points", notes = "get historical points")
    @PostMapping(value = "/historical-points/v1")
    @ResponseBody
    @LogEventName(operateType = "DADATA", businessDescription = "get historical points", webUrl = "/da-data/data/historical-points/v1")
    public ModelMap getHistoricalPoints(@RequestBody DaWorkerClientPointRequest request) {
        logger.info("getHistoricalPoints,"+JSON.toJSONString(request));
        ModelMap result = validateCommonRequest(request);
        if (result != null) {
            return result;
        }
        try {
            return HttpResponseUtil.ofmSuccessResponseMap(queryHistoricalData(request));
        } catch (Exception e) {
            logger.error("invoke method getHistoricalPoints failed:{}", e.getMessage(),e);
            Map<String, Object> map = new HashMap<>(2);
            map.put(DETAIL_ERROR_MSG, "query data failed cause internal exception.");
            return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
        }
    }

    @ApiOperation(value = "get historical points excel", notes = "get historical points excel")
    @PostMapping(value = "/historical-points-excel/v1")
    @ResponseBody
    @LogEventName(operateType = "DADATA", businessDescription = "get historical points excel", webUrl = "/da-data/data/historical-points-excel/v1")
    public ModelMap getHistoricalPointsExcel(@RequestBody DaWorkerClientPointRequestPlus request) {
        logger.info("getHistoricalPointsExcel,"+JSON.toJSONString(request));
        ModelMap result = validateCommonRequest(request);
        if (result != null) {
            return result;
        }
        try {
            HSSFWorkbook wb;
            wb = createExcel2(request);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            wb.write(outputStream);
            return HttpResponseUtil.ofmSuccessResponseMap(outputStream.toByteArray());
        } catch (Exception e) {
            String msg = e.getMessage();
            logger.error("invoke method getHistoricalPointsExcel failed:{}", msg,e);
            Map<String, Object> map = new HashMap<>(2);
            map.put(DETAIL_ERROR_MSG, e.getMessage());
            if (msg.contains(MAX_ROW_NUM_MSG)) {
                return HttpResponseUtil.ofmFailResponseMap(DataExcelErrorCodeEnum.DATA_TOO_BIG_ERROR, map);
            } else {
                return HttpResponseUtil.ofmFailResponseMap(ErrorCodeEnum.SERVER_UNKNOWN_ERROR, map);
            }
        }
    }

    @ApiOperation(value = "get historical points excel", notes = "get historical points excel")
    @PostMapping(value = "/historical-points-excel/v2")
    @LogEventName(operateType = "DADATA", businessDescription = "get historical points excel", webUrl = "/da-data/data/historical-points-excel/v2")
    public void getHistoricalPointsExcel(@RequestBody DaWorkerClientPointRequestPlus request, HttpServletResponse response) {
        logger.info("getHistoricalPointsExcel,"+JSON.toJSONString(request));
        HSSFWorkbook wb;
        try {
            wb = createExcel(request);
        } catch (Exception e) {
            logger.error("invoke method getRealTimePoints failed:{}", e.getMessage(),e);
            return;
        }

        //输出Excel文件
        OutputStream output = null;
        try {
            output = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=details.xls");
            response.setContentType("application/msexcel");
            wb.write(output);
        } catch (IOException e) {
            logger.error("write response failed : {}", e.getMessage(),e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.error("close output stream failed : {}", e.getMessage(),e);
                }
            }
        }

    }

    /**
     * 创建excel内容
     *
     * @param request
     * @return
     */
    private HSSFWorkbook createExcel2(DaWorkerClientPointRequestPlus request) throws Exception {
        List<SensorResponse> list = queryHistoricalData(request);
        if (list != null && list.size() >= MAX_ROW_NUM) {
            throw new Exception(MAX_ROW_NUM_MSG);
        }
        // 国际化标志
        Boolean international = request.getInternational() == null ? Boolean.TRUE : request.getInternational();
        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wb = new HSSFWorkbook();
        // 如果数据为空，则只返回空的excel表格
        if (list == null || list.size() == 0) {
            int sheetNum = 0;
            //建立新的sheet对象（excel的表单）
            HSSFSheet sheet = wb.createSheet(international ? DataExcelEnum.SHEET_NAME.getEcode() + sheetNum : DataExcelEnum.SHEET_NAME.getCcode() + sheetNum);
            //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
            HSSFRow row1 = sheet.createRow(0);
            //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
            HSSFCell cell = row1.createCell(0);
            //设置单元格内容
            cell.setCellValue(international ? DataExcelEnum.TABLE_NAME.getEcode() : DataExcelEnum.TABLE_NAME.getCcode());
            //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            //在sheet里创建第二行
            HSSFRow row2 = sheet.createRow(1);
            //创建单元格并设置单元格内容
            row2.createCell(0).setCellValue(international ? DataExcelEnum.SENSOR_CODE.getEcode() : DataExcelEnum.SENSOR_CODE.getCcode());
            row2.createCell(1).setCellValue(international ? DataExcelEnum.SENSOR_TAG.getEcode() : DataExcelEnum.SENSOR_TAG.getCcode());
            row2.createCell(2).setCellValue(international ? DataExcelEnum.SENSOR_TIME.getEcode() : DataExcelEnum.SENSOR_TIME.getCcode());
            row2.createCell(3).setCellValue(international ? DataExcelEnum.SENSOR_VALUE.getEcode() : DataExcelEnum.SENSOR_VALUE.getCcode());
            row2.createCell(4).setCellValue(international ? DataExcelEnum.SENSOR_QUALITY.getEcode() : DataExcelEnum.SENSOR_QUALITY.getCcode());
            return wb;
        }
        // 数据不为空（null或数据集合大小不为0）时，为数据绘制excel
        // 设置每个sheet最大的行数不超过60000
        int rowNumMax = 60_000;
        //格式化输出时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        // excel行数计数阈值
        int thresholdNum = 0;
        // sheet计数值
        int sheetNum = 0;
        // sheet引用
        HSSFSheet sheet = null;
        // 每个sheet中行计数
        int rowNum = 0;
        for (SensorResponse sensorResponse : list) {
            if (thresholdNum % rowNumMax == 0) {
                //建立新的sheet对象（excel的表单）
                sheet = wb.createSheet(international ? DataExcelEnum.SHEET_NAME.getEcode() + sheetNum : DataExcelEnum.SHEET_NAME.getCcode() + sheetNum);
                //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
                HSSFRow row1 = sheet.createRow(0);
                //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
                HSSFCell cell = row1.createCell(0);
                //设置单元格内容
                cell.setCellValue(international ? DataExcelEnum.TABLE_NAME.getEcode() : DataExcelEnum.TABLE_NAME.getCcode());
                //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
                //在sheet里创建第二行
                HSSFRow row2 = sheet.createRow(1);
                //创建单元格并设置单元格内容
                row2.createCell(0).setCellValue(international ? DataExcelEnum.SENSOR_CODE.getEcode() : DataExcelEnum.SENSOR_CODE.getCcode());
                row2.createCell(1).setCellValue(international ? DataExcelEnum.SENSOR_TAG.getEcode() : DataExcelEnum.SENSOR_TAG.getCcode());
                row2.createCell(2).setCellValue(international ? DataExcelEnum.SENSOR_TIME.getEcode() : DataExcelEnum.SENSOR_TIME.getCcode());
                row2.createCell(3).setCellValue(international ? DataExcelEnum.SENSOR_VALUE.getEcode() : DataExcelEnum.SENSOR_VALUE.getCcode());
                row2.createCell(4).setCellValue(international ? DataExcelEnum.SENSOR_QUALITY.getEcode() : DataExcelEnum.SENSOR_QUALITY.getCcode());
                // sheet计数加1
                sheetNum++;
                // 重置每个sheet中的数据行为2
                rowNum = 2;
            }
            String sensorID = sensorResponse.getSensorID();
            String tag = sensorResponse.getTag();
            String value = sensorResponse.getValue();
            Long timestamp = sensorResponse.getTimestamp();
            Boolean quality = sensorResponse.getQuality();
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sensorID == null ? "" : sensorID);
            row.createCell(1).setCellValue(tag == null ? "" : tag);
            row.createCell(2).setCellValue(timestamp == null ? "" : df.format(timestamp));
            row.createCell(3).setCellValue(value == null ? "" : value);
            row.createCell(4).setCellValue(Boolean.TRUE.equals(quality) ? QUALITY_NORMAL : QUALITY_ABNORMAL);
            thresholdNum++;
        }
        return wb;
    }


    /**
     * 创建excel内容
     *
     * @param request
     * @return
     */
    private HSSFWorkbook createExcel(DaWorkerClientPointRequestPlus request) throws Exception {
        List<SensorResponse> list = queryHistoricalData(request);
        // 国际化标志
        Boolean international = request.getInternational() == null ? Boolean.TRUE : request.getInternational();

        //创建HSSFWorkbook对象(excel的文档对象)
        HSSFWorkbook wb = new HSSFWorkbook();
        //建立新的sheet对象（excel的表单）
        HSSFSheet sheet = wb.createSheet(international ? DataExcelEnum.SHEET_NAME.getEcode() : DataExcelEnum.SHEET_NAME.getCcode());
        //在sheet里创建第一行，参数为行索引(excel的行)，可以是0～65535之间的任何一个
        HSSFRow row1 = sheet.createRow(0);
        //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        HSSFCell cell = row1.createCell(0);
        //设置单元格内容
        cell.setCellValue(international ? DataExcelEnum.TABLE_NAME.getEcode() : DataExcelEnum.TABLE_NAME.getCcode());
        //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
        //在sheet里创建第二行
        HSSFRow row2 = sheet.createRow(1);
        //创建单元格并设置单元格内容
        row2.createCell(0).setCellValue(international ? DataExcelEnum.SENSOR_CODE.getEcode() : DataExcelEnum.SENSOR_CODE.getCcode());
        row2.createCell(1).setCellValue(international ? DataExcelEnum.SENSOR_TAG.getEcode() : DataExcelEnum.SENSOR_TAG.getCcode());
        row2.createCell(2).setCellValue(international ? DataExcelEnum.SENSOR_TIME.getEcode() : DataExcelEnum.SENSOR_TIME.getCcode());
        row2.createCell(3).setCellValue(international ? DataExcelEnum.SENSOR_VALUE.getEcode() : DataExcelEnum.SENSOR_VALUE.getCcode());
        row2.createCell(4).setCellValue(international ? DataExcelEnum.SENSOR_QUALITY.getEcode() : DataExcelEnum.SENSOR_QUALITY.getCcode());

        //格式化输出时间
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        int rowNum = 2;
        for (SensorResponse sensorResponse : list) {
            String sensorID = sensorResponse.getSensorID();
            String tag = sensorResponse.getTag();
            String value = sensorResponse.getValue();
            Long timestamp = sensorResponse.getTimestamp();
            Boolean quality = sensorResponse.getQuality();
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sensorID == null ? "" : sensorID);
            row.createCell(1).setCellValue(tag == null ? "" : tag);
            row.createCell(2).setCellValue(timestamp == null ? "" : df.format(timestamp));
            row.createCell(3).setCellValue(value == null ? "" : value);
            row.createCell(4).setCellValue(Boolean.TRUE.equals(quality) ? QUALITY_NORMAL : QUALITY_ABNORMAL);
        }
        return wb;
    }

    /**
     * 参数验证
     *
     * @param request
     * @return
     */
    private ModelMap validateCommonRequest(DaWorkerClientPointRequest request) {
        Map<String, Object> map = new HashMap<>(2);
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
        return null;
    }

    /**
     * 获取数据
     *
     * @param request
     * @return
     * @throws Exception
     */
    private List<SensorResponse> queryHistoricalData(DaWorkerClientPointRequest request) throws Exception {
        Integer interval = request.getInterval();
        Map<String, List<DaWorkerClientPointResponse.DaWorkerClientPoint>> normalPointList;
        if (interval == null) {
            normalPointList = daWorkerClientService.getHistoricalPoints(request).getNormalPointList();
        } else {
            normalPointList = daWorkerClientService.getHistoricalPointsWithInterval(request).getNormalPointList();
        }
        List<DaWorkerClientPointResponse.DaWorkerClientPoint> tempList = normalPointList.entrySet().stream().flatMap(entry -> entry.getValue().stream()).collect(Collectors.toList());
        return transferDaWorkerClientPoint2SensorResponse(tempList);
    }


    /**
     * 将DaWorkerClientPoint转换成SensorResponse
     *
     * @param pointList
     * @return
     * @throws Exception
     */
    private List<SensorResponse> transferDaWorkerClientPoint2SensorResponse(List<DaWorkerClientPointResponse.DaWorkerClientPoint> pointList) {

        if (pointList == null || pointList.size() == 0) {
            return new ArrayList<>(0);
        }
        // 获取所有可用测点列表
        List<DaConfigSensor> sensorList = dataSensorService.getAllSensorList();
        // 获取sigetEcode()和tag映射
        Map<String, String> map = sensorList.stream().collect(Collectors.toMap(DaConfigSensor::getSiecode, DaConfigSensor::getTag, (old, current) -> current));
        return pointList.stream().map(point -> {
            SensorResponse response = new SensorResponse();
            response.setSensorID(point.getSensorID());
            response.setTag(map.get(point.getSensorID()));
            response.setValue(point.getValue());
            response.setTimestamp(point.getTimestamp());
            response.setQuality(point.getQuality());
            return response;
        }).collect(Collectors.toList());
    }


    /**
     * http excel request
     */
    public static class DaWorkerClientPointRequestPlus extends DaWorkerClientPointRequest {
        private Boolean international;

        public Boolean getInternational() {
            return international;
        }

        public void setInternational(Boolean international) {
            this.international = international;
        }
    }

    /**
     * http response
     */
    public static class SensorResponse {

        private String sensorID;

        private String tag;

        private String value;

        private Long timestamp;

        private Boolean quality;

        public String getSensorID() {
            return sensorID;
        }

        public void setSensorID(String sensorID) {
            this.sensorID = sensorID;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }

        public Boolean getQuality() {
            return quality;
        }

        public void setQuality(Boolean quality) {
            this.quality = quality;
        }
    }
}
