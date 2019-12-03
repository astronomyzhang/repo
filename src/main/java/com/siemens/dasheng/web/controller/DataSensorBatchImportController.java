package com.siemens.dasheng.web.controller;

import com.siemens.dasheng.web.enums.BatchRegistSensorCodeEnum;
import com.siemens.dasheng.web.model.batchregist.BatchRegistResponse;
import com.siemens.dasheng.web.model.batchregist.ImportResultInfo;
import com.siemens.dasheng.web.service.DataSensorBatchImportService;
import com.siemens.dasheng.web.util.OfflineDataAnalysisUtil;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * DataSensorBatchImportController
 *
 * @author zhangliming
 * @date 2019/10/12
 */
@Api(value = "sensorRegistBatch", description = "sensorRegistBatch")
@Controller
@RequestMapping("/sensorRegistBatch")
public class DataSensorBatchImportController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Integer EXCEL_MAX_SIZE = 20000;

    @Autowired
    private DataSensorBatchImportService dataSensorBatchImportService;

    @Autowired
    private OfflineDataAnalysisUtil offlineDataAnalysisUtil;

    /**
     * //后台服务异常
     */
    public static final String SERVICE_EXCEPTION = "SERVICE_EXCEPTION";

    /**
     * 离线数据文件下载
     */
    @SuppressWarnings("all")
    @ApiOperation(value = "download regist template", notes = "download regist template")
    @GetMapping(value = "/template/v1")
    @ResponseBody
    @LogEventName(operateType="template/v1",businessDescription="download template", webUrl = "/sensorRegistBatch/template/v1")
    public ModelMap template() {
        InputStream fileInputStream = null;
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        try {
            fileInputStream = this.getClass().getResourceAsStream("/sensortemplate/sensorRegistDataTemplate.xls");
            byte[] bytes = new byte[2048];
            int len;
            while ((len = fileInputStream.read(bytes)) > 0) {
                bytestream.write(bytes, 0, len);
            }
            byte[] data = bytestream.toByteArray();
            return HttpResponseUtil.ofmSuccessResponseMap(new BASE64Encoder().encode(data));
        } catch (Exception e) {
            logger.error("sensorExcelTemplateExport errors : {} , e {} ", e.getMessage(), e);
            e.printStackTrace();
            return HttpResponseUtil.ofmFailResponseMap(SERVICE_EXCEPTION);
        } finally {
            try {
                bytestream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * @param request
     * @throws Exception
     */
    @ApiOperation(value = "tagExcel parsing display", notes = "tagExcel parsing display")
    @ResponseBody
    @PostMapping(value = "/showData/v1")
    @LogEventName(operateType="showData",businessDescription="tagExcel parsing display", webUrl = "/sensorRegistBatch/showData/v1")
    public ModelMap showData(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Long connectorId = Long.valueOf(request.getParameter("connectorId"));
        Long applicationId = Long.valueOf(request.getParameter("applicationId"));
        MultipartFile file = multipartRequest.getFile("registData");
        if (file == null) {
            return HttpResponseUtil.ofmFailResponseMap(BatchRegistSensorCodeEnum.TO_UPDATE_REGIST_DATA_ERROR);
        }
        List<List<String>> listData;
        //get dataList from excel
        try {
            listData = offlineDataAnalysisUtil.getListData(file);
            if (listData != null && listData.size() > EXCEL_MAX_SIZE) {
                return HttpResponseUtil.ofmFailResponseMap(BatchRegistSensorCodeEnum.EXCEL_MAX_SIZE_ERROE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("happen error {}, e {} ", e.getMessage(), e);
            return HttpResponseUtil.ofmFailResponseMap(BatchRegistSensorCodeEnum.READ_DATA_FROM_EXCEL_ERROR);
        }
        //valid data
        try {
            BatchRegistResponse batchRegistResponse = dataSensorBatchImportService.validRegistData(listData, file.getOriginalFilename(), connectorId, applicationId);
            return HttpResponseUtil.ofmSuccessResponseMap(batchRegistResponse);
        } catch (Exception e) {
            logger.error("validRegistData error {}, e {} ", e.getMessage(), e);
            BatchRegistSensorCodeEnum codeEnum = DataSensorBatchImportService.analysisErrorInfo(e.getMessage(), "show");
            return  HttpResponseUtil.ofmFailResponseMap(codeEnum);
        }
    }


    /**
     *批量导入
     * @param request
     * @return
     */
    @ApiOperation(value = "import data", notes = "import data")
    @ResponseBody
    @PostMapping(value = "/import/v1")
    @LogEventName(operateType="import",businessDescription="import data", webUrl = "/sensorRegistBatch/import/v1")
    public ModelMap importData(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Long connectorId = Long.valueOf(request.getParameter("connectorId"));
        Long applicationId = Long.valueOf(request.getParameter("applicationId"));
        MultipartFile file = multipartRequest.getFile("registData");
        if (file == null) {
            return HttpResponseUtil.ofmFailResponseMap(BatchRegistSensorCodeEnum.TO_UPDATE_REGIST_DATA_ERROR);
        }
        //校验connector是否被移除
        List<Long> connectorIds = dataSensorBatchImportService.findAppConnectors(applicationId);
        if (!connectorIds.contains(connectorId)) {
            HttpResponseUtil.ofmFailResponseMap(BatchRegistSensorCodeEnum.READ_DATA_FROM_EXCEL_ERROR);
        }
        List<List<String>> listData;
        //get dataList from excel
        try {
            listData = offlineDataAnalysisUtil.getListData(file);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("happen error {}, e {} ", e.getMessage(), e);
            return HttpResponseUtil.ofmFailResponseMap(BatchRegistSensorCodeEnum.READ_DATA_FROM_EXCEL_ERROR);
        }
        //batch regist data
        try {
            ImportResultInfo importResultInfo = dataSensorBatchImportService.doRegistBatch(listData, file.getOriginalFilename(), connectorId, applicationId);
            return HttpResponseUtil.ofmSuccessResponseMap(importResultInfo);
        } catch (Exception e) {
            logger.error("import data error {}, e {} ", e.getMessage(), e);
            BatchRegistSensorCodeEnum codeEnum = DataSensorBatchImportService.analysisErrorInfo(StringUtils.isBlank(e.getMessage()) ? "" : e.getMessage(), "import");
            return  HttpResponseUtil.ofmFailResponseMap(codeEnum);
        }
    }

}


