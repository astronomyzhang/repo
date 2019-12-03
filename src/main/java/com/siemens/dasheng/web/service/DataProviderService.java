package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.AvailableStatus;
import com.siemens.dasheng.web.enums.ConnecteStatus;
import com.siemens.dasheng.web.enums.ConnectorStatus;
import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.event.DaProviderUpdateEvent;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.mapperfactory.DaConfigProviderMapperCommon;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.dto.AppInfo;
import com.siemens.dasheng.web.model.dto.DaCoreConnector;
import com.siemens.dasheng.web.model.dto.DaProviderSensorMapping;
import com.siemens.dasheng.web.model.dto.ProviderConnectorCount;
import com.siemens.dasheng.web.request.*;
import com.siemens.dasheng.web.response.ModifyProviderResponse;
import com.siemens.dasheng.web.response.QueryProviderResponse;
import com.siemens.dasheng.web.response.RemoveProviderConnectorResponse;
import com.siemens.dasheng.web.singleton.conf.ConnectorCache;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import com.siemens.dasheng.web.util.CollectionUtil;
import com.siemens.dasheng.web.util.ServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.PI_AF_APP_ID;

/**
 * @author xuxin
 * DataProviderService
 * created by xuxin on 27/11/2018
 */
@Service
public class DataProviderService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigConnectorTypeMapper daConfigConnectorTypeMapper;

    @Autowired
    private DaConfigConnectorClassMapper daConfigConnectorClassMapper;

    @Autowired
    private DaConfigDatabaseMapper daConfigDatabaseMapper;

    @Autowired
    private DaConfigCategoryMapper daConfigCategoryMapper;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DaConfigProviderMapperCommon daConfigProviderMapperCommon;

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;

    @Autowired
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private DaConfigAppProviderMapper daConfigAppProviderMapper;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DaConfigApplicationSaveService daConfigApplicationSaveService;

    @Autowired
    private DataApplicationService dataApplicationService;

    @Resource(name = "ribbonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    private static final String PROVIDER_LIST = "providerList";
    private static final String AVAIL_RATE = "availRate";
    private static final String TOTAL_NUM = "totalNum";
    private static final String CON_NAME = "conName";
    private static final String NUM = "num";
    private static final String REPEAT_NAME = "repeatName";

    private static final Long STATUS0 = 0L;
    private static final Long STATUS1 = 1L;
    private static final Long STATUS2 = 2L;
    private static final Long STATUS3 = 3L;
    private static final Long STATUS4 = 4L;
    private static final Long STATUS5 = 5L;
    private static final Long STATUS6 = 6L;
    private static final Long STATUS7 = 7L;


    public ModelMap queryConnectorFilter() throws Exception {
        ModelMap modelMap = new ModelMap();
        List<DaConfigConnectorType> daConfigConnectorTypeList = daConfigConnectorTypeMapper.selectByDataType();

        List<DaConfigConnectorClass> daConfigConnectorClassList = daConfigConnectorClassMapper.selectAllList();
        //key=connectorType;value=List<DaConfigConnectorClass>
        Map<String, List<DaConfigConnectorClass>> daTypeClissListMap = structureDaTypeClissListMap(daConfigConnectorClassList);

        List<DaConfigDatabase> daConfigDatabaseList = daConfigDatabaseMapper.selectAllList();
        //key=connectorClass;value=List<DaConfigDatabase>
        Map<String, List<DaConfigDatabase>> daClassDatabaseListMap = structureDaClassDatabaseListMap(daConfigDatabaseList);

        List<DaConfigCategory> daConfigCategoryList = daConfigCategoryMapper.selectAllList();
        //key=database;value=List<DaConfigCategory>
        Map<String, List<DaConfigCategory>> databaseCategoryListMap = structureDatabaseCategoryListMap(daConfigCategoryList);

        if (!CollectionUtils.isEmpty(daConfigConnectorTypeList)) {
            for (DaConfigConnectorType daType : daConfigConnectorTypeList) {
                if (!CollectionUtils.isEmpty(daTypeClissListMap.get(daType.getId()))) {
                    daType.setDaConfigConnectorClassList(daTypeClissListMap.get(daType.getId()));
                    for (DaConfigConnectorClass daClass : daType.getDaConfigConnectorClassList()) {
                        if (!CollectionUtils.isEmpty(daClassDatabaseListMap.get(daClass.getId()))) {
                            daClass.setDaConfigDatabaseList(daClassDatabaseListMap.get(daClass.getId()));
                            for (DaConfigDatabase daDatabase : daClass.getDaConfigDatabaseList()) {
                                if (!CollectionUtils.isEmpty(databaseCategoryListMap.get(daDatabase.getId()))) {
                                    daDatabase.setDaConfigCategoryList(databaseCategoryListMap.get(daDatabase.getId()));
                                }
                            }
                        }
                    }
                }
            }
        }
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, OPERATION_SUCCESS);
        modelMap.put(DATA, daConfigConnectorTypeList);
        return modelMap;
    }

    private Map<String, List<DaConfigCategory>> structureDatabaseCategoryListMap(List<DaConfigCategory> daConfigCategoryList) {
        Map<String, List<DaConfigCategory>> retMap = new HashMap<String, List<DaConfigCategory>>(16);
        if (CollectionUtils.isEmpty(daConfigCategoryList)) {
            return null;
        }
        for (DaConfigCategory model : daConfigCategoryList) {
            if (CollectionUtils.isEmpty(retMap.get(model.getDatabaseId()))) {
                List<DaConfigCategory> tempList = new ArrayList<DaConfigCategory>();
                tempList.add(model);
                retMap.put(model.getDatabaseId(), tempList);
                continue;
            }
            retMap.get(model.getDatabaseId()).add(model);
        }
        return retMap;
    }

    private Map<String, List<DaConfigDatabase>> structureDaClassDatabaseListMap(List<DaConfigDatabase> daConfigDatabaseList) {
        Map<String, List<DaConfigDatabase>> retMap = new HashMap<String, List<DaConfigDatabase>>(16);
        if (CollectionUtils.isEmpty(daConfigDatabaseList)) {
            return null;
        }
        for (DaConfigDatabase model : daConfigDatabaseList) {
            if (CollectionUtils.isEmpty(retMap.get(model.getClassId()))) {
                List<DaConfigDatabase> tempList = new ArrayList<DaConfigDatabase>();
                tempList.add(model);
                retMap.put(model.getClassId(), tempList);
                continue;
            }
            retMap.get(model.getClassId()).add(model);
        }
        return retMap;
    }

    private Map<String, List<DaConfigConnectorClass>> structureDaTypeClissListMap(List<DaConfigConnectorClass> daConfigConnectorClassList) {
        Map<String, List<DaConfigConnectorClass>> retMap = new HashMap<String, List<DaConfigConnectorClass>>(16);
        if (CollectionUtils.isEmpty(daConfigConnectorClassList)) {
            return null;
        }
        for (DaConfigConnectorClass model : daConfigConnectorClassList) {
            if (CollectionUtils.isEmpty(retMap.get(model.getConnectorTypeId()))) {
                List<DaConfigConnectorClass> tempList = new ArrayList<DaConfigConnectorClass>();
                tempList.add(model);
                retMap.put(model.getConnectorTypeId(), tempList);
                continue;
            }
            retMap.get(model.getConnectorTypeId()).add(model);
        }
        return retMap;
    }

    public ModelMap queryConnectorByClass(String classId) throws Exception {
        ModelMap modelMap = new ModelMap();
        List<DaConfigConnector> connectorList = daConfigConnectorMapper.selectConnectorByClass(classId);

        List<DaConfigDatabase> daConfigDatabaseList = daConfigDatabaseMapper.selectAllList();
        Map<String, String> databaseMap = structureDatabaseMap(daConfigDatabaseList);
        List<DaConfigCategory> daConfigCategoryList = daConfigCategoryMapper.selectAllList();
        Map<String, String> categoryMap = structureCategoryMap(daConfigCategoryList);

        for (DaConfigConnector con : connectorList) {
            con.setDatabaseName(databaseMap.get(con.getArchivedDatabase()));
            con.setCategoryName(categoryMap.get(con.getConnectApproach()));
        }
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, OPERATION_SUCCESS);
        modelMap.put(DATA, connectorList);
        return modelMap;
    }

    private Map<String, String> structureCategoryMap(List<DaConfigCategory> daConfigCategoryList) {
        Map<String, String> retMap = new HashMap<>(16);
        for (DaConfigCategory tmap : daConfigCategoryList) {
            retMap.put(tmap.getId(), tmap.getName());
        }
        return retMap;
    }

    private Map<String, String> structureDatabaseMap(List<DaConfigDatabase> daConfigDatabaseList) {
        Map<String, String> retMap = new HashMap<>(16);
        for (DaConfigDatabase tmap : daConfigDatabaseList) {
            retMap.put(tmap.getId(), tmap.getName());
        }
        return retMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public ModelMap saveDataProvider(DaConfigProviderRequest daConfigProviderRequest) {
        ModelMap modelMap = new ModelMap();
        //验证connectorIds是否都存在
        List<String> repeatNames = validateProviderDatasIsExist(daConfigProviderRequest.getConnectorList());
        //验证type/class是否正确
        List<String> typeErrorNames = validateTypeErrorProviderDatasIsExist(daConfigProviderRequest.getConnectorList(), daConfigProviderRequest.getConnectorType(), daConfigProviderRequest.getConnectorClass());
        if (!CollectionUtils.isEmpty(repeatNames) || !CollectionUtils.isEmpty(typeErrorNames)) {
            if (!CollectionUtils.isEmpty(repeatNames) && !CollectionUtils.isEmpty(typeErrorNames)) {
                StringBuffer repeatName = new StringBuffer();
                for (String tname : repeatNames) {
                    repeatName.append(tname + ",");
                }
                StringBuffer typeErrorName = new StringBuffer();
                for (String tname : typeErrorNames) {
                    typeErrorName.append(tname + ",");
                }
                Map map = new HashMap(8);
                map.put("typeErrorNames", typeErrorName.toString().substring(0, typeErrorName.toString().length() - 1));
                map.put("repeatName", repeatName.toString().substring(0, repeatName.toString().length() - 1));
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(ATTACHMENT_MAP, map);
                modelMap.put(MSG, VALIDATE_CONNECTOR_ERROR_INFO);
            } else if (!CollectionUtils.isEmpty(repeatNames)) {
                StringBuffer repeatName = new StringBuffer();
                for (String tname : repeatNames) {
                    repeatName.append(tname + ",");
                }
                Map map = new HashMap(8);
                map.put("repeatName", repeatName.toString().substring(0, repeatName.toString().length() - 1));
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(ATTACHMENT_MAP, map);
                modelMap.put(MSG, CONNECTOR_IS_NOT_EXIST);
            } else if (!CollectionUtils.isEmpty(typeErrorNames)) {
                StringBuffer typeErrorName = new StringBuffer();
                for (String tname : typeErrorNames) {
                    typeErrorName.append(tname + ",");
                }
                Map map = new HashMap(8);
                map.put("typeErrorNames", typeErrorName.toString().substring(0, typeErrorName.toString().length() - 1));
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(ATTACHMENT_MAP, map);
                modelMap.put(MSG, CONNECTOR_CLASS_OR_TYPE_IS_DIFFERENT);
            }
            return modelMap;
        }
        //验证激活状态下connector是否都可连接
        if(daConfigProviderRequest.getStatus().equals(ConnectorStatus.ACTIVATE.getType())){
            if(!CollectionUtils.isEmpty(daConfigProviderRequest.getConnectorList())){
                List<String> conNames = new ArrayList<>();
                for(DaConfigConnector con : daConfigProviderRequest.getConnectorList()){
                    if(con.getStatus().equals(ConnecteStatus.UNCONNECTABLE.getType())){
                        conNames.add(con.getName());
                    }
                }
                if(!CollectionUtils.isEmpty(conNames)){
                    //不可连接连接器名称，不能改状态
                    modelMap.put(IS_SUCCESS, false);
                    Map map = new HashMap(2);
                    map.put(CON_NAME, conNames);
                    map.put(NUM, conNames.size());
                    modelMap.put(ATTACHMENT_MAP, map);
                    modelMap.put(MSG, CONNECTOR_NOT_CONNECT_AND_CANT_ACTIVE);
                    return modelMap;
                }
            }
        }

        DaConfigProvider daConfigProvider = new DaConfigProvider();
        BeanUtils.copyProperties(daConfigProviderRequest, daConfigProvider);
        daConfigProvider.setId(daConfigProviderMapperCommon.selectId());
        if (checkRepeatConnector(daConfigProviderRequest, modelMap)) {
            return modelMap;
        }
        //保存provider表数据
        if (daConfigProviderMapper.insert(daConfigProvider) != 1) {
            return structureModelMap(modelMap, false, SAVE_FAIL, null);
        }
        if (!CollectionUtils.isEmpty(daConfigProviderRequest.getConnectorList())) {
            List<DaConfigProviderConnector> pcList = new ArrayList<DaConfigProviderConnector>();
            for (DaConfigConnector connector : daConfigProviderRequest.getConnectorList()) {
                DaConfigProviderConnector tmodel = new DaConfigProviderConnector();
                tmodel.setConnectorId(connector.getId());
                tmodel.setProviderId(daConfigProvider.getId());
                pcList.add(tmodel);
            }
            if (batchInsertProviderConnector(pcList)) {
                return structureModelMap(modelMap, false, SAVE_FAIL, null);
            }
        }
        return structureModelMap(modelMap, true, SAVE_SUCCESS, daConfigProvider.getId());

    }

    private boolean checkRepeatConnector(DaConfigProviderRequest daConfigProviderRequest, ModelMap modelMap) {
        if (null != daConfigProviderRequest.getConnectorList() && daConfigProviderRequest.getConnectorList().size() > 0) {
            List<Long> longList = daConfigProviderRequest.getConnectorList().stream().map(DaConfigConnector::getId).collect(Collectors.toList());
            List<DaConfigConnector> daConfigConnectors = daConfigConnectorMapper.selectConnectorsByIds(longList);
            Map<String, List<String>> stringListHashMap = new HashMap<>(10);
            if (null != daConfigConnectors && daConfigConnectors.size() > 0) {
                for (DaConfigConnector daConfigConnector : daConfigConnectors) {
                    if (null != daConfigConnector.getConnectorInfo()) {
                        List<String> strings = stringListHashMap.get(daConfigConnector.getConnectorInfo());
                        if (null != strings && strings.size() > 0) {
                            strings.add(daConfigConnector.getName());
                        } else {
                            List<String> connectorName = new ArrayList<>();
                            connectorName.add(daConfigConnector.getName());
                            stringListHashMap.put(daConfigConnector.getConnectorInfo(), connectorName);
                        }

                    }
                }
                List<List<String>> lists = new ArrayList<>();
                Boolean flag = false;
                for (Map.Entry<String, List<String>> entry : stringListHashMap.entrySet()) {
                    List<String> values = entry.getValue();
                    if (values.size() > 1) {
                        flag = true;
                        lists.add(values);
                    }
                }
                if (flag) {
                    modelMap.put(IS_SUCCESS, Boolean.TRUE);
                    modelMap.put(REPEAT_NAME, lists);
                    modelMap.put("status", STATUS4);
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> validateTypeErrorProviderDatasIsExist(List<DaConfigConnector> connectorIds, String connectorType, String connectorClass) {
        List<String> retNames = new ArrayList<String>();
        if (null == connectorIds || CollectionUtils.isEmpty(connectorIds)) {
            return retNames;
        }
        List<Long> connectIds = new ArrayList<Long>();
        for (DaConfigConnector str : connectorIds) {
            connectIds.add(str.getId());
        }
        List<DaConfigConnector> connectorList = daConfigConnectorMapper.selectConnectorsByIds(connectIds);
        if (!CollectionUtils.isEmpty(connectorList)) {
            for (DaConfigConnector temp : connectorList) {
                if (!temp.getConnectorType().equals(connectorType) || !temp.getConnectorClass().equals(connectorClass)) {
                    retNames.add(temp.getName());
                }
            }
        }
        return retNames;
    }

    private List<String> validateProviderDatasIsExist(List<DaConfigConnector> connectorIds) {
        List<String> retNames = new ArrayList<String>();
        if (null == connectorIds || CollectionUtils.isEmpty(connectorIds)) {
            return retNames;
        }
        List<Long> connectIds = new ArrayList<Long>();
        for (DaConfigConnector str : connectorIds) {
            connectIds.add(str.getId());
        }
        List<DaConfigConnector> connectorList = daConfigConnectorMapper.selectConnectorsByIds(connectIds);
        if (!CollectionUtils.isEmpty(connectorList)) {
            List<Long> tempIds = new ArrayList<Long>();
            for (DaConfigConnector con : connectorList) {
                tempIds.add(con.getId());
            }
            for (DaConfigConnector str : connectorIds) {
                if (!tempIds.contains(str.getId())) {
                    retNames.add(str.getName());
                }
            }
        } else {
            for (DaConfigConnector str : connectorIds) {
                retNames.add(str.getName());
            }
        }
        return retNames;
    }

    private Boolean batchInsertProviderConnector(List<DaConfigProviderConnector> pcList) {
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            return daConfigProviderConnectorMapper.batchInsertPG(pcList) < 1;
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            return daConfigProviderConnectorMapper.batchInsertOR(pcList) < 1;
        }
        return true;
    }

    private ModelMap structureModelMap(ModelMap modelMap, Boolean isSuccess, String msg, Object data) {
        modelMap.put(IS_SUCCESS, isSuccess);
        modelMap.put(MSG, msg);
        modelMap.put(DATA, data);
        return modelMap;
    }

    public QueryProviderResponse queryProviderList(QueryProviderRequest queryProviderRequest) throws Exception {

        QueryProviderResponse response = new QueryProviderResponse();
        queryProviderRequest.setSearchContent(queryProviderRequest.getSearchContent() != null ? queryProviderRequest.getSearchContent().trim().replaceAll("/", "//").replaceAll("%", "/%").replaceAll("_", "/_") : null);
        List<DaConfigProvider> providerList = daConfigProviderMapper.queryListByCondition(queryProviderRequest);
        //查询connectorClass name
        List<DaConfigConnectorClass> daConfigConnectorList = daConfigConnectorClassMapper.selectAllList();
        Map<String, String> conClassIdNameMap = structureConClassMap(daConfigConnectorList);
        if (!CollectionUtils.isEmpty(providerList)) {
            //添加可利用率
            List<Long> proIds = struProIds(providerList);
            List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
            //key=proId ,value=conIdList
            Map<Long, Set<Long>> proConSetMap = struProConSetMap(proConList);
            //key=proId,value=avail_rate
            Map<Long, String[]> proAvailMap = struProAvailMap(proConSetMap, new HashMap<>(16));
            for (DaConfigProvider provider : providerList) {
                provider.setConnectorClassName(conClassIdNameMap.get(provider.getConnectorClass()));
                provider.setAvailabilityRate((proAvailMap.get(provider.getId()) == null) ? "0.00" : proAvailMap.get(provider.getId())[0]);
                provider.setAvailability((proAvailMap.get(provider.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(provider.getId())[1]));
            }

            //过滤availStatus
            if (null != queryProviderRequest.getAvailability()) {
                List<DaConfigProvider> newProciderList = new ArrayList<>();
                for (DaConfigProvider provider : providerList) {
                    if (provider.getAvailability().equals(queryProviderRequest.getAvailability()) && ConnectorStatus.ACTIVATE.getType().equals(provider.getStatus())) {
                        newProciderList.add(provider);
                    }
                }
                response.setStatus(0L);
                response.setData(newProciderList);
                return response;
            }
        }

        response.setStatus(0L);
        response.setData(providerList);
        return response;
    }

    public Map<Long, String[]> struProAvailMap(Map<Long, Set<Long>> proConSetMap, Map<String, Object> availRateMap) {
        try {
            Map<Long, String[]> retMap = new HashMap<>(4);
            Set<Integer> availConIdSet = getAllAvailConnectors();

            if (proConSetMap.isEmpty()) {
                return retMap;
            }
            Double sumAvailNum = 0d;
            Double sumTotalNum = 0d;
            for (Map.Entry<Long, Set<Long>> proMap : proConSetMap.entrySet()) {
                Double availNum = 0d;
                Double totalNum = 0d;

                for (Long conId : proMap.getValue()) {
                    if (availConIdSet.contains(Integer.valueOf(conId.toString()))) {
                        availNum++;
                        sumAvailNum++;
                    }
                    totalNum++;
                    sumTotalNum++;

                }
                String[] value = new String[2];

                value[0] = String.format("%.2f", availNum / totalNum);
                if (availNum == 0) {
                    value[1] = AvailableStatus.UNAVAILABLE.getType().toString();
                } else {
                    value[1] = (availNum.equals(totalNum)) ? AvailableStatus.AVAILABLE.getType().toString() : AvailableStatus.PARTLY_AVAILABLE.getType().toString();
                }
                retMap.put(proMap.getKey(), value);

            }
            if (sumAvailNum == 0) {
                availRateMap.put(AVAIL_RATE, AvailableStatus.UNAVAILABLE.getType());
            } else {
                availRateMap.put(AVAIL_RATE, (sumAvailNum.equals(sumTotalNum)) ? AvailableStatus.AVAILABLE.getType() : AvailableStatus.PARTLY_AVAILABLE.getType());
            }
            return retMap;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    public Map<Long, Set<Long>> struProConSetMap(List<DaConfigProviderConnector> proConList) {
        Map<Long, Set<Long>> retMap = new HashMap<>(4);
        if (CollectionUtils.isEmpty(proConList)) {
            return retMap;
        }
        for (DaConfigProviderConnector proCon : proConList) {
            if (CollectionUtils.isEmpty(retMap.get(proCon.getProviderId()))) {
                Set<Long> conIdSet = new HashSet<>();
                conIdSet.add(proCon.getConnectorId());
                retMap.put(proCon.getProviderId(), conIdSet);
                continue;
            }
            retMap.get(proCon.getProviderId()).add(proCon.getConnectorId());
        }
        return retMap;
    }

    public List<Long> struProIds(List<DaConfigProvider> providerList) {
        List<Long> proIds = new ArrayList<>();
        for (DaConfigProvider pro : providerList) {
            proIds.add(pro.getId());
        }
        return proIds;
    }

    public Set<Integer> getAllAvailConnectors() {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        HttpEntity<MultiValueMap<String, Object>> entity =
                new HttpEntity<>(parameters, headers);
        DataHolder modelMap = null;
        String url = "http://ofm-da-routing/ofm-da-routing/v2/daRouting/allAvailableConnectors";

        ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
        if (null != stringResponseEntity) {
            modelMap = JSON.parseObject(stringResponseEntity.getBody(), DataHolder.class);
        }
        logger.info("getAllAvailConnectors");
        return modelMap == null ? new HashSet<>() : modelMap.getData();
    }

    public Object queryApplicationProvider(QueryApplicationProviderRequest queryApplicationProviderRequest) {
        Map<String, Object> retMap = new HashMap<>(8);
        List<DaConfigProvider> providerList = daConfigProviderMapper.queryApplicationProvider(queryApplicationProviderRequest.getApplicationId());
        //整体可用度
        retMap.put(AVAIL_RATE, AvailableStatus.UNAVAILABLE.getType());
        if (!CollectionUtils.isEmpty(providerList)) {
            //添加可利用率
            List<Long> proIds = struProIds(providerList);
            List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
            //key=proId ,value=conIdList
            Map<Long, Set<Long>> proConSetMap = struProConSetMap(proConList);
            //key=proId,value=avail_rate
            Map<Long, String[]> proAvailMap = struProAvailMap(proConSetMap, retMap);
            for (DaConfigProvider provider : providerList) {
                provider.setAvailabilityRate((proAvailMap.get(provider.getId()) == null) ? "0.00" : proAvailMap.get(provider.getId())[0]);
                provider.setAvailability((proAvailMap.get(provider.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(provider.getId())[1]));
            }
        }
        retMap.put(TOTAL_NUM, CollectionUtils.isEmpty(providerList) ? 0 : providerList.size());
        retMap.put(PROVIDER_LIST, providerList);

        //特殊处理部分可用
        List<ProviderConnectorCount> providerConnectorCounts = daConfigAppProviderMapper.queryProviderConnectorCount(queryApplicationProviderRequest.getApplicationId());
        if (!CollectionUtils.isEmpty(providerConnectorCounts)) {
            if (AvailableStatus.AVAILABLE.getType().equals(Long.valueOf(retMap.get(AVAIL_RATE).toString()))) {
                retMap.put(AVAIL_RATE, AvailableStatus.PARTLY_AVAILABLE.getType());
            }
        }
        return retMap;
    }

    private static class DataHolder {
        Set<Integer> data;

        public Set<Integer> getData() {
            return data;
        }

        public void setData(Set<Integer> data) {
            this.data = data;
        }
    }

    private Map<String, String> structureConClassMap(List<DaConfigConnectorClass> daConfigConnectorList) {
        Map<String, String> tmap = new HashMap<>(16);
        if (CollectionUtils.isEmpty(daConfigConnectorList)) {
            return tmap;
        }
        for (DaConfigConnectorClass conClass : daConfigConnectorList) {
            tmap.put(conClass.getId(), conClass.getName());
        }
        return tmap;
    }


    /**
     * Get connector list of pi af provider
     *
     * @return
     */
    public List<DaConfigConnector> queryConnectorListOfPiAf() {


        List<DaConfigConnector> returnConnectorList = daConfigApplicationMapper.selecyConnectedDaConfigConnectorListByAppId(PI_AF_APP_ID);

        if (null != returnConnectorList && returnConnectorList.size() > 0) {

            Set<String> connectorIdSet = new HashSet<>();

            Iterator<DaConfigConnector> iterator = returnConnectorList.iterator();

            while (iterator.hasNext()) {
                DaConfigConnector daConfigConnector = iterator.next();
                if (connectorIdSet.contains(daConfigConnector.getId() + "")) {
                    iterator.remove();
                } else {
                    connectorIdSet.add(daConfigConnector.getId() + "");
                }
            }

        } else {
            return new ArrayList<>();
        }

        return returnConnectorList;

    }

    public QueryProviderResponse queryConnectorListByProviderId(Long providerId) {
        QueryProviderResponse queryProviderResponse = new QueryProviderResponse();
        DaConfigProvider daConfigProvider = daConfigProviderMapper.queryById(providerId);
        if (null == daConfigProvider) {
            queryProviderResponse.setStatus(7L);
            return queryProviderResponse;
        }
        List<DaConfigConnectorClass> daConfigConnectorClassList = daConfigConnectorClassMapper.selectAllList();
        //key=connectorType;value=List<DaConfigConnectorClass>
        Map<String, String> daTypeClissMap = structureConnectorClissMap(daConfigConnectorClassList);
        daConfigProvider.setConnectorClassName(daTypeClissMap.get(daConfigProvider.getConnectorClass()));

        List<DaConfigConnector> connectorList = daConfigConnectorMapper.queryConnectorListByProviderId(providerId);
        //插入名称
        List<DaConfigDatabase> daConfigDatabaseList = daConfigDatabaseMapper.selectAllList();
        Map<String, String> databaseMap = structureDatabaseMap(daConfigDatabaseList);
        List<DaConfigCategory> daConfigCategoryList = daConfigCategoryMapper.selectAllList();
        Map<String, String> categoryMap = structureCategoryMap(daConfigCategoryList);
        for (DaConfigConnector con : connectorList) {
            con.setDatabaseName(databaseMap.get(con.getArchivedDatabase()));
            con.setCategoryName(categoryMap.get(con.getConnectApproach()));
        }
        daConfigProvider.setDaConfigConnectorList(connectorList);
        queryProviderResponse.setStatus(0L);
        queryProviderResponse.setDaConfigProvider(daConfigProvider);
        return queryProviderResponse;
    }

    private Map<String, String> structureConnectorClissMap(List<DaConfigConnectorClass> daConfigConnectorClassList) {
        Map<String, String> retMap = new HashMap<>(16);
        for (DaConfigConnectorClass temp : daConfigConnectorClassList) {
            retMap.put(temp.getId(), temp.getName());
        }
        return retMap;
    }

    private Integer getRandomNum(int max, int min) {
        SecureRandom random = new SecureRandom();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    public Long validateConnectorsStatus(ValidateConnectorsStatusRequest validateConnectorsStatusRequest) throws Exception {
        if (!validateConnectorsStatusRequest.getStatus().equals(ConnectorStatus.ACTIVATE.getType())) {
            return 1L;
        }
        if (CollectionUtils.isEmpty(validateConnectorsStatusRequest.getDaConfigConnectorList())) {
            return 2L;
        }
        for (DaConfigConnector con : validateConnectorsStatusRequest.getDaConfigConnectorList()) {
            if ((null == ConnectorCache.getInstance().get(con.getId())) || !ConnectorCache.getInstance().get(con.getId())) {
                return 3L;
            }
        }
        //TODO 验证能否激活

        return 0L;
    }

    @Transactional(rollbackFor = Exception.class)
    public ModifyProviderResponse modifyProvider(ModifyProviderRequest modifyProviderRequest) {
        ModifyProviderResponse modifyProviderResponse = new ModifyProviderResponse();
        //验证provider是否存在
        DaConfigProvider providerModel = daConfigProviderMapper.queryById(modifyProviderRequest.getId());
        if (null == providerModel) {
            modifyProviderResponse.setStatus(7L);
            return modifyProviderResponse;
        }
        //验证connectorIds是否都存在
        List<String> noExistNames = validateProviderDatasIsExist(modifyProviderRequest.getConnectorList());
        if(!CollectionUtils.isEmpty(noExistNames)){
            StringBuffer repeatName = new StringBuffer();
            for (String tname : noExistNames) {
                repeatName.append(tname + ",");
            }
            modifyProviderResponse.setRepeatNames(repeatName.toString().substring(0, repeatName.toString().length() - 1));
            modifyProviderResponse.setStatus(9L);
            return modifyProviderResponse;
        }

        //查询原来的connectorIds
        List<DaConfigConnector> connectorList = daConfigConnectorMapper.queryConnectorListByProviderId(modifyProviderRequest.getId());
        List<Long> origConnectorIds = struConnectorIds(connectorList);
        List<Long> newConnectorIds = struConnectorIds(modifyProviderRequest.getConnectorList());
        List<Long> reduceConnectorIds = CollectionUtil.getReduceaListThanbList(newConnectorIds,origConnectorIds);
        List<Long> addConnectorIds = CollectionUtil.getAddaListThanbList(newConnectorIds,origConnectorIds);

        //只修改基本信息
        if(CollectionUtils.isEmpty(reduceConnectorIds) && CollectionUtils.isEmpty(addConnectorIds) && modifyProviderRequest.getStatus().equals(providerModel.getStatus())){
            DaConfigProvider daConfigProvider = new DaConfigProvider();
            daConfigProvider.setId(modifyProviderRequest.getId());
            daConfigProvider.setName(modifyProviderRequest.getName());
            daConfigProvider.setDescription(modifyProviderRequest.getDescription());
            daConfigProviderMapper.updateByPrimaryKeySelective(daConfigProvider);
            modifyProviderResponse.setStatus(STATUS0);
            return modifyProviderResponse;
        }
        //查询provider被引用的app
        List<Long> appIds = queryAppIdsByProviderId(modifyProviderRequest.getId());

        //验证状态未激活到激活
        if(modifyProviderRequest.getStatus().equals(ConnectorStatus.ACTIVATE.getType())){
            if(!CollectionUtils.isEmpty(newConnectorIds)){
                List<DaConfigConnector> conList = daConfigConnectorMapper.selectConnectorsByIds(newConnectorIds);
                if(!CollectionUtils.isEmpty(conList)){
                    List<String> conNames = new ArrayList<>();
                    for(DaConfigConnector con : conList){
                        if(con.getStatus().equals(ConnecteStatus.UNCONNECTABLE.getType())){
                            conNames.add(con.getName());
                        }
                    }
                    if(!CollectionUtils.isEmpty(conNames)){
                        //不可连接连接器名称，不能改状态
                        modifyProviderResponse.setStatus(STATUS1);
                        modifyProviderResponse.setNames(conNames);
                        modifyProviderResponse.setNums(conNames.size());
                        return modifyProviderResponse;
                    }
                }
            }
        }
        //验证状态激活到未激活
        if(modifyProviderRequest.getStatus().equals(ConnectorStatus.INACTIVE.getType()) && providerModel.getStatus().equals(ConnectorStatus.ACTIVATE.getType())){
            if(!CollectionUtils.isEmpty(appIds)){
                //被app引用，不能改状态
                List<DaConfigApplication> applicationList = daConfigApplicationMapper.selectByAppIds(appIds);
                List<String> names = new ArrayList<>();
                Map<Long, AppInfo> appMap = daConfigApplicationSaveService.mapAppInfo();
                for(DaConfigApplication app : applicationList){
                    names.add(appMap.get(app.getFfAppId()).getName());
                }
                modifyProviderResponse.setStatus(STATUS2);
                modifyProviderResponse.setNames(names);
                return modifyProviderResponse;
            }
        }

        if(!CollectionUtils.isEmpty(reduceConnectorIds)){
            if(!CollectionUtils.isEmpty(appIds)){
                //查询是否被sensor导入
                List<DaConfigSensor> sensorList = daConfigSensorMapper.selectByConnectorIds(reduceConnectorIds,appIds);
                if(!CollectionUtils.isEmpty(sensorList)){

                    Map<Long,List<DaConfigSensor>> conSensorMap = struConSensorMap(sensorList);
                    //connector被导入sensor，不能移除
                    Set<Long> conIds = new HashSet<>();
                    for(DaConfigSensor sen : sensorList){
                        conIds.add(sen.getConnectorId());
                    }
                    List<DaConfigConnector> conList = daConfigConnectorMapper.selectConnectorsByIds(new ArrayList<>(conIds));
                    //key=connectorId,value=connector
                    Map<Long,DaConfigConnector> conMap = new HashMap<>(4);
                    for(DaConfigConnector con : conList){
                        conMap.put(con.getId(),con);
                    }
                    //key=appId,value=appName;
                    Map<Long,String> appNameMap = queryAppNameMap(sensorList);
                    List<RemoveProviderConnectorResponse> resList = new ArrayList<>();
                    for(Map.Entry<Long,List<DaConfigSensor>> map : conSensorMap.entrySet()){
                        RemoveProviderConnectorResponse res = new RemoveProviderConnectorResponse();
                        res.setConnectorName(conMap.get(map.getKey()).getName());
                        res.setAppNames(strAppNames(appNameMap,map.getValue()));
                        res.setNums(map.getValue().size());
                        resList.add(res);
                    }

                    modifyProviderResponse.setResponsesList(resList);
                    modifyProviderResponse.setStatus(STATUS3);
                    return modifyProviderResponse;
                }
            }
            //未被导入去除幽灵tag
            dataApplicationService.clearGhostTagByConnector(reduceConnectorIds,modifyProviderRequest.getId());
        }

        if(!CollectionUtils.isEmpty(addConnectorIds)){
            //验证provider下是否都重复
            Map<String,List<DaConfigConnector>> conInfoConnectListMap = struConInfoConListMap(modifyProviderRequest.getConnectorList());
            List<String> repeatNames = new ArrayList<>();
            for(Map.Entry<String,List<DaConfigConnector>> tempMap : conInfoConnectListMap.entrySet()){
                if(tempMap.getValue().size()>1){
                    StringBuilder str = new StringBuilder();
                    for(DaConfigConnector tcon : tempMap.getValue()){
                        str.append(tcon.getName()).append(", ");
                    }
                    repeatNames.add(str.substring(0,str.length()-2));
                }
            }
            if(!CollectionUtils.isEmpty(repeatNames)){
                //provider下重复
                modifyProviderResponse.setNames(repeatNames);
                modifyProviderResponse.setStatus(STATUS4);
                return modifyProviderResponse;
            }
            if(!CollectionUtils.isEmpty(appIds)){
                List<Long> proIdList = new ArrayList<>();
                proIdList.add(modifyProviderRequest.getId());
                List<DaConfigAppProvider> appProviderList = daConfigAppProviderMapper.selectByProIds(proIdList);
                Set<Long> tempAppIds = struAppIds(appProviderList);
                List<String> repeatAppNames = new ArrayList<>();
                Map<Long, AppInfo> appMap = daConfigApplicationSaveService.mapAppInfo();
                for(Long appId : tempAppIds){
                    DaConfigApplication application = daConfigApplicationMapper.selectAppById(appId);
                    //查询app下所有connector,排除自身connector
                    DaConfigAppProvider ap = new DaConfigAppProvider();
                    ap.setAppId(appId);
                    List<DaConfigAppProvider> appProList = daConfigAppProviderMapper.select(ap);

                    label :for(Long tconId : addConnectorIds){
                        DaConfigConnector tconnector = daConfigConnectorMapper.selectById(tconId);

                        StringBuilder stringBuilder = new StringBuilder();
                        for(DaConfigAppProvider apt : appProList){
                            if(apt.getProviderId().equals(modifyProviderRequest.getId())){
                                continue;
                            }
                            DaConfigProviderConnector temp = new DaConfigProviderConnector();
                            temp.setProviderId(apt.getProviderId());
                            List<DaConfigProviderConnector> tempProConList = daConfigProviderConnectorMapper.select(temp);
                            if(!CollectionUtils.isEmpty(tempProConList)){
                                List<Long> connectorIds = new ArrayList<>();
                                for(DaConfigProviderConnector pc : tempProConList){
                                    connectorIds.add(pc.getConnectorId());
                                }
                                List<DaConfigConnector> connList = daConfigConnectorMapper.selectConnectorsByIds(connectorIds);
                                //判断app下connector全都不同
                                for(DaConfigConnector tempCon : connList){
                                    if(tconnector.getConnectorInfo().equals(tempCon.getConnectorInfo())){
                                        stringBuilder.append("(")
                                                .append(tconnector.getName()).append(" in ")
                                                .append(providerModel.getName()).append(", ")
                                                .append(tempCon.getName()).append(" in ")
                                                .append(daConfigProviderMapper.selectByPrimaryKey(apt.getProviderId()).getName()).append(") in ")
                                                .append(null == appMap.get(application.getFfAppId()) ? "" : appMap.get(application.getFfAppId()).getName());
                                        repeatAppNames.add(stringBuilder.toString());
                                        continue label;
                                    }
                                }
                            }
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(repeatAppNames)){
                    modifyProviderResponse.setNames(repeatAppNames);
                    modifyProviderResponse.setStatus(STATUS5);
                    return modifyProviderResponse;
                }
            }
        }

        //修改provider
        DaConfigProvider daConfigProvider = new DaConfigProvider();
        BeanUtils.copyProperties(modifyProviderRequest, daConfigProvider);
        /*if (checkRepeatConnector(modifyProviderRequest, modifyProviderResponse)) {
            return modifyProviderResponse;
        }*/
        if (daConfigProviderMapper.updateByPrimaryKeySelective(daConfigProvider) < 1) {
            modifyProviderResponse.setStatus(6L);
            return modifyProviderResponse;
        }
        //删除原来关联
        daConfigProviderConnectorMapper.deleteByProviderId(modifyProviderRequest.getId());
        //添加新关联
        if (!CollectionUtils.isEmpty(modifyProviderRequest.getConnectorList())) {
            List<DaConfigProviderConnector> pcList = new ArrayList<DaConfigProviderConnector>();
            for (DaConfigConnector connector : modifyProviderRequest.getConnectorList()) {
                DaConfigProviderConnector tmodel = new DaConfigProviderConnector();
                tmodel.setConnectorId(connector.getId());
                tmodel.setProviderId(daConfigProvider.getId());
                pcList.add(tmodel);
            }
            if (batchInsertProviderConnector(pcList)) {
                modifyProviderResponse.setStatus(6L);
                return modifyProviderResponse;
            }
        }
        modifyProviderResponse.setStatus(0L);

        // refresh daProvider's sensormapping_update_time colum
        applicationEventPublisher.publishEvent(new DaProviderUpdateEvent(modifyProviderRequest.getId() + ""));

        return modifyProviderResponse;
    }

    private List<String> strAppNames(Map<Long, String> appNameMap, List<DaConfigSensor> sensorList) {
        List<String> retList = new ArrayList<>();
        Set<Long> appIds = new HashSet<>();
        for(DaConfigSensor sen : sensorList){
            appIds.add(sen.getAppId());
        }
        for(Long appId : appIds){
            retList.add(appNameMap.get(appId));
        }
        return retList;
    }

    private Map<Long, String> queryAppNameMap(List<DaConfigSensor> sensorList) {
        Map<Long,String> retMap = new HashMap<>(4);
        Set<Long> appIds = new HashSet<>();
        for(DaConfigSensor sen : sensorList){
            appIds.add(sen.getAppId());
        }
        List<DaConfigApplication> appList = daConfigApplicationMapper.selectByAppIds(new ArrayList<>(appIds));

        Map<Long, AppInfo> appMap = daConfigApplicationSaveService.mapAppInfo();
        for (DaConfigApplication app : appList) {
            retMap.put(app.getId(),appMap.get(app.getFfAppId()).getName());
        }
        return retMap;
    }

    private Map<Long, List<DaConfigSensor>> struConSensorMap(List<DaConfigSensor> sensorList) {
        Map<Long, List<DaConfigSensor>> retMap = new HashMap<>(4);
        for(DaConfigSensor sensor : sensorList){
            if(CollectionUtils.isEmpty(retMap.get(sensor.getConnectorId()))){
                List<DaConfigSensor> tempList = new ArrayList<>();
                tempList.add(sensor);
                retMap.put(sensor.getConnectorId(),tempList);
                continue;
            }
            retMap.get(sensor.getConnectorId()).add(sensor);
        }
        return retMap;
    }

    private Set<Long> struAppIds(List<DaConfigAppProvider> appProviderList) {
        Set<Long> appIds = new HashSet<>(4);
        for(DaConfigAppProvider ap : appProviderList){
            appIds.add(ap.getAppId());
        }
        return appIds;
    }

    private Map<String, List<DaConfigConnector>> struConInfoConListMap(List<DaConfigConnector> connectorList) {
        List<Long> longList = connectorList.stream().map(DaConfigConnector :: getId).collect(Collectors.toList());
        List<DaConfigConnector> daConfigConnectors = daConfigConnectorMapper.selectConnectorsByIds(longList);
        Map<String,List<DaConfigConnector>> retMap= daConfigConnectors.stream().collect(Collectors.groupingBy(DaConfigConnector :: getConnectorInfo));
        return retMap;
    }

    private List<Long> queryAppIdsByProviderId(Long providerId) {
        DaConfigAppProvider appProvider = new DaConfigAppProvider();
        appProvider.setProviderId(providerId);
        List<DaConfigAppProvider> apList = daConfigAppProviderMapper.select(appProvider);
        List<Long> appIds = new ArrayList<>();
        if(CollectionUtils.isEmpty(apList)){
            return appIds;
        }
        for(DaConfigAppProvider ap : apList){
            appIds.add(ap.getAppId());
        }
        return appIds;
    }

    private List<Long> struConnectorIds(List<DaConfigConnector> connectorList) {
        List<Long> retList = new ArrayList<>();
        if(CollectionUtils.isEmpty(connectorList)){
            return retList;
        }
        for(DaConfigConnector con : connectorList){
            retList.add(con.getId());
        }
        return retList;
    }


    private void structErrorNameMsg(ModifyProviderResponse modifyProviderResponse, List<String> repeatNames, List<String> typeErrorNames) {
        if (!CollectionUtils.isEmpty(repeatNames) && !CollectionUtils.isEmpty(typeErrorNames)) {
            StringBuffer repeatName = new StringBuffer();
            for (String tname : repeatNames) {
                repeatName.append(tname + ",");
            }
            StringBuffer typeErrorName = new StringBuffer();
            for (String tname : typeErrorNames) {
                typeErrorName.append(tname + ",");
            }
            Map map = new HashMap(8);
            map.put("typeErrorNames", typeErrorName.toString().substring(0, typeErrorName.toString().length() - 1));
            map.put("repeatName", repeatName.toString().substring(0, repeatName.toString().length() - 1));
            modifyProviderResponse.setErrorMap(map);
            modifyProviderResponse.setStatus(1L);
        } else if (!CollectionUtils.isEmpty(repeatNames)) {
            StringBuffer repeatName = new StringBuffer();
            for (String tname : repeatNames) {
                repeatName.append(tname + ",");
            }
            Map map = new HashMap(8);
            map.put("repeatName", repeatName.toString().substring(0, repeatName.toString().length() - 1));
            modifyProviderResponse.setErrorMap(map);
            modifyProviderResponse.setStatus(2L);
        } else if (!CollectionUtils.isEmpty(typeErrorNames)) {
            StringBuffer typeErrorName = new StringBuffer();
            for (String tname : typeErrorNames) {
                typeErrorName.append(tname + ",");
            }
            Map map = new HashMap(8);
            map.put("typeErrorNames", typeErrorName.toString().substring(0, typeErrorName.toString().length() - 1));
            modifyProviderResponse.setErrorMap(map);
            modifyProviderResponse.setStatus(3L);
        }
    }

    private Map<Long, Set<DaCoreConnector>> structProConSetMap(List<DaCoreConnector> proConList, int providerSize) {
        Map<Long, Set<DaCoreConnector>> retMap = new HashMap<>(providerSize);
        if (CollectionUtils.isEmpty(proConList)) {
            return retMap;
        }
        for (DaCoreConnector temp : proConList) {
            if (CollectionUtils.isEmpty(retMap.get(Long.valueOf(temp.getProviderId())))) {
                Set<DaCoreConnector> connectSet = new HashSet<>();
                connectSet.add(temp);
                retMap.put(Long.valueOf(temp.getProviderId()), connectSet);
                continue;
            }
            retMap.get(Long.valueOf(temp.getProviderId())).add(temp);
        }
        return retMap;
    }


    /**
     * getMockSensorMappingByProviderId
     *
     * @param providerId
     * @return
     */
    public List<DaProviderSensorMapping> getMockSensorMappingByProviderId(long providerId) {

        return daConfigProviderMapper.getMockSensorMappingByProviderId(providerId);

    }


    @Transactional(rollbackFor = Exception.class)
    public Long deleteProvider(Long providerId) throws Exception {
        DaConfigProvider provider = daConfigProviderMapper.queryById(providerId);
        if (null == provider) {
            return 1L;
        }
        //validate provider status
        if (ConnectorStatus.ACTIVATE.getType().equals(provider.getStatus())) {
            return 2L;
        }
        DaConfigAppProvider appPro = new DaConfigAppProvider();
        appPro.setProviderId(providerId);
        List<DaConfigAppProvider> appProList = daConfigAppProviderMapper.select(appPro);
        if (!CollectionUtils.isEmpty(appProList)) {
            return 3L;
        }
        //delete provider and connector relation
        daConfigProviderConnectorMapper.deleteByProviderId(providerId);
        daConfigProviderMapper.deleteByPrimaryKey(providerId);
        return 0L;
    }

    /**
     * 获取提供者
     *
     * @param queryProviderRequest
     * @return
     * @throws Exception
     */
    public QueryProviderResponse multiQueryProviderList(QueryProviderRequest queryProviderRequest) throws Exception {

        QueryProviderResponse response = new QueryProviderResponse();
        if (queryProviderRequest == null) {
            return response;
        }
        String connectorType = queryProviderRequest.getConnectorType();
        String connectorClass = queryProviderRequest.getConnectorClass();
        if (!StringUtils.isEmpty(connectorClass)) {
            String[] connectorClassStr = connectorClass.split(",");
            List<String> connectorList = Arrays.asList(connectorClassStr);
            String ccIds = "'" + StringUtils.join(connectorList, "','") + "'";
            String[] clStr = ccIds.split(",");
            connectorClass = ServiceUtil.mergeStrForIn(clStr, "connector_class");
        } else {
            if (!StringUtils.isEmpty(connectorType)) {
                connectorClass = "connector_class in('')";
            }
        }
        List<DaConfigProvider> providerList = daConfigProviderMapper.multiQueryListByCondition(connectorType, connectorClass);
       //查询connectorClass name
        List<DaConfigConnectorClass> daConfigConnectorList = daConfigConnectorClassMapper.selectAllList();
        Map<String, String> conClassIdNameMap = structureConClassMap(daConfigConnectorList);
        if (!CollectionUtils.isEmpty(providerList)) {
            //添加可利用率
            List<Long> proIds = struProIds(providerList);
            List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
            //key=proId ,value=conIdList
            Map<Long, Set<Long>> proConSetMap = struProConSetMap(proConList);
            //key=proId,value=avail_rate
            Map<Long, String[]> proAvailMap = struProAvailMap(proConSetMap, new HashMap<>(64));
            for (DaConfigProvider provider : providerList) {
                provider.setConnectorClassName(conClassIdNameMap.get(provider.getConnectorClass()));
                provider.setAvailabilityRate((proAvailMap.get(provider.getId()) == null) ? "0.00" : proAvailMap.get(provider.getId())[0]);
                provider.setAvailability((proAvailMap.get(provider.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(provider.getId())[1]));
            }
        }
        response.setStatus(0L);
        response.setData(providerList);
        return response;
    }

    /**
     * @param applicationProviderRequest
     * @return result
     */
    public List<DaConfigProvider> assembleProConMap(QueryApplicationProviderRequest applicationProviderRequest) {
        List<DaConfigProvider> providerList = daConfigProviderMapper.queryApplicationProvider(applicationProviderRequest.getApplicationId());
        if (CollectionUtils.isEmpty(providerList)) {
            return null;
        }
        List<Long> providerIds = this.struProIds(providerList);
        List<DaConfigConnectorPlus> dataConnectorList = daConfigConnectorMapper.queryConnectorListByProviderIds(providerIds);
        if (CollectionUtils.isEmpty(dataConnectorList)) {
            return null;
        }
        for (DaConfigProvider provider : providerList) {
            List<DaConfigConnector> tempList = new ArrayList<>();
            for (DaConfigConnectorPlus conn : dataConnectorList) {
                if (provider.getId().equals(conn.getProviderId())) {
                    DaConfigConnector connector = new DaConfigConnector();
                    connector.setId(conn.getId());
                    connector.setName(conn.getName());
                    connector.setArchivedDatabase(conn.getArchivedDatabase());
                    tempList.add(connector);
                }
            }
            provider.setDaConfigConnectorList(tempList);
        }
        return providerList;
    }
}
