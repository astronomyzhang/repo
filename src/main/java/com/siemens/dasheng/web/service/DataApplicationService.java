package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.enums.*;
import com.siemens.dasheng.web.event.DaAppUpdateEvent;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.mapperfactory.DaConfigConnectorSensorMapperCommon;
import com.siemens.dasheng.web.mapperfactory.DaConfigSensorMapperCommon;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.dto.AppInfo;
import com.siemens.dasheng.web.page.PageRequest;
import com.siemens.dasheng.web.page.PageRespone;
import com.siemens.dasheng.web.request.*;
import com.siemens.dasheng.web.response.QueryApplicationTreeResponse;
import com.siemens.dasheng.web.singleton.constant.CommonConstant;
import com.siemens.dasheng.web.util.ServiceUtil;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;

/**
 * DataApplicationService
 *
 * @author xuxin
 * @date 2019/3/5
 */
@Service
public class DataApplicationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigApplicationSyncService daConfigApplicationSyncService;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private DaConfigAppProviderMapper daConfigAppProviderMapper;

    @Autowired
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private DaConfigConnectorSensorMapperCommon daConfigConnectorSensorMapperCommon;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    private DaConfigAppExtensionMapper daConfigAppExtensionMapper;

    @Autowired
    private DaConfigSensorMapperCommon daConfigSensorMapperCommon;

    @Autowired
    private DaConfigApplicationSaveService daConfigApplicationSaveService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private DaAppResourceUsageMapper daAppResourceUsageMapper;


    private static final String TOTAL_NUM = "totalNum";
    private static final String IMPORTED_NUM = "importNum";
    private static final String UNIMPORT_NUM = "unimportNum";
    private static final String NO_EXIST_NUM = "noExistNum";
    private static final String CON_SENSOR_LIST = "conSensorList";
    private static final String CON_NO_EXIST_TAG = "conNoExistTag";
    private static final String SUCCESS_NUM = "successNum";
    private static final String SIECODE_EXIST_TAG = "sieCodeExistTag";
    private static final String SIECODE_BLANK_TAG = "sieCodeBlankTag";
    private static final String TAG_NO_EXIST_LIAT = "tagNoExistList";
    private static final String SIECODE_MAX_LENGTH_TAG = "siecodeMaxLengthTag";
    private static final String UNIT_MAX_LENGTH_TAG = "unitMaxLengthTag";
    private static final String DES_MAX_LENGTH_TAG = "descriptionMaxLengthTag";
    private static final String REPEAT_SENSOR_TAG = "repeatSensorTag";
    private static final int SUCCESS_STATUS = 1;
    private static final int FAIL_STATUS = 2;
    private static final int SIECODE_SIZE = 100;
    private static final int UNIT_SIZE = 100;
    private static final int DESCRIPTION_SIZE = 200;


    public QueryApplicationTreeResponse queryApplicationTree(HttpServletRequest request) {
        QueryApplicationTreeResponse queryApplicationTreeResponse = new QueryApplicationTreeResponse();
        //查询application列表
        List<DaConfigApplication> applicationList = daConfigApplicationMapper.selectAll();
        List<DaConfigApplicationPlus> appPlusList = new ArrayList(applicationList);
        if (CollectionUtils.isEmpty(appPlusList)) {
            queryApplicationTreeResponse.setStatus(0);
            queryApplicationTreeResponse.setApplicationList(appPlusList);
            return queryApplicationTreeResponse;
        }
        List<Long> appIds = struAppIds(appPlusList);
        List<DaConfigAppProvider> appProviderList = daConfigAppProviderMapper.selectByAppIds(appIds);
        if (CollectionUtils.isEmpty(appProviderList)) {
            queryApplicationTreeResponse.setStatus(0);
            queryApplicationTreeResponse.setApplicationList(appPlusList);
            return queryApplicationTreeResponse;
        }
        List<Long> providerIds = struProviderIds(appProviderList);
        List<DaConfigProvider> providerList = daConfigProviderMapper.selectByIds(providerIds);
        //key=appId
        Map<Long, List<DaConfigProvider>> appProviderListMap = struappProviderListMap(appProviderList, providerIds, providerList);
        for (DaConfigApplicationPlus app : appPlusList) {
            app.setProviderList(appProviderListMap.get(app.getId()));
        }
        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(providerIds);
        if (CollectionUtils.isEmpty(proConList)) {
            queryApplicationTreeResponse.setStatus(0);
            queryApplicationTreeResponse.setApplicationList(appPlusList);
            return queryApplicationTreeResponse;
        }
        List<Long> connectorIds = struConnectorIds(proConList);
        List<DaConfigConnector> connectorList = daConfigConnectorMapper.selectConnectorsByIds(connectorIds);
        //key=providerId
        Map<Long, List<DaConfigConnector>> proConnectorListMap = struProConnectorListMap(proConList, connectorList);
        for (DaConfigProvider pro : providerList) {
            pro.setDaConfigConnectorList(proConnectorListMap.get(pro.getId()));
        }
        queryApplicationTreeResponse.setStatus(0);
        queryApplicationTreeResponse.setApplicationList(appPlusList);
        return queryApplicationTreeResponse;
    }

    private Map<Long, List<DaConfigConnector>> struProConnectorListMap(List<DaConfigProviderConnector> proConList, List<DaConfigConnector> connectorList) {
        Map<Long, List<DaConfigConnector>> retMap = new HashMap<>(16);
        Map<Long, DaConfigConnector> conMap = new HashMap<>(16);
        for (DaConfigConnector con : connectorList) {
            conMap.put(con.getId(), con);
        }
        for (DaConfigProviderConnector proCon : proConList) {
            if (null == conMap.get(proCon.getConnectorId())) {
                continue;
            }
            if (CollectionUtils.isEmpty(retMap.get(proCon.getProviderId()))) {
                List<DaConfigConnector> tempList = new ArrayList<>();
                DaConfigConnector tempCon = conMap.get(proCon.getConnectorId());
                tempList.add(tempCon);
                retMap.put(proCon.getProviderId(), tempList);
                continue;
            }
            retMap.get(proCon.getProviderId()).add(conMap.get(proCon.getConnectorId()));
        }
        return retMap;
    }

    private Map<Long, List<DaConfigProvider>> struappProviderListMap(List<DaConfigAppProvider> appProviderList, List<Long> providerIds, List<DaConfigProvider> providerList) {
        Map<Long, List<DaConfigProvider>> retMap = new HashMap<>(16);
        Map<Long, DaConfigProvider> proMap = new HashMap<>(16);
        for (DaConfigProvider pro : providerList) {
            proMap.put(pro.getId(), pro);
        }
        for (DaConfigAppProvider appPro : appProviderList) {
            if (null == proMap.get(appPro.getProviderId())) {
                continue;
            }
            if (CollectionUtils.isEmpty(retMap.get(appPro.getAppId()))) {
                List<DaConfigProvider> tempList = new ArrayList<>();
                DaConfigProvider tempPro = proMap.get(appPro.getProviderId());
                tempList.add(tempPro);
                retMap.put(appPro.getAppId(), tempList);
                continue;
            }
            retMap.get(appPro.getAppId()).add(proMap.get(appPro.getProviderId()));
        }
        return retMap;
    }

    private List<Long> struConnectorIds(List<DaConfigProviderConnector> proConList) {
        List<Long> conIds = new ArrayList<>();
        for (DaConfigProviderConnector pc : proConList) {
            conIds.add(pc.getConnectorId());
        }
        return conIds;
    }

    private List<Long> struProviderIds(List<DaConfigAppProvider> appProviderList) {
        List<Long> providerIds = new ArrayList<>();
        for (DaConfigAppProvider apppro : appProviderList) {
            providerIds.add(apppro.getProviderId());
        }
        return providerIds;
    }

    private List<Long> struAppIds(List<DaConfigApplicationPlus> applicationList) {
        List<Long> appIds = new ArrayList<>();
        for (DaConfigApplication app : applicationList) {
            appIds.add(app.getId());
        }
        return appIds;
    }

    public int initConnectorTag(Long applicationId) throws Exception {

        Set<DaConnectorConfig> connectorSet = queryConnectorSet(applicationId, ConnecteStatus.CONNECTABLE.getType());
        if (null == connectorSet) {
            return 0;
        }
        //insertOrDiffTag
        for (DaConnectorConfig dcc : connectorSet) {
            DaConfigConnectorRequest daConfigConnectorRequest = new DaConfigConnectorRequest();
            BeanUtils.copyProperties(dcc, daConfigConnectorRequest);
            List<Long> appIds = new ArrayList<>();
            appIds.add(applicationId);
            daConfigConnectorRequest.setApplicationIdList(appIds);
            dataConnectorService.insertOrUpdateConnectorSensorList(daConfigConnectorRequest, 1);
        }
        return 0;
    }

    private Set<DaConnectorConfig> queryConnectorSet(Long applicationId, Long connectorStatus) {
        //查询application下所有的connector
        DaConfigApplication tempApp = new DaConfigApplication();
        tempApp.setId(applicationId);
        List<DaConfigApplication> applicationList = daConfigApplicationMapper.select(tempApp);
        if(CollectionUtils.isEmpty(applicationList)){
            return null;
        }
        DaConfigApplication application = applicationList.get(0);
        DaConfigApplicationPlus appPlus = new DaConfigApplicationPlus();
        BeanUtils.copyProperties(application, appPlus);
        if (StringUtils.isEmpty(appPlus)) {
            return null;
        }
        List<Long> appIds = new ArrayList<>();
        appIds.add(applicationId);
        List<DaConfigAppProvider> appProviderList = daConfigAppProviderMapper.selectByAppIds(appIds);
        if (CollectionUtils.isEmpty(appProviderList)) {
            return null;
        }
        List<Long> providerIds = struProviderIds(appProviderList);

        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectActiveProviderByProIds(providerIds);
        if (CollectionUtils.isEmpty(proConList)) {
            return null;
        }
        List<Long> connectorIds = struConnectorIds(proConList);
        List<DaConnectorConfig> connectorList = daConfigConnectorMapper.selectConnectorsByIdsAsc(connectorIds, connectorStatus);
        if (CollectionUtils.isEmpty(connectorList)) {
            return null;
        }
        //解密密码
        for (DaConnectorConfig dcc : connectorList) {
            dcc.setPassword(StringUtils.isEmpty(dcc.getPassword()) ? "" : stringEncryptor.decrypt(dcc.getPassword().trim()));
        }
        Set<DaConnectorConfig> connectorSet = new HashSet<>(connectorList);
        return connectorSet;
    }

    public PageRespone queryApplicationList(QueryApplicationListRequest queryApplicationListRequest) {
        long start = System.currentTimeMillis();
        Map<String, Object> retMap = new HashMap<>(8);

        PageRespone<DaConfigConnectorSensorPlus> pageRespone = new PageRespone<DaConfigConnectorSensorPlus>();
        if (null == queryApplicationListRequest) {
            queryApplicationListRequest = new QueryApplicationListRequest();
        }
        PageRequest pageRequest = queryApplicationListRequest.getPageRequest();
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }

        //query application connectorSet
        Set<DaConnectorConfig> connectorSet = queryConnectorSet(Long.valueOf(queryApplicationListRequest.getApplicationId()), null);
        if (null == connectorSet) {
            retMap.put(TOTAL_NUM, 0);
            retMap.put(IMPORTED_NUM, 0);
            retMap.put(UNIMPORT_NUM, 0);
            retMap.put(NO_EXIST_NUM, 0);
            retMap.put(CON_SENSOR_LIST, new ArrayList<>());
            pageRespone.setTotal(0);
            pageRespone.setPageSize(pageRequest.getPageSize());
            pageRespone.setCurrent(1);
            pageRespone.setData(retMap);
            return pageRespone;
        }
        List<Long> connectorIds = struConIds(connectorSet);

        //query total count
        queryApplicationListRequest.setSearchContent(queryApplicationListRequest.getSearchContent().trim().replaceAll("/", "//").replaceAll("%", "/%").replaceAll("_", "/_").replaceAll("\\?", "_").replaceAll("\\*", "%"));
        int total = daConfigConnectorSensorMapper.selectCountByCondition(queryApplicationListRequest.getConnectorStatus(), queryApplicationListRequest.getStatus(), queryApplicationListRequest.getSearchContent(), connectorIds, Long.valueOf(queryApplicationListRequest.getApplicationId()));

        pageRespone.setTotal(total);
        pageRespone.setPageSize(pageRequest.getPageSize());
        int totalnum = daConfigConnectorSensorMapper.selectCountByCondition(null, null, null, connectorIds, Long.valueOf(queryApplicationListRequest.getApplicationId()));
        int importnum = daConfigConnectorSensorMapper.selectCountByCondition(null, DelStatus.IMPORTED.getType(), null, connectorIds, Long.valueOf(queryApplicationListRequest.getApplicationId()));
        int delnum = daConfigConnectorSensorMapper.selectCountByCondition(null, DelStatus.DELETE.getType(), null, connectorIds, Long.valueOf(queryApplicationListRequest.getApplicationId()));
        if (total == 0) {
            retMap.put(TOTAL_NUM, totalnum);
            retMap.put(IMPORTED_NUM, importnum);
            retMap.put(UNIMPORT_NUM, (totalnum - importnum - delnum));
            retMap.put(NO_EXIST_NUM, delnum);
            retMap.put(CON_SENSOR_LIST, new ArrayList<>());
            pageRespone.setCurrent(1);
            pageRespone.setData(retMap);
            return pageRespone;
        }

        int totalPage = (total % pageRequest.getPageSize()) == 0 ? total / pageRequest.getPageSize() : total / pageRequest.getPageSize() + 1;
        int current = totalPage > pageRequest.getPage() ? pageRequest.getPage() : totalPage;
        pageRespone.setCurrent(current);
        List<DaConfigConnectorSensorPlus> csList = daConfigConnectorSensorMapperCommon.selectListByCondition(Long.valueOf(queryApplicationListRequest.getApplicationId()), connectorIds, queryApplicationListRequest.getConnectorStatus(), queryApplicationListRequest.getStatus(), queryApplicationListRequest.getSearchContent(),
                (current - 1) * pageRequest.getPageSize(), pageRequest.getPageSize());
        long end = System.currentTimeMillis();
        logger.info("------query app list page--------" + (end - start) + "ms");

        long start1 = System.currentTimeMillis();

        //总数量
        retMap.put(TOTAL_NUM, totalnum);
        //导入数量
        retMap.put(IMPORTED_NUM, importnum);
        //未导入数量
        retMap.put(UNIMPORT_NUM, (totalnum - importnum - delnum));
        //已删除数量
        retMap.put(NO_EXIST_NUM, delnum);
        retMap.put(CON_SENSOR_LIST, csList);
        pageRespone.setCurrent(queryApplicationListRequest.getPageRequest().getPage());
        pageRespone.setData(retMap);
        long end1 = System.currentTimeMillis();
        logger.info("------query app num --------" + (end1 - start1) + "ms");
        return pageRespone;

    }


    private List<Long> struConIds(Set<DaConnectorConfig> connectorSet) {
        List<Long> connectIds = new ArrayList<>();
        for (DaConnectorConfig con : connectorSet) {
            connectIds.add(con.getId());
        }
        return connectIds;
    }


    public List<DaConfigConnectorSensorPlus> validateSieCodeIsImported(ValidateSieCodeIsImportedRequest validateSieCodeIsImportedRequest) throws Exception {
        //query con_sen_list
        List<DaConfigConnectorSensorPlus> conSensorList = validateSieCodeIsImportedRequest.getConSensorList();
        //query app connector Set
        Set<DaConnectorConfig> connectorSet = queryAppConnectorSet(validateSieCodeIsImportedRequest.getApplicationId(), null);
        if (null == connectorSet) {
            for (DaConfigConnectorSensorPlus conSensor : conSensorList) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_CONNECTOR_NOT_EXIST);
            }
            return conSensorList;
        }
        //key=consensorId ,value = connectorId;
        Map<Long, Long> sensorConMap = struSensorConMap(connectorSet, conSensorList);

        //querySieCodeExist
        Map<String, DaConfigSensor> sieCodeMap = struSieCodeMap(conSensorList);

        //queryTagIsExist
        Map<Long, Long> tagExistMap = struTagExistMap(conSensorList);

        List<String> repeatSieCodeList = new ArrayList<>();
        //queryEverySensorExist
        List<String> checkRepeatList = new ArrayList<>();
        for (DaConfigConnectorSensorPlus conSensor : conSensorList) {
            if (null == conSensor.getCheck() || !conSensor.getCheck()) {
                checkRepeatList.add(conSensor.getSiecode());
            }
        }
        for (DaConfigConnectorSensorPlus conSensor : conSensorList) {
            if (null == sensorConMap.get(conSensor.getId())) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_CONNECTOR_NOT_EXIST);
                continue;
            }
            if (StringUtils.isEmpty(conSensor.getSiecode())) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_SIECODE_BLANK);
                continue;
            }
            if (conSensor.getSiecode().getBytes(CommonConstant.UTF8).length > SIECODE_SIZE) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_SIECODE_MAX_LENGTH_100);
                continue;
            }
            if (null != conSensor.getCheck() && conSensor.getCheck()) {
                if (checkRepeatList.contains(conSensor.getSiecode())) {
                    conSensor.setValidateStatus(FAIL_STATUS);
                    conSensor.setMsg(DA_CONFIG_SIECODE_EXIST);
                    continue;
                }
            } else {
                if (repeatSieCodeList.contains(conSensor.getSiecode())) {
                    conSensor.setValidateStatus(FAIL_STATUS);
                    conSensor.setMsg(DA_CONFIG_SIECODE_EXIST);
                    continue;
                }
            }

            if (conSensor.getUnit() != null && conSensor.getUnit().getBytes(CommonConstant.UTF8).length > UNIT_SIZE) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_UNIT_MAX_LENGTH_100);
                continue;
            }
            if (conSensor.getDescription() != null && conSensor.getDescription().getBytes(CommonConstant.UTF8).length > DESCRIPTION_SIZE) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_DESCRIPTION_MAX_LENGTH_200);
                continue;
            }
            if (null != sieCodeMap.get(conSensor.getSiecode())) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_SIECODE_EXIST);
                continue;
            }
            if (StringUtils.isEmpty(tagExistMap.get(conSensor.getId())) || tagExistMap.get(conSensor.getId()).equals(DelStatus.DELETE.getType())) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_TAG_DELETE);
                continue;
            }
            //验证tag是否被导入了
            DaConfigSensor sen = new DaConfigSensor();
            sen.setConnectorId(conSensor.getConnectorId());
            sen.setAppId(validateSieCodeIsImportedRequest.getApplicationId());
            sen.setTag(conSensor.getTag());
            if (!CollectionUtils.isEmpty(daConfigSensorMapper.select(sen))){
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_TAG_REPEAT_IMPORTED);
                continue;
            }
            if (null == conSensor.getCheck() || !conSensor.getCheck()) {
                repeatSieCodeList.add(conSensor.getSiecode());
            }

            conSensor.setValidateStatus(SUCCESS_STATUS);
            conSensor.setMsg("");
        }
        return conSensorList;
    }

    private Map<Long, Long> struSensorConMap(Set<DaConnectorConfig> connectorSet, List<DaConfigConnectorSensorPlus> conSensorList) throws Exception {
        Map<Long, Long> sensorConMap = new HashMap<>(8);
        for (DaConfigConnectorSensorPlus conSensor : conSensorList) {
            for (DaConnectorConfig con : connectorSet) {
                if (con.getId().equals(conSensor.getConnectorId())) {
                    sensorConMap.put(conSensor.getId(), con.getId());
                    break;
                }

            }
        }
        return sensorConMap;
    }

    private Map<String, DaConfigSensor> struSieCodeMap(List<DaConfigConnectorSensorPlus> conSensorList) {
        Map<String, DaConfigSensor> sieCodeMap = new HashMap<>(8);
        List<String> sieCodeList = new ArrayList<>();
        for (DaConfigConnectorSensorPlus conSensor : conSensorList) {
            sieCodeList.add(conSensor.getSiecode());
        }
        List<DaConfigSensor> sensorsList = daConfigSensorMapper.selectBySiecodes(sieCodeList);
        if (!CollectionUtils.isEmpty(sensorsList)) {
            for (DaConfigSensor sensor : sensorsList) {
                sieCodeMap.put(sensor.getSiecode(), sensor);
            }
        }
        return sieCodeMap;
    }


    private Set<DaConnectorConfig> queryAppConnectorSet(Long applicationId, Long connectorStatus) {

        DaConfigApplication temp = new DaConfigApplication();
        temp.setId(applicationId);
        List<DaConfigApplication> applicationList = daConfigApplicationMapper.select(temp);
        DaConfigApplication application = applicationList.get(0);
        if (StringUtils.isEmpty(application)) {
            return null;
        }
        List<Long> appIds = new ArrayList<>();
        appIds.add(applicationId);
        List<DaConfigAppProvider> appProviderList = daConfigAppProviderMapper.selectByAppIds(appIds);
        if (CollectionUtils.isEmpty(appProviderList)) {
            return null;
        }
        List<Long> providerIds = struProviderIds(appProviderList);

        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(providerIds);
        if (CollectionUtils.isEmpty(proConList)) {
            return null;
        }
        List<Long> connectorIds = struConnectorIds(proConList);
        List<DaConnectorConfig> connectorList = daConfigConnectorMapper.selectConnectorsByIdsAsc(connectorIds, connectorStatus);
        if (CollectionUtils.isEmpty(connectorList)) {
            return null;
        }
        //解密密码
        for (DaConnectorConfig dcc : connectorList) {
            dcc.setPassword(StringUtils.isEmpty(dcc.getPassword()) ? "" : stringEncryptor.decrypt(dcc.getPassword().trim()));
        }
        return new HashSet<>(connectorList);
    }

    public Object importTagToSensor(ImportTagToSensorRequest importTagToSensorRequest) throws Exception {
        Map<String, Object> retMap = new HashMap<>(16);
        List<String> conNoExistTagList = new ArrayList<>();
        List<String> sieCodeExistTagList = new ArrayList<>();
        List<String> sieCodeBlankTagList = new ArrayList<>();
        List<String> tagNoExistList = new ArrayList<>();
        List<String> sieCodeMaxLength = new ArrayList<>();
        List<String> unitMaxLengthTag = new ArrayList<>();
        List<String> desMaxLengthTag = new ArrayList<>();
        List<String> repeatSensorTag = new ArrayList<>();
        int insNum = 0;


        List<DaConfigConnectorSensorPlus> conSensorList = importTagToSensorRequest.getConSensorList();
        //query app connector Set
        Set<DaConnectorConfig> connectorSet = queryAppConnectorSet(importTagToSensorRequest.getApplicationId(), null);
        if (null == connectorSet) {
            for (DaConfigConnectorSensorPlus conSensor : conSensorList) {
                conSensor.setValidateStatus(FAIL_STATUS);
                conSensor.setMsg(DA_CONFIG_CONNECTOR_NOT_EXIST);
            }

            for (DaConfigConnectorSensorPlus temp : conSensorList) {
                conNoExistTagList.add(temp.getTag());
            }
            retMap.put(CON_NO_EXIST_TAG, conNoExistTagList);
            retMap.put(SIECODE_EXIST_TAG, sieCodeExistTagList);
            retMap.put(SIECODE_BLANK_TAG, sieCodeBlankTagList);
            retMap.put(TAG_NO_EXIST_LIAT, tagNoExistList);
            retMap.put(SIECODE_MAX_LENGTH_TAG, sieCodeMaxLength);
            retMap.put(UNIT_MAX_LENGTH_TAG, unitMaxLengthTag);
            retMap.put(DES_MAX_LENGTH_TAG, desMaxLengthTag);
            retMap.put(REPEAT_SENSOR_TAG,repeatSensorTag);
            retMap.put(SUCCESS_NUM, insNum);
            return retMap;
        }
        //key=consensorId ,value = connectorId;
        Map<Long, Long> sensorConMap = struSensorConMap(connectorSet, conSensorList);

        //querySieCodeExist
        Map<String, DaConfigSensor> sieCodeMap = struSieCodeMap(conSensorList);

        //queryTagIsExist
        Map<Long, Long> tagExistMap = struTagExistMap(conSensorList);

        List<DaConfigSensor> sensorList = new ArrayList<>();

        //update status import
        List<Long> conSensorIds = new ArrayList<>();
        //IMPORT
        List<String> repeatSieCodeList = new ArrayList<>();
        for (DaConfigConnectorSensorPlus conSensor : conSensorList) {

            if (null == sensorConMap.get(conSensor.getId())) {
                conNoExistTagList.add(conSensor.getTag());
                continue;
            }
            if (StringUtils.isEmpty(conSensor.getSiecode())) {
                sieCodeBlankTagList.add(conSensor.getTag());
                continue;
            }
            if (conSensor.getSiecode().getBytes(CommonConstant.UTF8).length > SIECODE_SIZE) {
                sieCodeMaxLength.add(conSensor.getTag());
                continue;
            }
            if (repeatSieCodeList.contains(conSensor.getSiecode())) {
                sieCodeExistTagList.add(conSensor.getTag());
                continue;
            }
            if (conSensor.getUnit() != null && conSensor.getUnit().getBytes(CommonConstant.UTF8).length > UNIT_SIZE) {
                unitMaxLengthTag.add(conSensor.getTag());
                continue;
            }
            if (conSensor.getDescription() != null && conSensor.getDescription().getBytes(CommonConstant.UTF8).length > DESCRIPTION_SIZE) {
                desMaxLengthTag.add(conSensor.getTag());
                continue;
            }
            if (null != sieCodeMap.get(conSensor.getSiecode())) {
                sieCodeExistTagList.add(conSensor.getTag());
                continue;
            }
            if (StringUtils.isEmpty(tagExistMap.get(conSensor.getId())) || tagExistMap.get(conSensor.getId()).equals(DelStatus.DELETE.getType())) {
                tagNoExistList.add(conSensor.getTag());
                continue;
            }
            //验证tag是否被导入了
            DaConfigSensor sen = new DaConfigSensor();
            sen.setConnectorId(conSensor.getConnectorId());
            sen.setAppId(importTagToSensorRequest.getApplicationId());
            sen.setTag(conSensor.getTag());
            if (!CollectionUtils.isEmpty(daConfigSensorMapper.select(sen))){
                repeatSensorTag.add(conSensor.getTag());
                continue;
            }
            repeatSieCodeList.add(conSensor.getSiecode());
            DaConfigSensor sensor = new DaConfigSensor();
            sensor.setSiecode(conSensor.getSiecode());
            sensor.setTag(conSensor.getTag());
            sensor.setConnectorId(conSensor.getConnectorId());
            sensor.setAppId(importTagToSensorRequest.getApplicationId());
            sensor.setDescription(conSensor.getDescription());
            sensor.setStatus(DelStatus.IMPORTED.getType());
            sensor.setUnit(conSensor.getUnit());
            sensorList.add(sensor);
            conSensorIds.add(conSensor.getId());
        }
        //update status imported
        if (!CollectionUtils.isEmpty(conSensorIds)) {
            daConfigConnectorSensorMapper.updateStatusImportedByIds(conSensorIds);
        }


        //insert sensor
        if (!CollectionUtils.isEmpty(sensorList)) {
            insNum += daConfigSensorMapperCommon.insertList(sensorList);
        }

        // notice update event
        applicationEventPublisher.publishEvent(new DaAppUpdateEvent(importTagToSensorRequest.getApplicationId() + ""));

        retMap.put(CON_NO_EXIST_TAG, conNoExistTagList);
        retMap.put(SIECODE_EXIST_TAG, sieCodeExistTagList);
        retMap.put(SIECODE_BLANK_TAG, sieCodeBlankTagList);
        retMap.put(TAG_NO_EXIST_LIAT, tagNoExistList);
        retMap.put(SIECODE_MAX_LENGTH_TAG, sieCodeMaxLength);
        retMap.put(UNIT_MAX_LENGTH_TAG, unitMaxLengthTag);
        retMap.put(DES_MAX_LENGTH_TAG, desMaxLengthTag);
        retMap.put(REPEAT_SENSOR_TAG, repeatSensorTag);
        retMap.put(SUCCESS_NUM, insNum);

        // 成功导入之后对相关的app进行版本号升级
        Long appId = importTagToSensorRequest.getApplicationId();
        daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(appId);
        return retMap;
    }

    private Map<Long, Long> struTagExistMap(List<DaConfigConnectorSensorPlus> conSensorList) {
        Map<Long, Long> retMap = new HashMap<>(8);
        List<Long> ids = new ArrayList<>();
        for (DaConfigConnectorSensorPlus cs : conSensorList) {
            ids.add(cs.getId());
        }
        List<DaConfigConnectorSensorPlus> csList = daConfigConnectorSensorMapper.selectByIds(ids);
        for (DaConfigConnectorSensorPlus cs : csList) {
            retMap.put(cs.getId(), cs.getStatus());
        }
        return retMap;
    }


    /**
     * querySensorList
     *
     * @param querySensorListRequest
     * @return
     */
    public PageRespone querySensorList(QuerySensorListRequest querySensorListRequest) {
        PageRespone<DaConfigConnectorSensorPlus> pageRespone = new PageRespone<DaConfigConnectorSensorPlus>();

        if (null == querySensorListRequest) {
            querySensorListRequest = new QuerySensorListRequest();
        }
        PageRequest pageRequest = querySensorListRequest.getPageRequest();
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }
        //update extend appId by scope
        updateExtendAppIdsByScope(querySensorListRequest);

        if (CollectionUtils.isEmpty(querySensorListRequest.getExtendAppIds())) {
            pageRespone.setTotal(0);
            pageRespone.setPageSize(pageRequest.getPageSize());
            pageRespone.setCurrent(1);
            pageRespone.setData(new ArrayList<>());
            return pageRespone;
        }

        querySensorListRequest.setSearchContent(!StringUtils.isEmpty(querySensorListRequest.getSearchContent()) ? querySensorListRequest.getSearchContent().trim().replaceAll("/", "//").replaceAll("%","/%").replaceAll("_", "/_").replaceAll("\\?", "_").replaceAll("\\*", "%") : "");
        int total = daConfigSensorMapper.selectCountByCondition(querySensorListRequest.getAppId(), querySensorListRequest.getStatus(), querySensorListRequest.getScope(),
                querySensorListRequest.getSearchContent(), querySensorListRequest.getExtendAppIds());

        pageRespone.setTotal(total);
        pageRespone.setPageSize(pageRequest.getPageSize());
        if (total == 0) {
            pageRespone.setCurrent(1);
            pageRespone.setData(new ArrayList<>());
            return pageRespone;
        }

        int totalPage = (total % pageRequest.getPageSize()) == 0 ? total / pageRequest.getPageSize() : total / pageRequest.getPageSize() + 1;
        int current = totalPage > pageRequest.getPage() ? pageRequest.getPage() : totalPage;
        pageRespone.setCurrent(current);
        List<DaConfigSensorPlus> csList = daConfigSensorMapperCommon.selectListByCondition(querySensorListRequest.getAppId(), querySensorListRequest.getStatus(), querySensorListRequest.getScope(), querySensorListRequest.getSearchContent(), querySensorListRequest.getExtendAppIds(),
                (current - 1) * pageRequest.getPageSize(), pageRequest.getPageSize());
        pageRespone.setData(strucsList(csList,querySensorListRequest.getAppId()));
        return pageRespone;
    }

    private void updateExtendAppIdsByScope(QuerySensorListRequest querySensorListRequest) {
        if (AppScopeEnum.APP_ALL.getType().equals(querySensorListRequest.getScope()) ||
                (AppScopeEnum.APP_PUBLIC.getType().equals(querySensorListRequest.getScope()))) {
            if (AppScopeEnum.APP_PUBLIC.getType().equals(querySensorListRequest.getScope()) && !CollectionUtils.isEmpty(querySensorListRequest.getExtendAppIds())) {
                return;
            }
            DaConfigAppExtension appExt = new DaConfigAppExtension();
            appExt.setAppId(querySensorListRequest.getAppId());
            List<DaConfigAppExtension> appExtList = daConfigAppExtensionMapper.select(appExt);
            List<Long> appIds = new ArrayList<>();
            if (!CollectionUtils.isEmpty(appExtList)) {
                for (DaConfigAppExtension ce : appExtList) {
                    appIds.add(ce.getExtensionAppId());
                }
            }

            if (AppScopeEnum.APP_ALL.getType().equals(querySensorListRequest.getScope())) {
                appIds.add(querySensorListRequest.getAppId());
            }
            querySensorListRequest.setExtendAppIds(appIds);
        } else if (AppScopeEnum.APP_PRIVATE.getType().equals(querySensorListRequest.getScope())) {
            querySensorListRequest.getExtendAppIds().add(querySensorListRequest.getAppId());
        }
    }

    private List<DaConfigSensorPlus> strucsList(List<DaConfigSensorPlus> csList, Long appId) {
        if (!CollectionUtils.isEmpty(csList)) {
            Map<Long, AppInfo> appMap = daConfigApplicationSaveService.mapAppInfo();
            for (DaConfigSensorPlus cs : csList) {
                cs.setUniqueAppId(appMap.get(cs.getFfAppId()).getAppid());
                cs.setAppName(appMap.get(cs.getFfAppId()).getName());
                if(CollectionUtils.isEmpty(daAppResourceUsageMapper.queryUseResourceBySensorOrGroup(cs.getSiecode(), ObjectTypeEnum.SENSOR.getType()))){
                    cs.setUsage(UsageType.UNAVAILABLE.getType());
                }else{
                    cs.setUsage(UsageType.AVAILABLE.getType());
                }
                if(cs.getAppId().equals(appId)){
                    cs.setScope(Long.valueOf(AppScopeEnum.APP_PRIVATE.getType()));
                }else{
                    cs.setScope(Long.valueOf(AppScopeEnum.APP_PUBLIC.getType()));
                }
            }
        }
        return csList;
    }

    /**
     * 清除幽灵tag
     *
     * @param providerIds
     */
    public void clearGhostTag(List<Long> providerIds, Long apppId) {
        if (!CollectionUtils.isEmpty(providerIds)) {
            List<Long> tempList = new ArrayList<>();
            tempList.addAll(providerIds);
            List<DaConfigAppProvider> daConfigAppProviders = daConfigAppProviderMapper.selectByProIdsAndAppId(providerIds, apppId);
            if (!CollectionUtils.isEmpty(daConfigAppProviders)) {
                List<Long> providerIdList = daConfigAppProviders.stream().map(DaConfigAppProvider::getProviderId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(providerIdList)) {
                    tempList.removeAll(providerIdList);
                }
            }
            if (!CollectionUtils.isEmpty(tempList)) {
                List<DaConfigProviderConnector> daConfigProviderConnectors = daConfigProviderConnectorMapper.selectByProIds(tempList);
                if (!CollectionUtils.isEmpty(daConfigProviderConnectors)) {
                    List<Long> connectorIdList = daConfigProviderConnectors.stream().map(DaConfigProviderConnector::getConnectorId).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(connectorIdList)) {

                        //校验删除的公共应用是否可以删除
                        String deleteIds = net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.join(connectorIdList, ",");

                        String[] deleteIdStr = deleteIds.split(",");
                        String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "connector_id");
                        List<Long> connectorIdTempList = daConfigProviderConnectorMapper.selectGhostConnector(deleteIdSqlStr, apppId);
                        if (CollectionUtils.isEmpty(connectorIdTempList)) {
                            daConfigConnectorSensorMapper.batchDeleteByConnectorId(deleteIdSqlStr);
                        } else {
                            connectorIdList.removeAll(connectorIdTempList);
                            if (!CollectionUtils.isEmpty(connectorIdList)) {
                                //校验删除的公共应用是否可以删除
                                deleteIds = net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.join(connectorIdList, ",");

                                deleteIdStr = deleteIds.split(",");
                                deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "connector_id");
                                daConfigConnectorSensorMapper.batchDeleteByConnectorId(deleteIdSqlStr);
                            }
                        }
                    }
                }

            }

        }
    }

    public void clearGhostTagByConnector(List<Long> connectorIds, Long providerId) {
        String deleteIds = net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.join(connectorIds, ",");

        String[] deleteIdStr = deleteIds.split(",");
        String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "connector_id");
        List<Long> connectorIdTempList = daConfigProviderConnectorMapper.selectGhostConnectorExcludeProviderId(deleteIdSqlStr, providerId);
        if (CollectionUtils.isEmpty(connectorIdTempList)) {
            daConfigConnectorSensorMapper.batchDeleteByConnectorId(deleteIdSqlStr);
        } else {
            connectorIds.removeAll(connectorIdTempList);
            if (!CollectionUtils.isEmpty(connectorIds)) {
                //校验删除的公共应用是否可以删除
                deleteIds = net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.join(connectorIds, ",");

                deleteIdStr = deleteIds.split(",");
                deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "connector_id");
                daConfigConnectorSensorMapper.batchDeleteByConnectorId(deleteIdSqlStr);
            }
        }
    }

    public int getAllSensorNum() {
        return daConfigSensorMapper.selectSensorCounts();
    }
}
