package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.enums.BatchRegistSensorCodeEnum;
import com.siemens.dasheng.web.enums.CategoryType;
import com.siemens.dasheng.web.enums.ConnectorDatabaseType;
import com.siemens.dasheng.web.enums.DelStatus;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.mapper.DaConfigProviderMapper;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.mapperfactory.DaConfigConnectorSensorMapperCommon;
import com.siemens.dasheng.web.mapperfactory.DaConfigSensorMapperCommon;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.batchregist.*;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.singleton.SieExecutorsPool;
import com.siemens.dasheng.web.singleton.constant.CommonConstant;
import com.siemens.dasheng.web.util.ServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.DATA;

/**
 * DataSensorBatchImportService
 *
 * @author zhangliming
 * @date 2019/10/12
 */
@Service
public class DataSensorBatchImportService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DaConfigConnectorSensorMapperCommon daConfigConnectorSensorMapperCommon;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private DataSensorService dataSensorService;

    @Autowired
    private DaConfigApplicationSyncService daConfigApplicationSyncService;

    @Autowired
    private DaConfigSensorMapperCommon daConfigSensorMapperCommon;

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;

    @Autowired
    private StringEncryptor stringEncryptor;

    public static final String TRUE_STR = "yes";

    public static final String TRUE_FALSE_STR = "yes,no";

    public static final String QUERY_TAG_KEY = "tagList";

    public static final Integer TAG_MAX_LENGTH = 200;

    public static final Integer OPENPLANT_TAG_MAX_LENGTH = 80;

    public static final Integer REPEAT_LIMIT_LOWEST = 2;

    public static final Integer SENSOT_MAX_LENGTH = 100;

    public static final Integer DESCRIPTION_MAX_LENGTH = 200;

    private static final int BATCH_SIZE = 2000;

    /**
     * 执行测试连接的任务数目
     */
    private AtomicInteger taskCount;

    private void reduceTaskCount() {
        taskCount.getAndDecrement();
    }

    private AtomicInteger getTaskCount() {
        return taskCount;
    }


    private static final String MAXIMUM_LICENSED_POINT = "PI_MAX_LICENCE_LIMIT";
    private static final String MAXIMUM_LICENSED_POINT_DETAIL = "Maximum";
    private static final String PI_CLIENT_NO_AUTH = "PI_CLIENT_NO_AUTH";
    private static final String PI_CLIENT_NO_AUTH_DETAIL = "Access";
    private static final String PI_CLIENT_TIMEOUT = "PI_CONNECT_TIMEOUT_MSG";
    private static final String PI_CLIENT_TIMEOUT_DETAIL = "Timeout on PI RPC";
    private static final String OPENPLANT_TIMEOUT = "SocketTimeoutException";

    private static final List<String> TAG_BEGIN_CHARACTERS_INCLUDE_LIST = new ArrayList<>();
    //pi 异常tagname prefix
    static {
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("#");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("-");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add(".");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("[");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("]");
    }

    /**
     * 校验tag,sensor是否合法
     * @param lists
     * @param originalFilename
     * @param connectorId
     * @param applicationId
     * @return BatchRegistResponse
     */
    public BatchRegistResponse validRegistData(List<List<String>> lists, String originalFilename, Long connectorId, Long applicationId) throws Exception {
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(connectorId);
        boolean isopenplant = false;
        if (connector.getArchivedDatabase().equals(ConnectorDatabaseType.OPENPLANT.getType())) {
            if (CategoryType.OPENPLANTSDK_LINUX.getType().equals(connector.getConnectApproach())) {
                isopenplant = true;
            }
        }
        BatchRegistResponse response = new BatchRegistResponse();
        //struct regist data
        List<RegistData> dataList = structRegistData(lists);
        //必填、字段长度、tag名称合法性校验
        this.checkFieldValid(dataList, connector, isopenplant);
        //excel sensor or tag是否存在重复
        this.checkExcelFieldRepeat(dataList);
        //sensor在库中是否存在
        this.checkSensorExistInDataBase(dataList);
        //create tag (true / false 相应逻辑校验)
        this.checkCreateTag(dataList, connector, isopenplant);
        //测点是否已经mapping
        this.checkTagMapped(dataList, connector, applicationId);
        dataList.forEach(a-> {if(a.getErrorInfos() != null && a.getErrorInfos().size() > 0) {a.setValidate(false);} else { a.setValidate(true);}});
        response.setFileName(originalFilename);
        response.setLists(dataList);
        return response;
    }

    /**
     * 校验tag是否已经map
     * @param dataList
     * @param connector
     * @param applicationId
     */
    public void checkTagMapped(List<RegistData> dataList, DaConfigConnector connector, Long applicationId) {
        List<String> validTagList = new ArrayList<>();
        List<RegistData> toCheckedList = new ArrayList<>();
        dataList.stream().filter(a-> (a.getCreateTag() != null && !a.getCreateTag() && !StringUtils.isBlank(a.getTag()))).forEach(a->toCheckedList.add(a));
        toCheckedList.stream().forEach(a->validTagList.add(a.getTag()));
        //已存在DaConfigSensor
        String sqlstr = buildInSql(validTagList, "a.tag");
        List<DaConfigSensor> sensorList = daConfigSensorMapper.selectListByConnectorIdAndTags(connector.getId(), applicationId, sqlstr);
        Map<String, String> result = sensorList.stream().collect(Collectors.toMap(DaConfigSensor::getTag, DaConfigSensor::getSiecode, (key1, key2) -> key2));
        for (RegistData data : toCheckedList) {
            if (result.keySet().contains(data.getTag())) {
                this.setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_HAS_BEEN_MAPED);
            }
        }
    }

    /**
     * 校验是否创建tag逻辑
     * @param dataList
     * @param connector
     * @param isopenplant
     * @throws Exception
     */
    public void checkCreateTag(List<RegistData> dataList, DaConfigConnector connector, boolean isopenplant) throws Exception {
        DaConfigConnectorRequest daConfigConnectorRequest = convertConnectToRequest(connector);
        List<String> tagList = new ArrayList<>();
        dataList.stream().filter(a->a.getTag() != null).forEach(a->tagList.add(a.getTag()));
        //批量查询tag from datasource
        Map<String, List<String>> result;
        if(isopenplant){
            String prefix = dataSensorService.struOpTag(connector.getId());
            List<String> openplanTagList = new ArrayList<>();
            tagList.forEach(a->openplanTagList.add(prefix + a.toUpperCase()));
            //tag添加前缀
            dataList.stream().filter(d->d.getTag() != null).forEach(d->d.setTag(prefix + d.getTag().toUpperCase()));
            result = dataConnectorService.selectTagListBatch(daConfigConnectorRequest, openplanTagList);
        } else {
            result = dataConnectorService.selectTagListBatch(daConfigConnectorRequest, tagList);
        }
        if (result != null) {
            List<String> existTagList = result.get(QUERY_TAG_KEY);
            StringBuffer sb = new StringBuffer();
            if (existTagList != null && existTagList.size() > 0) {
                existTagList.forEach(a->sb.append(a).append(","));
                for (RegistData data : dataList) {
                    if (data.getCreateTag() != null && data.getCreateTag() && sb.toString().contains(data.getTag())) {
                        setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_HAS_EXIST_UPDER_CONNECTOR);
                    }
                    if (data.getCreateTag() != null && !data.getCreateTag() && !(sb.toString().contains(data.getTag()))) {
                        setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_NOT_EXIST_UPDER_CONNECTOR);
                    }
                }
            } else {
                logger.info("existTagList tag result empty! ");
                dataList.stream().filter(a->a.getCreateTag() != null && !a.getCreateTag()).forEach(a->this.setErrorInfos(a, BatchRegistSensorCodeEnum.TAG_NOT_EXIST_UPDER_CONNECTOR));
            }
        } else {
            logger.info("selectTagListBatch result empty! ");
            dataList.stream().filter(a->a.getCreateTag() != null && !a.getCreateTag()).forEach(a->this.setErrorInfos(a, BatchRegistSensorCodeEnum.TAG_NOT_EXIST_UPDER_CONNECTOR));
        }
    }

    /**
     * 校验sensor在库中是否存在
     * @param dataList
     */
    public void checkSensorExistInDataBase(List<RegistData> dataList) {
        List<String> sieCodes = new ArrayList<>();
        dataList.stream().filter(a->a.getSiecode() != null).forEach(a->sieCodes.add(a.getSiecode()));
        List<DaConfigSensor> exitsSensors = daConfigSensorMapper.selectBySiecodes(sieCodes);
        StringBuffer sb = new StringBuffer();
        for (DaConfigSensor sensor : exitsSensors) {
            sb.append(sensor.getSiecode()).append(",");
        }
        dataList.stream().filter(a->a.getSiecode() != null && sb.toString().contains(a.getSiecode())).forEach(a->this.setErrorInfos(a, BatchRegistSensorCodeEnum.SENSOR_HAS_EXIST));
    }

    /**
     * 校验excel文件字段是否存在重复（除第一个，其他报错误）
     * @param dataList
     */
    public void checkExcelFieldRepeat(List<RegistData> dataList) {
        //check siecode
        Map<String, List<RegistData>> siecodeMap = dataList.stream().filter(a->a.getSiecode() != null)
                .collect(Collectors.groupingBy(RegistData::getSiecode));
        for (Map.Entry<String, List<RegistData>> entry : siecodeMap.entrySet()) {
            //存在重复siecode
            List<RegistData> valueList = entry.getValue();
            if (valueList.size() >= REPEAT_LIMIT_LOWEST) {
                //除第一个，其他报错误
                for (int i = 1; i < valueList.size(); i++) {
                    RegistData data = valueList.get(i);
                    setErrorInfos(data, BatchRegistSensorCodeEnum.SIECODE_REPEAT_IN_EXCEL_ERROR);
                }
            }
        }
        //check tag
        Map<String, List<RegistData>> tagNameMap = dataList.stream().filter(a->a.getTag() != null)
                .collect(Collectors.groupingBy(RegistData::getTag));
        for (Map.Entry<String, List<RegistData>> entry : tagNameMap.entrySet()) {
            //存在重复siecode
            List<RegistData> valueList = entry.getValue();
            if (valueList.size() >= REPEAT_LIMIT_LOWEST) {
                //除第一个，其他报错误
                for (int i = 1; i < valueList.size(); i++) {
                    RegistData data = valueList.get(i);
                    setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_REPEAT_IN_EXCEL_ERROR);
                }
            }
        }

    }

    /**
     * 必填、字段长度、tag名称合法性校验
     * @param dataList
     * @param connector
     * @param isopenplant
     * @return
     */
    public void checkFieldValid(List<RegistData> dataList, DaConfigConnector connector, boolean isopenplant) {
        DaConfigConnectorRequest connectorRequest = convertConnectToRequest(connector);
        Integer approachType = Integer.valueOf(dataConnectorService.getConnectorApproachType(connectorRequest));
        for (RegistData data : dataList) {
            //tag 长度及合法性校验
            if (data.getTag() != null) {
                //tagname logic check
                if (isopenplant) {
                    //openplant 只支持英文字符和下划线
                    if(!isContainSpecialChart(data.getTag())){
                        setErrorInfos(data, BatchRegistSensorCodeEnum.OPENPLANT_TAG_NAME_INVALID_ERROR);
                    }
                } else {
                    if (TAG_BEGIN_CHARACTERS_INCLUDE_LIST.contains(data.getTag().substring(0,1))) {
                        setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_NAME_NOT_IMPROPER);
                    }
                }
                if (isopenplant) {
                    if (data.getTag().length() > OPENPLANT_TAG_MAX_LENGTH) {
                        setErrorInfos(data, BatchRegistSensorCodeEnum.OPENPLANT_TAG_MAX_LENGTH);
                    }
                } else {
                    if (data.getTag().length() > TAG_MAX_LENGTH) {
                        setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_MAX_LENGTH_ERROR);
                    }
                }
                if (isContainChinese(data.getTag())) {
                    setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_CHINESE_CHARACTER_ERROR);
                }
            } else {
                setErrorInfos(data, BatchRegistSensorCodeEnum.TAG_NOT_BLANK_ERROE);
            }
            //create tag 合法性校验
            if (data.getCreateTag() == null) {
                setErrorInfos(data, BatchRegistSensorCodeEnum.CREATE_TAG_UNKWON_ERROE);
            }
            //opu只做映射，所以createTag必须为no
            if (DataConnectorService.OPC_APPROACH_WAYS_LIST.contains(approachType)) {
                if (data.getCreateTag() != null && data.getCreateTag()) {
                    setErrorInfos(data, BatchRegistSensorCodeEnum.OPU_CREATE_TAG_NO_ERROR);
                }
            }
            //sensor code 合法性校验
            if (data.getSiecode() != null) {
                if (data.getSiecode().length() > SENSOT_MAX_LENGTH) {
                    setErrorInfos(data, BatchRegistSensorCodeEnum.SENSOR_MAX_LENGTH_ERROR);
                }
                if (isContainChinese(data.getSiecode())) {
                    setErrorInfos(data, BatchRegistSensorCodeEnum.SIECODE_CHINESE_CHARACTER_ERROR);
                }
            } else {
                setErrorInfos(data, BatchRegistSensorCodeEnum.SENSOR_CODE_NOT_BLANK_ERROE);
            }
            //description 合法性校验
            if (data.getDescription() != null) {
                if (data.getDescription().length() > DESCRIPTION_MAX_LENGTH) {
                    setErrorInfos(data, BatchRegistSensorCodeEnum.DESCRIPTION_MAX_LENGTH_ERROR);
                }
            }
        }
    }


    /**
     * 构造待导入对象
     * @param lists
     * @return
     */
    public List<RegistData> structRegistData(List<List<String>> lists) {
        List<RegistData> dataList = new ArrayList<>();
        for (List<String> colums : lists) {
            RegistData data = new RegistData();
            //tag
            data.setTag(StringUtils.isBlank(colums.get(0)) ? null : colums.get(0));
            //create tag?
            String createTag = StringUtils.isBlank(colums.get(1)) ? null : colums.get(1).toLowerCase();
            if (createTag == null) {
                data.setCreateTag(false);
            } else {
                if (TRUE_FALSE_STR.contains(createTag)) {
                    if (TRUE_STR.equals(createTag)) {
                        data.setCreateTag(true);
                    } else {
                        data.setCreateTag(false);
                    }
                } else {
                    data.setCreateTag(null);
                }
            }
            //sensor code
            data.setSiecode(StringUtils.isBlank(colums.get(2)) ? null : colums.get(2));
            //sensor description
            data.setDescription(StringUtils.isBlank(colums.get(3)) ? null : colums.get(3));
            dataList.add(data);
        }
        return dataList;
    }


    /**
     * 设置数据错误信息
     * @param data
     * @param batchRegistSensorCodeEnum
     * @return RegistData
     */
    public RegistData setErrorInfos (RegistData data, BatchRegistSensorCodeEnum batchRegistSensorCodeEnum) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorCode(batchRegistSensorCodeEnum.getCode());
        errorInfo.setErrorMsg(batchRegistSensorCodeEnum.getMsg());
        if (data.getErrorInfos() == null) {
            List<ErrorInfo> errors = new ArrayList<>();
            errors.add(errorInfo);
            data.setErrorInfos(errors);
        } else {
            List<ErrorInfo> errors = data.getErrorInfos();
            errors.add(errorInfo);
        }
        return data;
    }


    /**
     * 数据批量注册
     * @param listData
     * @param originalFilename
     * @param connectorId
     * @param applicationId
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportResultInfo doRegistBatch(List<List<String>> listData, String originalFilename, Long connectorId, Long applicationId) throws Exception  {
        ImportResultInfo importResultInfo = new ImportResultInfo();
        //先校验
        BatchRegistResponse batchRegistResponse = this.validRegistData(listData, originalFilename, connectorId, applicationId);
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(connectorId);
        DaConfigConnectorRequest connectorRequest = convertConnectToRequest(connector);
        List<RegistData> validDataList = batchRegistResponse.getLists().stream().filter(a->a.getValidate()).collect(Collectors.toList());
        //关系型数据库插入
        insertIntoRelationalDB(validDataList, connector, applicationId);
        //时序数据库插入
        Set<String> wrongTagList = this.insertIntoSerialsDB(batchRegistResponse.getLists(), connectorRequest, importResultInfo, connector);
        importResultInfo.setSucceedNum(validDataList.size() - wrongTagList.size());
        importResultInfo.setFailedNum(wrongTagList.size());
        //删除错误数据
        if (wrongTagList.size() > 0) {
            daConfigSensorMapper.deleteBatchByConnectorId(connectorId, wrongTagList);
            daConfigConnectorSensorMapper.deleteUnImportTagByTagList(connectorId, wrongTagList);
        }
        // 更新app版本号
        if (importResultInfo.getSucceedNum() > 0) {
            daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(applicationId);
        }
        return importResultInfo;
    }

    /**
     * 关系型数据库插入
     * @param validDataList
     * @param connector
     * @param applicationId
     */
    public void insertIntoRelationalDB(List<RegistData> validDataList, DaConfigConnector connector, Long applicationId) {
        List<String> validTagList = new ArrayList<>();
        validDataList.forEach(a->validTagList.add(a.getTag()));
        //已存在DaConfigSensor
        String sqlstr = buildInSql(validTagList, "a.tag");
        List<DaConfigConnectorSensor> existConnectSensorList = daConfigConnectorSensorMapper.selectListByTags(sqlstr)
                .stream().filter(a->a.getConnectorId().equals(connector.getId())).collect(Collectors.toList());
        //DaConfigConnectorSensor对应已存在tag
        List<String> existConnectSensorTags = new ArrayList();
        existConnectSensorList.forEach(a-> existConnectSensorTags.add(a.getTag()));
        //待插入数据
        List<DaConfigSensor> configSensorList = new ArrayList<>();
        List<DaConfigConnectorSensor> connectorSensorList = new ArrayList<>();
        for(RegistData data : validDataList) {
            DaConfigSensor daConfigSensor = dataSensorService.initConfigSensor(data, applicationId, connector.getId());
            configSensorList.add(daConfigSensor);
            //插入DaConfigSensor(不存在时，插入)
            if (!existConnectSensorTags.contains(data.getTag())) {
                //DaConfigConnectorSensor
                DaConfigConnectorSensor connectorSensor = this.initConnectorSensor(data.getTag(), connector);
                connectorSensorList.add(connectorSensor);
            }
        }
        taskCount = new AtomicInteger((configSensorList.size() + connectorSensorList.size())/BATCH_SIZE);
        if (configSensorList.size() > 0) {
            insertConfigSensor(configSensorList);
        }
        if (connectorSensorList.size() > 0) {
            insertConnectorSensor(connectorSensorList);
        }
    }

    /**
     * 批量插入DaConfigSensor
     * @param configSensorList
     * @return
     */
    public int insertConfigSensor(List<DaConfigSensor> configSensorList) {
        List<DaConfigSensor> sensorList = new ArrayList<>();
        // set batch insert size 4000 (8000*4<32767)
        int batchSize = BATCH_SIZE;
        int insertNum = 0;
        int currentIndex = 0;
        for (DaConfigSensor sensor : configSensorList) {
            if (currentIndex == batchSize) {
                SieExecutorsPool.executor(new SensorTask(sensorList));
                currentIndex = 0;
                sensorList = new ArrayList<>();
            }
            sensorList.add(sensor);
            currentIndex++;
        }
        // insert last records
        if (sensorList.size() > 0) {
            insertNum += daConfigSensorMapperCommon.insertList(sensorList);
        }
        return insertNum;
    }


    /**
     * 批量插入DaConfigConnectorSensor
     * @param connectorSensorList
     * @return
     */
    public int insertConnectorSensor(List<DaConfigConnectorSensor> connectorSensorList) {
        List<DaConfigConnectorSensor> retList = new ArrayList<>();
        // set batch insert size 4000 (8000*4<32767)
        int batchSize = BATCH_SIZE;
        int insertNum = 0;
        int currentIndex = 0;

        for (DaConfigConnectorSensor consensor : connectorSensorList) {
            if (currentIndex == batchSize) {
                SieExecutorsPool.executor(new ConnectorSensorTask(retList));
                currentIndex = 0;
                retList = new ArrayList<>();
            }
            retList.add(consensor);
            currentIndex++;
        }
        // insert last records
        if (retList.size() > 0) {
            insertNum += daConfigConnectorSensorMapperCommon.insertList(retList);
        }
        return insertNum;
    }

    /**
     * @param dataList
     * @param connectorRequest
     * @param resultInfo
     * @param connector
     * @return wrong tag list
     * @throws Exception
     */
    public Set<String> insertIntoSerialsDB(List<RegistData> dataList, DaConfigConnectorRequest connectorRequest, ImportResultInfo resultInfo, DaConfigConnector connector) throws Exception {
        List<RegistData> createTagList = dataList.stream().filter(a->a.getCreateTag() != null
                && a.getCreateTag() && a.getValidate()).collect(Collectors.toList());
        boolean isopenplant = false;
        if (connector.getArchivedDatabase().equals(ConnectorDatabaseType.OPENPLANT.getType())) {
            if (CategoryType.OPENPLANTSDK_LINUX.getType().equals(connector.getConnectApproach())) {
                isopenplant = true;
            }
        }
        //未插入的tag
        Set<String> wrongTagList = new HashSet<>();
        //错误列表
        List<ImportFalse> falseList = new ArrayList<>();
        //组装tag数据
        HashSet<String> tagSet = new HashSet<>();
        for (RegistData data : createTagList) {
            String tag = data.getTag();
            if (isopenplant) {
                tagSet.add(tag.substring(tag.lastIndexOf(".") + 1));
            } else {
                tagSet.add(tag);
            }
        }
        if (tagSet.size() > 0) {
            //批量插入
            ModelMap result = dataConnectorService.batchInsertTagData(connectorRequest, tagSet);
            //插入结果（已存在的也会包含在返回结果中）
            List<String> succeedTags = (List<String>)result.get(DATA);
            if (null != succeedTags && succeedTags.size() > 0) {
                resultInfo.setFailedNum(createTagList.size() - succeedTags.size());
                createTagList.stream().filter(a->!succeedTags.contains(a.getTag())).forEach(a->{falseList.add(new ImportFalse(a.getTag(), a.getSiecode()));});
            } else {
                resultInfo.setFailedNum(createTagList.size());
                createTagList.stream().forEach(a->{falseList.add(new ImportFalse(a.getTag(), a.getSiecode()));});
            }
        }
        resultInfo.setFailedList(falseList);
        falseList.forEach(a->wrongTagList.add(a.getTag()));
        return wrongTagList;
    }


    /**
     * 捕获已知错误
     * @param errorMsg
     * @param operateType
     * @return
     */
    public static BatchRegistSensorCodeEnum analysisErrorInfo(String errorMsg, String operateType) {
        if (errorMsg.contains(MAXIMUM_LICENSED_POINT) || errorMsg.contains(MAXIMUM_LICENSED_POINT_DETAIL)) {
            return BatchRegistSensorCodeEnum.PI_MAX_LICENCE_LIMIT;
        } else if (errorMsg.contains(PI_CLIENT_NO_AUTH) || errorMsg.contains(PI_CLIENT_NO_AUTH_DETAIL)) {
            return BatchRegistSensorCodeEnum.PI_CLIENT_NO_AUTH_MSG;
        } else if (errorMsg.contains(PI_CLIENT_TIMEOUT) || errorMsg.contains(PI_CLIENT_TIMEOUT_DETAIL)) {
            return BatchRegistSensorCodeEnum.PI_CONNECT_TIMEOUT_MSG;
        } else if (errorMsg.contains(OPENPLANT_TIMEOUT)) {
            return BatchRegistSensorCodeEnum.OPENPLANT_TIMEOUT_MSG;
        }
        if (operateType.equals("show")) {
            return BatchRegistSensorCodeEnum.VALID_IMPORT_DATA_ERROR;
        }  else {
            return BatchRegistSensorCodeEnum.BATCH_MPORT_DATA_ERROR;
        }

    }

    /**
     * 将connector转化可请求
     * @param connector
     * @return
     */
    public DaConfigConnectorRequest convertConnectToRequest(DaConfigConnector connector) {
        DaConfigConnectorRequest daConfigConnectorRequest = new DaConfigConnectorRequest();
        BeanUtils.copyProperties(connector, daConfigConnectorRequest);
        daConfigConnectorRequest.setDatabase(connector.getDatabase());
        daConfigConnectorRequest.setPassword(StringUtils.isBlank(daConfigConnectorRequest.getPassword()) ? "" : stringEncryptor.decrypt(daConfigConnectorRequest.getPassword().trim()));
        return daConfigConnectorRequest;
    }

    /**
     * 判断字符串中是否包含中文
     * @param str
     * @return boolean
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


    /**
     * 是否包含特殊字符
     * @param str
     * @return
     */
    public static boolean isContainSpecialChart(String str) {
        String regex = "^[a-zA-Z0-9_][a-zA-Z0-9_]*$";
        return str.matches(regex);
    }


    /**
     * 批量插入线程
     */
    public class SensorTask implements Runnable{
        List<DaConfigSensor> sensorList;
        public SensorTask(List<DaConfigSensor> sensorList){
            this.sensorList = sensorList;
        }

        @Override
        public void run() {
            daConfigSensorMapperCommon.insertList(sensorList);
            reduceTaskCount();
        }
    }


    /**
     * 批量插入线程
     */
    public class ConnectorSensorTask implements Runnable{
        List<DaConfigConnectorSensor> connectorSensorList;
        public ConnectorSensorTask(List<DaConfigConnectorSensor> connectorSensorList){
            this.connectorSensorList = connectorSensorList;
        }

        @Override
        public void run() {
            daConfigConnectorSensorMapperCommon.insertList(connectorSensorList);
            reduceTaskCount();
        }
    }

    /**
     *  查询app下所有connector
     * @param appId
     * @return
     */
    public List<Long> findAppConnectors(Long appId) {
        List<DaConfigProvider> providerList = daConfigProviderMapper.queryApplicationProvider(appId);
        List<Long> providerIds = new ArrayList<>();
        providerList.forEach(a->providerIds.add(a.getId()));
        List<DaConfigConnectorPlus> dataConnectorList = daConfigConnectorMapper.queryConnectorListByProviderIds(providerIds);
        List<Long> connectorIds = new ArrayList<>();
        dataConnectorList.forEach(a->connectorIds.add(a.getId()));
        return connectorIds;
    }

    /**
     * 初始化connectorsensor
     * @param tag
     * @param connector
     * @return
     */
    public DaConfigConnectorSensor initConnectorSensor(String tag, DaConfigConnector connector) {
        DaConfigConnectorSensorPlus connSensor = new DaConfigConnectorSensorPlus();
        connSensor.setConnectorId(connector.getId());
        connSensor.setConnectorInfo(connector.getConnectorInfo());
        connSensor.setTag(tag);
        connSensor.setUnit(null);
        connSensor.setStatus(DelStatus.IMPORTED.getType());
        connSensor.setFromRegist(CommonConstant.STATUS1);
        return connSensor;
    }

    /**
     * 构造in sql
     *
     * @param idList
     * @param field
     * @return
     */
    private String buildInSql(List<String> idList, String field) {
        String idStr = "'" + StringUtils.join(idList, "','") + "'";
        String[] str = idStr.split(",");
        String commonSql = ServiceUtil.mergeStrForIn(str, field);
        return commonSql;
    }

}
