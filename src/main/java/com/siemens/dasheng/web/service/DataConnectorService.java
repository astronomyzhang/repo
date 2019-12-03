package com.siemens.dasheng.web.service;

import javax.annotation.Resource;

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.*;
import com.siemens.dasheng.web.generalmodel.dataconnector.OpcUaEventsQueryRequest;
import com.siemens.dasheng.web.mapper.DaConfigApplicationMapper;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigProviderConnectorMapper;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.mapperfactory.DaConfigConnectorMapperCommon;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.DaConfigProvider;
import com.siemens.dasheng.web.model.DaConfigProviderConnector;
import com.siemens.dasheng.web.model.DaConfigSensor;
import com.siemens.dasheng.web.model.dto.DaConnectorStatus;
import com.siemens.dasheng.web.model.dto.DaMonitorConnector;
import com.siemens.dasheng.web.model.dto.HttpDataHolder;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.util.RedisLock;
import com.siemens.ofm.opcua.model.UaEvent;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.PI_AF_APP_ID;

/**
 * @author xuxin
 * DataConnectorService
 * created by xuxin on 15/11/2018
 */
@Service
public class DataConnectorService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int SQLDAS_LINUX = 1;

    private static final int SQLDAS_WINDOWS = 2;

    private static final int OPENPLANT_LINUX = 3;

    private static final int SDK_WINDOWS = 4;

    private static final int PI_AF = 5;

    private static final int OPCHDA = 6;

    private static final int OPCHDA_REALTIME = 7;

    private static final int OPCUA = 8;

    private static final int DEFAULT = 0;

    private static final List<String> TAG_BEGIN_CHARACTERS_INCLUDE_LIST = new ArrayList<>();

    public static final List<Integer> OPC_APPROACH_WAYS_LIST = new ArrayList<>();

    private static final String NO_INSTANCE_AVAILABLE_FLAG = "instances";

    static {
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("#");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("-");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add(".");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("[");
        TAG_BEGIN_CHARACTERS_INCLUDE_LIST.add("]");


        OPC_APPROACH_WAYS_LIST.add(Integer.valueOf(OPCHDA));
        OPC_APPROACH_WAYS_LIST.add(Integer.valueOf(OPCHDA_REALTIME));
        OPC_APPROACH_WAYS_LIST.add(Integer.valueOf(OPCUA));
    }

    @Value("${pi.protocol}")
    private String protocol;

    @Value("${pi.url.suffix}")
    private String urlSuffix;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    private DaConfigConnectorMapperCommon daConfigConnectorMapperCommon;

    @Autowired
    private ITestConnectorService sqlDasLinuxTestConServiceImpl;

    @Autowired
    private ITestConnectorService sqlDasWindowsTestConServiceImpl;

    @Autowired
    private ITestConnectorService openPlantLinuxTestConServiceImpl;

    @Autowired
    private ITestConnectorService sdkWindowsTestConServiceImpl;

    @Autowired
    private ITestConnectorService piafTestConServiceImpl;

    @Autowired
    private ITestConnectorService opchdaServiceImpl;

    @Autowired
    private ITestConnectorService opchdaRealtimeserviceImpl;

    @Autowired
    private ITestConnectorService opcUaServiceImpl;

    @Autowired
    private ITestConnectorService defaultTestConServiceImpl;

    @Autowired
    private DataProviderService dataProviderService;

    @Autowired
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private StringRedisTemplate redisOPCTemplate;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private LicenseClientService licenseClientService;

    @Resource(name = "ribbonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    @Resource(name = "commonRestTemplate")
    private RestTemplate commonRestTemplate;


    /**
     * Get da monitor connector list by appId
     *
     * @param appId
     * @return
     */
    public List<DaMonitorConnector> getDaMonitorConnectorList(String appId) {

        List<DaMonitorConnector> daMonitorConnectorList = new ArrayList<>();

        List<DaConnectorStatus> daConnectorStatusList = getDaConnectorStatusListFromDb(appId);

        if (null != daConnectorStatusList && daConnectorStatusList.size() > 0) {

            boolean piAfApp = false;
            if (PI_AF_APP_ID.equals(appId)) {
                piAfApp = true;
            }

            List<DaMonitorConnector> daMonitorConnectorListFromDaRouting;

            if (piAfApp) {
                daMonitorConnectorListFromDaRouting = getDaMonitorConnectorListFromDaAf();
            } else {
                daMonitorConnectorListFromDaRouting = getDaMonitorConnectorListFromDaRouting(appId);
            }

            Map<String, DaMonitorConnector> connectId2DaMonitorConnectorMap = new HashMap<>(16);

            if (null != daMonitorConnectorListFromDaRouting) {
                for (DaMonitorConnector daMonitorConnector : daMonitorConnectorListFromDaRouting) {
                    connectId2DaMonitorConnectorMap.put(daMonitorConnector.getId() + "", daMonitorConnector);
                }
            }

            for (DaConnectorStatus daConnectorStatus : daConnectorStatusList) {

                DaMonitorConnector daMonitorConnector = new DaMonitorConnector();
                daMonitorConnector.setId(daConnectorStatus.getId());
                daMonitorConnector.setName(daConnectorStatus.getName());
                daMonitorConnector.setConnectApproach(daConnectorStatus.getConnectApproach());

                if (daConnectorStatus.getStatus().intValue() == ConnecteStatus.CONNECTABLE.getType().intValue()) {
                    daMonitorConnector.setConnected(true);
                } else {
                    daMonitorConnector.setConnected(false);
                }

                if (piAfApp) {

                    if (connectId2DaMonitorConnectorMap.containsKey(daConnectorStatus.getId() + "")) {
                        if (connectId2DaMonitorConnectorMap.get(daConnectorStatus.getId() + "").getConnected()) {
                            daMonitorConnector.setAvailability(ConnectorAvailableStatus.AVAILABLE.getType());
                        } else {
                            daMonitorConnector.setAvailability(ConnectorAvailableStatus.NOT_AVAILABLE.getType());
                        }
                    } else {
                        daMonitorConnector.setAvailability(ConnectorAvailableStatus.UNDISTRIBUTED.getType());
                    }
                    daMonitorConnector.setWorkerInfo(null);

                } else {

                    if (connectId2DaMonitorConnectorMap.containsKey(daConnectorStatus.getId() + "")) {
                        daMonitorConnector.setAvailability(connectId2DaMonitorConnectorMap.get(daConnectorStatus.getId() + "").getAvailability());
                        daMonitorConnector.setWorkerInfo(connectId2DaMonitorConnectorMap.get(daConnectorStatus.getId() + "").getWorkerInfo());
                    } else {
                        daMonitorConnector.setAvailability(ConnectorAvailableStatus.UNDISTRIBUTED.getType());
                        daMonitorConnector.setWorkerInfo(null);
                    }

                }

                daMonitorConnectorList.add(daMonitorConnector);

            }

        }

        return daMonitorConnectorList;

    }

    /**
     * Get Base DaConnectorStatusList From DB-Core
     *
     * @param appId
     * @return
     */
    public List<DaConnectorStatus> getDaConnectorStatusListFromDb(String appId) {

        if (!StringUtils.isEmpty(appId)) {

            List<String> publicAppIdList = daConfigApplicationMapper.getPublicFleetFrameAppIdListByPrivateAppId(appId);

            Set<String> appIdSet = new HashSet<>();
            appIdSet.add(appId);

            if (null != publicAppIdList && publicAppIdList.size() > 0) {
                for (String publicAppId : publicAppIdList) {
                    appIdSet.add(publicAppId);
                }
            }

            List<String> appIdList = new ArrayList<>(appIdSet);

            List<DaConnectorStatus> daConnectorStatusList = daConfigApplicationMapper.selectDaConnectorStatusListByFleetFrameAppIdList(appIdList);

            if (null != daConnectorStatusList) {

                Set<String> connectorIdSet = new HashSet<>();

                Iterator<DaConnectorStatus> iterator = daConnectorStatusList.iterator();
                while (iterator.hasNext()) {
                    DaConnectorStatus daConnectorStatus = iterator.next();
                    if (null != daConnectorStatus) {
                        if (connectorIdSet.contains(daConnectorStatus.getId() + "")) {
                            // remove repeated connector
                            iterator.remove();
                        } else {
                            connectorIdSet.add(daConnectorStatus.getId() + "");
                        }
                    }
                }

                return daConnectorStatusList;

            }

        }

        return new ArrayList<>();
    }

    /**
     * get connector list from da-af
     *
     * @return
     */
    public List<DaMonitorConnector> getDaMonitorConnectorListFromDaAf() {

        List<DaMonitorConnector> daMonitorConnectorList = new ArrayList<>();

        HttpDataHolder modelMap = null;
        //String url = "http://10.193.9.31:8099/ofm-da-af/v2/monitor";
        String url = "http://ofm-da-af/ofm-da-af/v2/monitor";
        try {
            ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.getForEntity(url, String.class);
            if (null != stringResponseEntity) {
                modelMap = JSON.parseObject(stringResponseEntity.getBody(), HttpDataHolder.class);
            }
            if (null != modelMap) {
                if (modelMap.getSuccess()) {

                    String data = modelMap.getData();
                    if (null != data) {
                        daMonitorConnectorList = JSON.parseArray(data, DaMonitorConnector.class);
                    }

                } else {
                    logger.error("Get connector list da-af service error ,errorMsg:{}", modelMap.getMsg());
                }
            }
        } catch (Exception e) {
            logger.error("Get connector list from da-af service error, errorMsg:{}", e.getMessage(),e);
        }

        return daMonitorConnectorList;

    }

    /**
     * get connector list by appId from da-routing
     *
     * @param appId
     * @return
     */
    public List<DaMonitorConnector> getDaMonitorConnectorListFromDaRouting(String appId) {

        List<DaMonitorConnector> daMonitorConnectorList = new ArrayList<>();

        if (!StringUtils.isEmpty(appId)) {

            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

            HttpEntity<MultiValueMap<String, Object>> entity =
                    new HttpEntity<>(parameters, headers);
            HttpDataHolder modelMap = null;
            String url = "http://ofm-da-routing/ofm-da-routing/v2/daRouting/connectors/" + appId;
            try {
                ResponseEntity<String> stringResponseEntity = ribbonRestTemplate.postForEntity(url, entity, String.class);
                if (null != stringResponseEntity) {
                    modelMap = JSON.parseObject(stringResponseEntity.getBody(), HttpDataHolder.class);
                }
                if (null != modelMap) {
                    if (modelMap.getSuccess()) {

                        String data = modelMap.getData();
                        if (null != data) {
                            daMonitorConnectorList = JSON.parseArray(data, DaMonitorConnector.class);
                        }

                    } else {
                        logger.error("Get connector list by appId from da-routing service error,appId:{},errorMsg:{}", appId, modelMap.getMsg());
                    }
                }
            } catch (Exception e) {
                logger.error("Get connector list by appId from da-routing service error",e);
            }

        }

        return daMonitorConnectorList;

    }


    /**
     * get DaConfigConnector and Providers witch referenced DaConfigConnector
     *
     * @param id
     * @return
     * @throws Exception
     */
    public Map<String, Object> getConnectorWithReferencedProvider(String id) throws Exception {
        Map<String, Object> map = new HashMap<>(8);
        Long tempId;
        try {
            tempId = Long.valueOf(id);
        } catch (Exception e) {
            map.put("status", 1);
            return map;
        }
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(tempId);

        if (null == connector) {
            map.put("status", 1);
            return map;
        }
        connector.setPassword(StringUtils.isBlank(connector.getPassword()) ? "" : stringEncryptor.decrypt(connector.getPassword().trim()));

        map.put("DaConfigConnector", connector);
        map.put("DaConfigConnectorStatus", connector.getStatus());
        List<DaConfigProvider> providers = daConfigConnectorMapper.queryConnectorReferencedProviders(tempId);

        //添加可利用率
        addProvidersAvailablity(providers);
        map.put("ReferencedProvider", providers);
        map.put("status", 0);
        return map;
    }

    private void addProvidersAvailablity(List<DaConfigProvider> providerList) {
        if (CollectionUtils.isEmpty(providerList)) {
            return;
        }
        //添加可利用率
        List<Long> proIds = dataProviderService.struProIds(providerList);
        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
        //key=proId ,value=conIdList
        Map<Long, Set<Long>> proConSetMap = dataProviderService.struProConSetMap(proConList);
        //key=proId,value=avail_rate
        Map<Long, String[]> proAvailMap = dataProviderService.struProAvailMap(proConSetMap, new HashMap<>(16));
        for (DaConfigProvider provider : providerList) {
            provider.setAvailabilityRate((proAvailMap.get(provider.getId()) == null) ? "0.00" : proAvailMap.get(provider.getId())[0]);
            provider.setAvailability((proAvailMap.get(provider.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(provider.getId())[1]));
        }
    }


    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {

        //ITestConnectorService testConService = null;
        switch (getConnectorApproachType(daConfigConnectorRequest)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.testConnector(daConfigConnectorRequest);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.testConnector(daConfigConnectorRequest);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.testConnector(daConfigConnectorRequest);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.testConnector(daConfigConnectorRequest);
            case PI_AF:
                return piafTestConServiceImpl.testConnector(daConfigConnectorRequest);
            case OPCHDA:
                return opchdaServiceImpl.testConnector(daConfigConnectorRequest);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.testConnector(daConfigConnectorRequest);
            case OPCUA:
                return opcUaServiceImpl.testConnector(daConfigConnectorRequest);
            default:
                return defaultTestConServiceImpl.testConnector(daConfigConnectorRequest);
        }

    }

    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        switch (getConnectorApproachType(daConfigConnectorRequest)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.selectTagList(daConfigConnectorRequest);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.selectTagList(daConfigConnectorRequest);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.selectTagList(daConfigConnectorRequest);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.selectTagList(daConfigConnectorRequest);
            case PI_AF:
                return piafTestConServiceImpl.selectTagList(daConfigConnectorRequest);
            case OPCHDA:
                return opchdaServiceImpl.selectTagList(daConfigConnectorRequest);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.selectTagList(daConfigConnectorRequest);
            case OPCUA:
                return opcUaServiceImpl.selectTagList(daConfigConnectorRequest);
            default:
                return defaultTestConServiceImpl.selectTagList(daConfigConnectorRequest);
        }

    }

    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, String tag) throws Exception {
        switch (getConnectorApproachType(daConfigConnectorRequest)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case PI_AF:
                return piafTestConServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case OPCHDA:
                return opchdaServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.selectTagListExact(daConfigConnectorRequest, tag);
            case OPCUA:
                return opcUaServiceImpl.selectTagListExact(daConfigConnectorRequest,tag);
            default:
                return defaultTestConServiceImpl.selectTagListExact(daConfigConnectorRequest, tag);
        }
    }

    public ModelMap insertIntoTimeSeriesDB(DaConfigConnectorRequest daConfigConnectorRequest, HashSet<String> tagSet) throws Exception {
        switch (getConnectorApproachType(daConfigConnectorRequest)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case PI_AF:
                return piafTestConServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case OPCHDA:
                return opchdaServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.insertTagData(daConfigConnectorRequest, tagSet);
            case OPCUA:
                return opcUaServiceImpl.insertTagData(daConfigConnectorRequest,tagSet);
            default:
                return defaultTestConServiceImpl.insertTagData(daConfigConnectorRequest, tagSet);
        }
    }

    public int getConnectorApproachType(DaConfigConnectorRequest daConfigConnectorRequest) {
        if (daConfigConnectorRequest.getConnectorType().equals(ConnectorType.TIME_SERIES_DATA.getType())) {
            if (daConfigConnectorRequest.getConnectorClass().equals(ConnectClassType.ARCHIVED.getType()) || daConfigConnectorRequest.getConnectorClass().equals(ConnectClassType.REAL_TIME.getType()) || daConfigConnectorRequest.getConnectorClass().equals(ConnectClassType.REAL_TIME_AND_ARCHIVED.getType())) {
                if (daConfigConnectorRequest.getArchivedDatabase().equals(ConnectorDatabaseType.OSIPI.getType())) {
                    if (CategoryType.SQLDASV2012_LINUX.getType().equals(daConfigConnectorRequest.getConnectApproach()) || CategoryType.SQLDASV2016_LINUX.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                        return SQLDAS_LINUX;
                    }
                    if (CategoryType.SQLDASV2012_WINDOWS.getType().equals(daConfigConnectorRequest.getConnectApproach()) || CategoryType.SQLDASV2016_WINDOWS.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                        return SQLDAS_WINDOWS;
                    }
                    if (CategoryType.SDKV2016_WINDOWS.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                        return SDK_WINDOWS;
                    }
                }
                if (daConfigConnectorRequest.getArchivedDatabase().equals(ConnectorDatabaseType.OPENPLANT.getType())) {
                    if (CategoryType.OPENPLANTSDK_LINUX.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                        return OPENPLANT_LINUX;
                    }

                }
                if (daConfigConnectorRequest.getArchivedDatabase().equals(ConnectorDatabaseType.OPC.getType()) || daConfigConnectorRequest.getArchivedDatabase().equals(ConnectorDatabaseType.OPC_REALTIME.getType())) {
                    if (CategoryType.OPCHDA.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                        return OPCHDA;
                    }
                    if (CategoryType.OPCHDA_REALTIME.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                        return OPCHDA_REALTIME;
                    }
                }
                if(ConnectorDatabaseType.OPC_UA.getType().equals(daConfigConnectorRequest.getArchivedDatabase())){
                    if(CategoryType.OPCUA_GATEWAY.getType().equals(daConfigConnectorRequest.getConnectApproach())){
                        return OPCUA;
                    }
                }
            }
        }
        if (daConfigConnectorRequest.getConnectorType().equals(ConnectorType.SQL_DATA.getType())) {
            if (daConfigConnectorRequest.getConnectorClass().equals(ConnectClassType.PIAF.getType())) {
                if (CategoryType.OSIPIAFSERVER.getType().equals(daConfigConnectorRequest.getConnectApproach())) {
                    return PI_AF;
                }
            }
        }

        return DEFAULT;
    }

    private ModelMap getErrorConnectMap(ModelMap modelMap) {
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, CONNECT_FAIL);
        modelMap.put(DATA, structureRetMap(false, null));
        return modelMap;
    }

    private ModelMap getErrorConnectMap(ModelMap modelMap, Boolean b, String msg) {
        modelMap.put(IS_SUCCESS, b);
        modelMap.put(MSG, msg);
        modelMap.put(DATA, structureRetMap(false, null));
        return modelMap;
    }


    public ModelMap saveConnector(DaConfigConnectorRequest daConfigConnectorRequest) {
        ModelMap modelMap = new ModelMap();
        DaConfigConnector daConfigConnector = new DaConfigConnector();
        daConfigConnectorRequest.setId(daConfigConnectorMapperCommon.selectId());
        BeanUtils.copyProperties(daConfigConnectorRequest, daConfigConnector);
        daConfigConnector.setDataType(DataConfigType.DATA_CONNECTOR.getType());
        try {
            long bgTime = System.currentTimeMillis();
            ModelMap conStatus = testConnector(daConfigConnectorRequest);
            long endTime = System.currentTimeMillis();
            logger.info("Test connect use time:{}ms", (endTime - bgTime));
            Map<String, Object> tmap = (Map<String, Object>) conStatus.get(DATA);
            if (((Boolean) tmap.get(STATUS))) {
                daConfigConnector.setStatus(ConnecteStatus.CONNECTABLE.getType());
            } else {
                daConfigConnector.setStatus(ConnecteStatus.UNCONNECTABLE.getType());
            }
        } catch (Exception e) {
            daConfigConnector.setStatus(ConnecteStatus.UNCONNECTABLE.getType());
        }
        try {
            daConfigConnectorRequest.setPassword(StringUtils.isBlank(daConfigConnectorRequest.getPassword()) ? "" : daConfigConnectorRequest.getPassword().trim());
            String connectorInfo = structureConnectorInfo(daConfigConnectorRequest);

            daConfigConnector.setConnectorInfo(connectorInfo);
            //对password加密
            daConfigConnector.setPassword(StringUtils.isBlank(daConfigConnectorRequest.getPassword()) ? "" : stringEncryptor.encrypt(daConfigConnectorRequest.getPassword().trim()));
            if (daConfigConnectorMapper.insert(daConfigConnector) != 1) {
                modelMap.put(MSG, SAVE_FAIL);
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                modelMap.put(DATA, structureRetMap(false, null));
                return modelMap;
            }

        } catch (Exception e) {
            logger.error("save Connector Error:" + e.getMessage(),e);
            modelMap.put(MSG, SAVE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(DATA, structureRetMap(false, null));
            return modelMap;
        }

        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        modelMap.put(MSG, SAVE_SUCCESS);
        modelMap.put(DATA, structureRetMap(true, daConfigConnector.getId()));
        return modelMap;

    }

    public ModelMap insertOrUpdateConnectorSensorList(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {
        RedisLock redisLock = new RedisLock(daConfigConnectorRequest.getId().toString(), redisOPCTemplate);
        try {
            if (redisLock.lock()) {
                switch (getConnectorApproachType(daConfigConnectorRequest)) {
                    case SQLDAS_LINUX:
                        return sqlDasLinuxTestConServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.SQLDAS_LINUX.getType());
                    case SQLDAS_WINDOWS:
                        return sqlDasWindowsTestConServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.SQLDAS_WINDOWS.getType());
                    case OPENPLANT_LINUX:
                        return openPlantLinuxTestConServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.OPENPLANT_LINUX.getType());
                    case SDK_WINDOWS:
                        return sdkWindowsTestConServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.SDK_WINDOWS.getType());
                    case PI_AF:
                        return piafTestConServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.PI_AF.getType());
                    case OPCHDA:
                        return opchdaServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.OPCHDA.getType());
                    case OPCHDA_REALTIME:
                        return opchdaRealtimeserviceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.OPCHDA_REALTIME.getType());
                    case OPCUA:
                        return opcUaServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest,ConnDatabaseType.OPCUA.getType());
                    default:
                        return defaultTestConServiceImpl.addOrUpdateConnectorSensorRelation(daConfigConnectorRequest, ConnDatabaseType.DEFAULT.getType());
                }
            } else {
                throw new RuntimeException("The current ConnectorSensor is being modified!");
            }
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }
    }

    public String structureConnectorInfo(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        switch (getConnectorApproachType(daConfigConnectorRequest)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case PI_AF:
                return piafTestConServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case OPCHDA:
                return opchdaServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.structureConnectorInfo(daConfigConnectorRequest);
            case OPCUA:
                return opcUaServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
            default:
                return defaultTestConServiceImpl.structureConnectorInfo(daConfigConnectorRequest);
        }
    }


    private Map<String, Object> structureRetMap(boolean flag, Long id) {
        Map<String, Object> retMap = new HashMap<String, Object>(4);
        retMap.put("status", flag);
        if (null != id) {
            retMap.put("id", id);
        }
        return retMap;
    }

    public boolean isConnectorOp(Long connectorId) {
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(connectorId);
        if (connector.getArchivedDatabase().equals(ConnectorDatabaseType.OPENPLANT.getType())) {
            if (CategoryType.OPENPLANTSDK_LINUX.getType().equals(connector.getConnectApproach())) {
                return true;
            }
        }
        return false;
    }

    public ModelMap checkTagExistByConnectorId(Long connectorId, String tag, Boolean toBeCreated, Long applicationId, ModelMap modelMap) throws Exception {

        modelMap.put(DATA, tag);
        //verify tag exists by connector that has been used
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(connectorId);
        DaConfigConnectorRequest daConfigConnectorRequest = new DaConfigConnectorRequest();
        BeanUtils.copyProperties(connector, daConfigConnectorRequest);
        daConfigConnectorRequest.setPassword(StringUtils.isBlank(daConfigConnectorRequest.getPassword()) ? "" : stringEncryptor.decrypt(daConfigConnectorRequest.getPassword().trim()));
        Map<String, String> resultData = null;
        try {
            if (isConnectorOp(connectorId)) {
                resultData = this.selectTagListExact(daConfigConnectorRequest, tag.toUpperCase());
            } else {
                resultData = this.selectTagListExact(daConfigConnectorRequest, tag);
            }
        } catch (Exception ex) {
            logger.error("selectTagListExact error : " + ex.getMessage(),ex);
            modelMap.put(IS_SUCCESS, false);
            if (ex.getMessage().contains(NO_INSTANCE_AVAILABLE_FLAG)) {
                modelMap.put(MSG, DA_CORE_AGENT_ERROR);
            } else {
                modelMap.put(MSG, OPERATE_FAIL);
            }
            return modelMap;
        }
        List<DaConfigSensor> sensorList = daConfigSensorMapper.selectListByConnectorIdAndSiecode(connectorId, tag, applicationId);
        //tagname logic check
        if (TAG_BEGIN_CHARACTERS_INCLUDE_LIST.contains(tag.substring(0,1))) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, TAG_NAME_NOT_IMPROPER);
            return modelMap;
        }

        if (toBeCreated) {
            if (!CollectionUtils.isEmpty(resultData)) {
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(TAG_EXIST_IN_POOL, true);
                modelMap.put(MSG, TAG_HAS_EXIST_UPDER_CONNECTOR);
                return modelMap;
            } else {
                modelMap.put(TAG_EXIST_IN_POOL, false);

                modelMap.put(IS_SUCCESS, true);
                modelMap.put(MSG, TAG_VERIFY_SUCCESSFUL);
            }
        } else {
            if (CollectionUtils.isEmpty(resultData)) {
                modelMap.put(TAG_EXIST_IN_POOL, false);
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, TAG_NOT_EXIST_UPDER_CONNECTOR);
                return modelMap;
            } else {
                if (!CollectionUtils.isEmpty(sensorList)) {
                    modelMap.put(IS_SUCCESS, false);
                    modelMap.put(MSG, TAG_HAS_BEEN_MAPED);
                    return modelMap;
                } else  {
                    modelMap.put(IS_SUCCESS, true);
                    modelMap.put(MSG, TAG_VERIFY_SUCCESSFUL);
                }
                modelMap.put(TAG_EXIST_IN_POOL, true);
            }
        }
        return modelMap;
    }


    /**
     * 批量查询tag
     * @param daConfigConnectorReques
     * @param tagList
     * @return
     * @throws Exception
     */
    public Map<String, List<String>> selectTagListBatch(DaConfigConnectorRequest daConfigConnectorReques, List<String> tagList) throws Exception {
        switch (getConnectorApproachType(daConfigConnectorReques)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case PI_AF:
                return piafTestConServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case OPCHDA:
                return opchdaServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
            case OPCUA:
                return opcUaServiceImpl.batchSelectTagListExact(daConfigConnectorReques,tagList);
            default:
                return defaultTestConServiceImpl.batchSelectTagListExact(daConfigConnectorReques, tagList);
        }
    }


    /**
     * 批量注册测点
     * @param daConfigConnectorRequest
     * @param tagSet
     * @return
     * @throws Exception
     */
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorRequest, HashSet<String> tagSet) throws Exception {
        switch (getConnectorApproachType(daConfigConnectorRequest)) {
            case SQLDAS_LINUX:
                return sqlDasLinuxTestConServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case SQLDAS_WINDOWS:
                return sqlDasWindowsTestConServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case OPENPLANT_LINUX:
                return openPlantLinuxTestConServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case SDK_WINDOWS:
                return sdkWindowsTestConServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case PI_AF:
                return piafTestConServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case OPCHDA:
                return opchdaServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case OPCHDA_REALTIME:
                return opchdaRealtimeserviceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
            case OPCUA:
                return opcUaServiceImpl.batchInsertTagData(daConfigConnectorRequest,tagSet);
            default:
                return defaultTestConServiceImpl.batchInsertTagData(daConfigConnectorRequest, tagSet);
        }
    }

    public ModelMap queryOpcUaEvents(OpcUaEventsQueryRequest request, ModelMap modelMap) {
        DaConfigConnector connector = daConfigConnectorMapper.selectById(request.getConnectorId());
        connector.setPassword(stringEncryptor.decrypt(connector.getPassword().trim()));

        if (!connector.getConnectApproach().equalsIgnoreCase(CategoryType.OPCUA_GATEWAY.getType())) {

            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, CONNECTOR_CATEGROY_NOT_MATCH);

            return modelMap;
        }

        try {
            List<UaEvent> events = ((OpcUaServiceImpl) opcUaServiceImpl).queryOpcUaEvents(connector, request.getStart(),
                    request.getEnd(), request.getNodeId());
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(DATA, events);
        } catch (Exception e) {
            logger.error("read opcua events error", e);

            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, OPCUA_READ_EVENTS_ERROR);
        }

        return modelMap;
    }
}
