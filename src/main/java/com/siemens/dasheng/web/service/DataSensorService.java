package com.siemens.dasheng.web.service;

import com.magus.net.OPConnect;
import com.magus.net.OPDB;
import com.magus.net.OPNode;
import com.siemens.dasheng.web.enums.*;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.mapperfactory.DaConfigConnectorSensorMapperCommon;
import com.siemens.dasheng.web.mapperfactory.DaConfigGroupMapperCommon;
import com.siemens.dasheng.web.mapperfactory.DaConfigSensorMapperCommon;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.batchregist.RegistData;
import com.siemens.dasheng.web.request.*;
import com.siemens.dasheng.web.response.*;
import com.siemens.dasheng.web.singleton.constant.CommonConstant;
import com.siemens.dasheng.web.util.CollectionUtil;
import com.siemens.dasheng.web.util.RedisLock;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.CommonConstant.*;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @description: data sensor service
 * @author: ly
 * @create: 2019-04-03 17:10
 */
@Service
public class DataSensorService {

    private static final int NAME_DEFAULT_LENGTH = 100;

    private static final int DES_DEFAULT_LENGTH = 200;

    private static final Long ACCOUNT = 0L;

    private static final String AM_GLOBAL_APP_ID = "OFM1125281313222352891";

    @Autowired
    private StringRedisTemplate redisOPCTemplate;

    @Autowired
    private DaConfigGroupMapper daConfigGroupMapper;

    @Autowired
    private DaConfigGroupMapperCommon daConfigGroupMapperCommon;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private DaConfigApplicationSyncService daConfigApplicationSyncService;

    @Autowired
    private DaAppResourceUsageMapper daAppResourceUsageMapper;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private DaConfigSensorMapperCommon daConfigSensorMapperCommon;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DaConfigConnectorSensorMapperCommon daConfigConnectorSensorMapperCommon;


    @Autowired
    private StringEncryptor stringEncryptor;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 内部同步锁
     */
    private final Lock lock = new ReentrantLock();

    /**
     * view sensor info
     *
     * @param sieCode
     * @return
     */
    public Map<String, Object> getSensorInfoBySieCode(String sieCode) {
        Map<String, Object> map = daConfigSensorMapper.selectSimpleSensorInfoBySieCode(sieCode);
        if (map == null) {
            return new HashMap<>(0);
        }
        String globalAppIdPg = (String) map.get("global_app_id");
        String globalAppIdOrc = (String) map.get("GLOBAL_APP_ID");
        String globalAppId = globalAppIdPg != null ? globalAppIdPg : globalAppIdOrc;
        if (AM_GLOBAL_APP_ID.equals(globalAppId)) {
            map.put("scope", 0L);
        } else {
            map.put("scope", 1L);
        }
        return map;
    }

    /**
     * GetSensorListByGlobalId
     * @param globalId
     * @return
     */
    public List<Map<String, String>> getSensorListByGlobalId(String globalId) {
        Long appId = daConfigApplicationMapper.getPublicAppIdByGlobalId(globalId);
        if (null==appId) {
            return new ArrayList<>();
        }
        List<DaConfigSensor> sensorList = daConfigSensorMapper.getSensorListByAppId(appId);
        if (null != sensorList && sensorList.size() > 0) {
            List<Map<String, String>> res = new ArrayList<>();
            for (DaConfigSensor sensor : sensorList) {
                if (null != sensor && null != sensor.getSiecode() && null != sensor.getTag()) {
                    Map<String, String> sensorInstance = new HashMap<>(4);
                    sensorInstance.put("siecode", sensor.getSiecode());
                    sensorInstance.put("tstag", sensor.getTag());
                    sensorInstance.put("description", sensor.getDescription());
                    sensorInstance.put("unit", sensor.getUnit());
                    res.add(sensorInstance);
                }
            }
            return res;
        }
        return new ArrayList<>();
    }

    /**
     * view sensor group list
     *
     * @param appId
     * @return
     */
    public List<DaConfigGroupPlus> getSensorGroupList(Long appId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("appId", appId);
        List<DaConfigGroupPlus> list = daConfigGroupMapper.selectGroupListByAppId(map);
        if (!CollectionUtils.isEmpty(list)) {
            for (DaConfigGroupPlus plus : list) {
                plus.setAccount(null == plus.getAccount() ? ACCOUNT : plus.getAccount());
                plus.setUsage(plus.getUsedNum() > 0 ? UsageType.AVAILABLE.getType() : UsageType.UNAVAILABLE.getType());
            }
        }
        return list;
    }

    /**
     * view all sensor list
     *
     * @param appId
     * @return
     */
    public List<String> getSensorList(Long appId) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("appId", appId);
        List<String> sensorList = daConfigGroupMapper.selectSensorListByAppId(map);
        return sensorList;
    }

    /**
     * get all sensors
     *
     * @return
     */
    public List<DaConfigSensor> getAllSensorList() {
        return daConfigSensorMapper.selectAllSensorList();
    }

    /**
     * select all sensors by appId
     *
     * @param appId
     * @return
     */
    public List<DaConfigSensorPlus> selectAvailableSensorListByAppId(Long appId) {
        return daConfigSensorMapper.selectAvailableSensorListByAppId(appId);
    }

    /**
     * add group with sensor list
     *
     * @param daConfigGroupRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSensorGroup(DaConfigGroupRequest daConfigGroupRequest) {
        Long appId = daConfigGroupRequest.getAppId();
        String groupName = daConfigGroupRequest.getGroupName();
        String groupDescription = daConfigGroupRequest.getGroupDescription();
        List<String> usedSensorList = daConfigGroupRequest.getSensorList();
        DaConfigGroup daConfigGroup = new DaConfigGroup();
        daConfigGroup.setName(groupName);
        daConfigGroup.setDescription(groupDescription);
        daConfigGroup.setAppId(appId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("appId", appId);
        // redis lock
        RedisLock redisLock = new RedisLock(groupName, redisOPCTemplate);
        try {
            if (redisLock.lock()) {
                List<DaConfigGroupPlus> list = daConfigGroupMapper.selectGroupListByAppId(map);
                boolean groupNameIsExist = list.stream().anyMatch(group -> group.getName().equals(groupName));
                if (groupNameIsExist) {
                    throw new RuntimeException("The current sensor group name is in used!");
                }
                // insert group info
                daConfigGroupMapperCommon.insertSelective(daConfigGroup);
                // insert sensor group relationship
                insertSensorGroupRelationship(daConfigGroup.getId(), usedSensorList);
            } else {
                throw new RuntimeException("The current sensor group name is in used!");
            }
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }
    }

    /**
     * remove group and sensors
     *
     * @param groupId
     */
    @Transactional(rollbackFor = Exception.class)
    public RemoveSensorGroupResponse removeSensorGroup(Long groupId) {
        RemoveSensorGroupResponse res = new RemoveSensorGroupResponse();
        //验证测点组是否被app引用
        List<DaAppResourceUsage> appNames = new ArrayList<>();
        if (checkSensorGroupImported(groupId, ObjectTypeEnum.SENSORGROUP.getType(), appNames)) {
            res.setSensorList(appNames);
            res.setStatus(STATUS1);
            return res;
        }
        daConfigGroupMapper.deleteByPrimaryKey(groupId);
        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", groupId);
        daConfigGroupMapper.deleteSensorWithGroup(map);
        res.setStatus(STATUS0);
        return res;
    }

    private boolean checkSensorGroupImported(Long groupId, String type, List<DaAppResourceUsage> appNames) {
        List<DaAppResourceUsage> appNamess = daAppResourceUsageMapper.queryUseResourceBySensorOrGroup(groupId.toString(), type);
        if (CollectionUtils.isEmpty(appNamess)) {
            return false;
        }
        appNames.addAll(appNamess);
        return true;
    }

    /**
     * modify group and sensors
     *
     * @param daConfigGroupRequest
     */
    @Transactional(rollbackFor = Exception.class)
    public ModifySensorGroupResponse modifySensorGroup(DaConfigGroupRequest daConfigGroupRequest) {
        logger.info("###########start modifying SensorGroup###########");
        ModifySensorGroupResponse response = new ModifySensorGroupResponse();
        Long appId = daConfigGroupRequest.getAppId();
        Long groupId = daConfigGroupRequest.getGroupId();
        String groupName = daConfigGroupRequest.getGroupName();
        String description = daConfigGroupRequest.getGroupDescription();
        List<String> usedSensorList = daConfigGroupRequest.getSensorList();
        DaConfigGroup daConfigGroup = new DaConfigGroup();
        daConfigGroup.setId(groupId);
        daConfigGroup.setName(groupName);
        daConfigGroup.setDescription(description);
        // check if group name has already been in used
        Map<String, Object> map = new HashMap<>(2);
        map.put("appId", appId);
        // redis lock
        RedisLock redisLock = new RedisLock(groupId.toString(), redisOPCTemplate);
        try {
            if (redisLock.lock()) {
                //验证修改的sensorList是否都存在
                List<String> noExistsensorList = new ArrayList<>();
                if (checkSensorExist(usedSensorList, noExistsensorList)) {
                    response.setStatus(STATUS3);
                    response.setSensorCodeList(noExistsensorList);
                    return response;
                }
                List<DaConfigGroupPlus> list = daConfigGroupMapper.selectGroupListByAppId(map);
                boolean groupNameIsUsedByOthers = list.stream().anyMatch(group -> group.getName().equals(groupName) && !group.getId().equals(groupId));
                if (groupNameIsUsedByOthers) {
                    response.setStatus(STATUS1);
                    return response;
                }
                //验证移除的sensor是否被引用
                List<String> removedSensorList = queryRemovedSensorList(daConfigGroupRequest.getSensorList(), daConfigGroupRequest.getGroupId());
                if (!CollectionUtils.isEmpty(removedSensorList)) {
                    List<Map<String, Object>> userdSensorList = new ArrayList<>();
                    if (checkRemovedSensorImported(userdSensorList, removedSensorList)) {
                        response.setStatus(STATUS2);
                        response.setUserdSensorList(userdSensorList);
                        return response;
                    }
                }
                // 修改测点组信息
                daConfigGroupMapper.updateByPrimaryKeySelective(daConfigGroup);
                // update sensor group relationship
                Map<String, Object> map2 = new HashMap<>(2);
                map2.put("groupId", groupId);
                daConfigGroupMapper.deleteSensorWithGroup(map2);
                insertSensorGroupRelationship(groupId, usedSensorList);
                // 修改成功之后需要将该测点组版本号更新
                List<Long> groupIdList = new ArrayList<>(2);
                groupIdList.add(groupId);

                daConfigApplicationSyncService.daConfigGroupVersionUpgrade(groupIdList);
                logger.info("end update sensor group");
            } else {
                throw new RuntimeException("The current sensorGroup is being modified!");
            }
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }
        response.setStatus(STATUS0);
        return response;

    }

    private boolean checkSensorExist(List<String> usedSensorList, List<String> noExistsensorList) {
        if (CollectionUtils.isEmpty(usedSensorList)) {
            return false;
        }
        List<DaConfigSensor> sensorList = daConfigSensorMapper.selectBySiecodes(usedSensorList);
        List<String> existSensorList = new ArrayList<>();
        for (DaConfigSensor sen : sensorList) {
            existSensorList.add(sen.getSiecode());
        }
        noExistsensorList.addAll(CollectionUtil.getReduceaListThanbList(existSensorList, usedSensorList));
        if (CollectionUtils.isEmpty(noExistsensorList)) {
            return false;
        }
        return true;

    }

    private boolean checkRemovedSensorImported(List<Map<String, Object>> userdSensorList, List<String> removedSensorList) {
        List<DaAppResourceUsage> usages = daAppResourceUsageMapper.queryUseResourcesBySensorOrGroup(removedSensorList, ObjectTypeEnum.SENSOR.getType());
        if (CollectionUtils.isEmpty(usages)) {
            return false;
        }
        Map<String, List<String>> retMap = new HashMap<>(4);
        for (DaAppResourceUsage use : usages) {
            if (CollectionUtils.isEmpty(retMap.get(use.getObjectid()))) {
                List<String> appList = new ArrayList<>();
                appList.add(use.getConsumerobject());
                retMap.put(use.getObjectid(), appList);
                continue;
            }
            retMap.get(use.getObjectid()).add(use.getConsumerobject());
        }

        for (Map.Entry<String, List<String>> sensorUsage : retMap.entrySet()) {
            Map<String, Object> tempMap = new HashMap<>(4);
            tempMap.put("sensor", sensorUsage.getKey());
            tempMap.put("appList", sensorUsage.getValue());
            userdSensorList.add(tempMap);
        }
        return true;
    }

    private List<String> queryRemovedSensorList(List<String> sensorList, Long groupId) {
        Map<String, Object> map = new HashMap<>(4);
        map.put("groupId", groupId);
        List<String> origList = daConfigGroupMapper.selectSensorListWithGroup(map);
        return CollectionUtil.getReduceaListThanbList(sensorList, origList);
    }


    /**
     * view group and sensors
     *
     * @param groupId
     * @return
     */
    public DaConfigGroupResponse viewSensorGroup(Long groupId) {
        DaConfigGroupResponse response = null;
        DaConfigGroup daConfigGroup = daConfigGroupMapper.selectByPrimaryKey(groupId);
        if (daConfigGroup != null) {
            Map<String, Object> queryMap = new HashMap<>(2);
            queryMap.put("groupId", groupId);
            queryMap.put("appId", daConfigGroup.getAppId());
            List<String> usedSensorList = daConfigGroupMapper.selectSensorListWithGroup(queryMap);
            List<String> sensorList = daConfigGroupMapper.selectSensorListByAppId(queryMap);
            response = new DaConfigGroupResponse();
            response.setGroupId(groupId);
            response.setGroupName(daConfigGroup.getName());
            response.setGroupDescription(daConfigGroup.getDescription());
            response.setSensorList(sensorList);
            response.setUsedSensorList(usedSensorList);
            return response;
        }
        throw new RuntimeException("the sensor group is not exists!");
    }

    /**
     * insert sensor group relationship
     *
     * @param groupId
     * @param usedSensorList
     */
    private void insertSensorGroupRelationship(Long groupId, List<String> usedSensorList) {
        if (usedSensorList == null || usedSensorList.size() == 0) {
            return;
        }
        List<Map<String, Object>> list = usedSensorList.stream().map(sensorCode -> {
            Map<String, Object> map = new HashMap<>(4);
            map.put("groupId", groupId);
            map.put("sensorCode", sensorCode);
            return map;
        }).collect(Collectors.toList());
        daConfigGroupMapperCommon.insertSensorList(list);
    }


    /**
     * 删除测点
     *
     * @param deleteSensorRequest
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DeleteSensorResponse deleteSensor(DeleteSensorRequest deleteSensorRequest) {
        DeleteSensorResponse res = new DeleteSensorResponse();
        DaConfigSensor sensor = daConfigSensorMapper.selectBySiecode(deleteSensorRequest.getSiecode());
        if (sensor == null) {
            res.setStatus(STATUS3);
            return res;
        }
        if (STATUS1.equals(sensor.getFromRegist())) {
            res.setStatus(STATUS4);
            return res;
        }
        //查询sensor是否被app引用
        List<DaAppResourceUsage> usages = new ArrayList<>();
        if (checkSensorImported(deleteSensorRequest.getAppId(), deleteSensorRequest.getSiecode(), usages)) {
            res.setStatus(1);
            res.setUsages(usages);
            return res;
        }
        Long appId = daConfigGroupMapper.selectAppIdBySieCode(deleteSensorRequest.getSiecode());
        List<Long> groupIdList = daConfigGroupMapper.selectGroupIdListBySieCode(deleteSensorRequest.getSiecode());
        if (daConfigSensorMapper.deleteBySieCode(deleteSensorRequest.getSiecode()) < 1) {
            res.setStatus(2);
            return res;
        }
        //删除幽灵tag
        if (sensor.getStatus().equals(DelStatus.DELETE.getType())) {
            clearGhostTag(sensor.getTag(), sensor.getConnectorId());
        }
        //删除relation
        daConfigSensorMapper.deleteRelationBySieCode(deleteSensorRequest.getSiecode());
        // 更新相关app版本
        daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(appId);
        // 更新相关的测点组版本
        daConfigApplicationSyncService.daConfigGroupVersionUpgrade(groupIdList);
        // 删除测点下的策略新
        daConfigSensorMapper.deleteValidRuleBySieCode(deleteSensorRequest.getSiecode());
        res.setStatus(0);
        return res;

    }

    private void clearGhostTag(String tag, Long connectorId) {
        DaConfigSensor daSensor = new DaConfigSensor();
        daSensor.setTag(tag);
        daSensor.setConnectorId(connectorId);
        List<DaConfigSensor> sensorList = daConfigSensorMapper.select(daSensor);
        if (CollectionUtils.isEmpty(sensorList)) {
            daConfigConnectorSensorMapper.deleteByConnectorIdAndTag(connectorId, tag);
        }
    }

    private boolean checkSensorImported(Long appId, String siecode, List<DaAppResourceUsage> usages) {
        List<DaAppResourceUsage> usagess = daAppResourceUsageMapper.queryUseResourceBySensorOrGroup(siecode, ObjectTypeEnum.SENSOR.getType());
        if (CollectionUtils.isEmpty(usagess)) {
            return false;
        }
        usages.addAll(usagess);
        return true;
    }

    public List<DaAppResourceUsage> querySensorAppList(QuerySensorAppListRequest querySensorAppListRequest) {
        List<DaAppResourceUsage> appUsageList = daAppResourceUsageMapper.queryUseResourceBySensorOrGroup(querySensorAppListRequest.getObjectId(), querySensorAppListRequest.getObjectType());
        return appUsageList;
    }

    @Transactional(rollbackFor = Exception.class)
    public ModelMap saveSensorRegistration(SensorRegistrationRequest sensorRegistrationRequest, ModelMap modelMap) throws Exception {
        //insert tag into time series database
        HashSet<String> tagSet = new HashSet<String>(1);
        tagSet.add(sensorRegistrationRequest.getTag());
        //查询connector是否openplant
        if(isConnectorOp(sensorRegistrationRequest.getConnectorId())){
            sensorRegistrationRequest.setOptag(sensorRegistrationRequest.getTag());
            sensorRegistrationRequest.setTag(struOpTag(sensorRegistrationRequest.getConnectorId())+sensorRegistrationRequest.getTag().toUpperCase());
        }
        modelMap.put(DATA,sensorRegistrationRequest.getTag());

        //check attr available
        modelMap = checkSensorRegistrationAttrAvailable(sensorRegistrationRequest, modelMap);
        if (!(Boolean) modelMap.get(IS_SUCCESS)) {
            return modelMap;
        }
        //verify sensoe exist
        DaConfigSensor sensor = daConfigSensorMapper.selectBySiecode(sensorRegistrationRequest.getSiecode());
        if (sensor != null) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, SENSOR_HAS_EXIST);
            return modelMap;
        }
        //verify tag logic
        modelMap = dataConnectorService.checkTagExistByConnectorId(sensorRegistrationRequest.getConnectorId()
                , sensorRegistrationRequest.getTag(), sensorRegistrationRequest.getToBeCreated(), sensorRegistrationRequest.getApplicationId(), modelMap);
        if (!(Boolean) modelMap.get(IS_SUCCESS)) {
            return modelMap;
        }
        // find connector info
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(sensorRegistrationRequest.getConnectorId());
        DaConfigConnectorRequest daConfigConnectorRequest = new DaConfigConnectorRequest();
        BeanUtils.copyProperties(connector, daConfigConnectorRequest);
        daConfigConnectorRequest.setPassword(StringUtils.isBlank(daConfigConnectorRequest.getPassword()) ? "" : stringEncryptor.decrypt(daConfigConnectorRequest.getPassword().trim()));

        //judge if need insert to tag pool
        if (!(Boolean) modelMap.get(TAG_EXIST_IN_POOL)) {
            modelMap = dataConnectorService.insertIntoTimeSeriesDB(daConfigConnectorRequest, tagSet);
            modelMap.put(DATA,sensorRegistrationRequest.getTag());
            if (!(Boolean) modelMap.get(IS_SUCCESS)) {
                return modelMap;
            }
        }
        //do create sensor
        DaConfigSensor daConfigSensor = this.doCreateConfigSensor(sensorRegistrationRequest);
        List<DaConfigSensor> configSensorList = new ArrayList<>();
        configSensorList.add(daConfigSensor);

        if (daConfigSensorMapperCommon.insertList(configSensorList) != 1) {
            modelMap.put(MSG, SAVE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
        //check tag exist if no do create
        List<DaConfigConnectorSensorPlus> connectorSensorList = daConfigConnectorSensorMapper
                .selectListByConnectorIdAndSiecode(sensorRegistrationRequest.getConnectorId(), sensorRegistrationRequest.getTag());
        DaConfigConnectorSensor connSensor = null;
        if (CollectionUtils.isEmpty(connectorSensorList)) {
            //docreate tag
            connSensor = this.doCreateConnectorSensor(sensorRegistrationRequest, connector);
            List<DaConfigConnectorSensor> connSensorList = new ArrayList<>();
            connSensorList.add(connSensor);
            if (daConfigConnectorSensorMapperCommon.insertList(connSensorList) != 1) {
                modelMap.put(MSG, SAVE_FAIL);
                modelMap.put(IS_SUCCESS, Boolean.TRUE);
                return modelMap;
            }
        }
        // 更新app版本号
        Long appId = sensorRegistrationRequest.getApplicationId();
        daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(appId);

        modelMap.put(MSG, REGIST_SENSOR_SUCCESSFUL);
        modelMap.put(ID, connSensor == null ? null : connSensor.getId());
        modelMap.put(DATA,sensorRegistrationRequest.getTag());
        return modelMap;
    }

    public String struOpTag(Long connectorId) throws Exception{
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(connectorId);
        OPConnect conn = null;
        try {
            conn = new OPConnect(connector.getServerHost(), Integer.parseInt(connector.getPort()), 6000, connector.getUserName(), StringUtils.isBlank(connector.getPassword()) ? "" : stringEncryptor.decrypt(connector.getPassword().trim()));
            if(!StringUtils.isBlank(connector.getDatabase())){
                OPNode opnode = conn.getNodeByNodeName(connector.getDatabase());
                if (null == opnode) {
                    return "";
                }
            }
            //查询所有的database
            OPDB[] dbs = conn.getDBs();
            OPNode jiedian;
            //s所用database；
            OPDB db = null;
            if(StringUtils.isBlank(connector.getDatabase())){
                db = dbs[0];
            }else{
                for(OPDB tdb : dbs){
                    if(tdb.getName().equals(connector.getDatabase())){
                        db = tdb;
                        break;
                    }
                }
            }
            if(null == db){
                db = dbs[0];
            }
            return db.getName()+"."+"UNIT_HUIXIE"+".";
        }catch (IOException e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
            throw new Exception(e);
        } catch (Exception e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
            throw new Exception(e);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    logger.error("close openplant conn error : " + e.getMessage(),e);
                    throw new Exception(e);
                }
            }
        }
    }

    private boolean isConnectorOp(Long connectorId) {
        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(connectorId);
        if (connector.getArchivedDatabase().equals(ConnectorDatabaseType.OPENPLANT.getType())) {
            if (CategoryType.OPENPLANTSDK_LINUX.getType().equals(connector.getConnectApproach())) {
                return true;
            }
        }
        return false;
    }

    public ModelMap checkSensorRegistrationAttrAvailable(SensorRegistrationRequest sensorRegistrationRequest, ModelMap modelMap) {
        try {
            if (sensorRegistrationRequest.getSiecode().getBytes(CommonConstant.UTF8).length > NAME_DEFAULT_LENGTH) {
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, SIECODE_NAME_LENGTH_MAXSIZE_100);
            }
            if (!StringUtils.isBlank(sensorRegistrationRequest.getDescription())
                    && sensorRegistrationRequest.getDescription().getBytes(CommonConstant.UTF8).length > DES_DEFAULT_LENGTH) {
                modelMap.put(IS_SUCCESS, false);
                modelMap.put(MSG, SENSOR_DESCRIPTION_LENGTH_MAXSIZE_200);
            }
        } catch (UnsupportedEncodingException e) {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, SIECODE_NAME_LENGTH_MAXSIZE_100);
            return modelMap;
        }
        return modelMap;
    }

    /**
     * @param sensorRegistrationRequest
     * @return daConfigSensor
     * @author zhangliming
     */
    public DaConfigSensor doCreateConfigSensor(SensorRegistrationRequest sensorRegistrationRequest) {
        //sensor alway new created
        DaConfigSensor daConfigSensor = new DaConfigSensor();
        sensorRegistrationRequest.setId(daConfigSensorMapperCommon.selectId());
        if (StringUtils.isBlank(sensorRegistrationRequest.getDescription())) {
            sensorRegistrationRequest.setDescription(sensorRegistrationRequest.getSiecode());
        }
        //properties copy
        BeanUtils.copyProperties(sensorRegistrationRequest, daConfigSensor);
        daConfigSensor.setAppId(sensorRegistrationRequest.getApplicationId());
        //init status and unit
        daConfigSensor.setStatus(DelStatus.IMPORTED.getType());
        daConfigSensor.setFromRegist(CommonConstant.STATUS1);
        daConfigSensor.setUnit(null);
        return daConfigSensor;
    }

    /**
     * @param sensorRegistrationRequest
     * @return connSensor
     * @author zhangliming
     */
    public DaConfigConnectorSensor doCreateConnectorSensor(SensorRegistrationRequest sensorRegistrationRequest, DaConfigConnector connector) {
        DaConfigConnectorSensorPlus connSensor = new DaConfigConnectorSensorPlus();
        connSensor.setId(daConfigConnectorSensorMapperCommon.selectId());
        connSensor.setConnectorId(sensorRegistrationRequest.getConnectorId());
        connSensor.setConnectorInfo(connector.getConnectorInfo());
        connSensor.setTag(sensorRegistrationRequest.getTag());
        connSensor.setUnit(null);
        connSensor.setStatus(DelStatus.IMPORTED.getType());
        connSensor.setFromRegist(CommonConstant.STATUS1);
        return connSensor;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAppAndSensorGroupVersion(String siecode) {

        if (StringUtils.isNotEmpty(siecode)) {

            DaConfigSensor sensor = daConfigSensorMapper.selectBySiecode(siecode);

            if (null != sensor) {
                // 更新相关app版本
                daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(sensor.getAppId());
                // 更新相关的测点组版本
                List<Long> groupIdList = daConfigGroupMapper.selectGroupIdListBySieCode(siecode);
                daConfigApplicationSyncService.daConfigGroupVersionUpgrade(groupIdList);
            }

        }

    }


    @Transactional(rollbackFor = Exception.class)
    public ModifySensorResponse modifySensor(SensorRegistrationRequest modifySensorRequest) throws Exception {
        ModifySensorResponse response = new ModifySensorResponse();
        modifySensorRequest.setDescription(StringUtils.isEmpty(modifySensorRequest.getDescription()) ? modifySensorRequest.getSiecode() : modifySensorRequest.getDescription());
        //查询原sensor是否存在
        DaConfigSensor sensor = daConfigSensorMapper.selectBySiecode(modifySensorRequest.getOrigSiecode());
        if (sensor == null) {
            response.setStatus(STATUS1);
            return response;
        }
        //只改描述
        if (modifySensorRequest.getTag().equals(sensor.getTag()) && modifySensorRequest.getConnectorId().equals(sensor.getConnectorId()) && modifySensorRequest.getOrigSiecode().equals(modifySensorRequest.getSiecode())) {
            daConfigSensorMapper.updateDescriptionBySiecode(modifySensorRequest.getSiecode(), modifySensorRequest.getDescription());
            // 更新相关app版本
            daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(modifySensorRequest.getApplicationId());
            // 更新相关的测点组版本
            List<Long> groupIdList = daConfigGroupMapper.selectGroupIdListBySieCode(modifySensorRequest.getSiecode());
            daConfigApplicationSyncService.daConfigGroupVersionUpgrade(groupIdList);
            response.setStatus(STATUS0);
            return response;
        }
        //查询修改后的siecode是否重复
        if (!modifySensorRequest.getOrigSiecode().equals(modifySensorRequest.getSiecode()) && null != daConfigSensorMapper.selectBySiecode(modifySensorRequest.getSiecode())) {
            response.setStatus(STATUS3);
            return response;
        }
        //查询原sensor是否被引用
        if (!CollectionUtils.isEmpty(daAppResourceUsageMapper.queryUseResourceBySensorOrGroup(modifySensorRequest.getOrigSiecode(), ObjectTypeEnum.SENSOR.getType()))) {
            response.setStatus(STATUS2);
            return response;
        }
        if (STATUS1.equals(sensor.getFromRegist())) {
            if (!modifySensorRequest.getTag().equals(sensor.getTag()) || !modifySensorRequest.getConnectorId().equals(sensor.getConnectorId())) {
                response.setStatus(STATUS6);
                return response;
            }
        }
        //只改描述和siecode的情况，直接修改
        if (modifySensorRequest.getTag().equals(sensor.getTag()) && modifySensorRequest.getConnectorId().equals(sensor.getConnectorId())) {
            //修改与sensorgroup关联
            daConfigSensorMapper.updateSiecodeOfSensorGroup(modifySensorRequest.getOrigSiecode(), modifySensorRequest.getSiecode());
            daConfigSensorMapper.updateSiecodeDescriptionBySiecode(modifySensorRequest.getSiecode(), modifySensorRequest.getDescription(), modifySensorRequest.getOrigSiecode());
            // 更新相关app版本
            daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(modifySensorRequest.getApplicationId());
            // 更新相关的测点组版本
            List<Long> groupIdList = daConfigGroupMapper.selectGroupIdListBySieCode(modifySensorRequest.getSiecode());
            daConfigApplicationSyncService.daConfigGroupVersionUpgrade(groupIdList);
            daConfigSensorMapper.updateValidRuleBySieCode(modifySensorRequest.getOrigSiecode(), modifySensorRequest.getSiecode());
            response.setStatus(STATUS0);
            return response;
        }


        DaConfigConnector connector = daConfigConnectorMapper.selectConnectorById(modifySensorRequest.getConnectorId());
        connector.setPassword(StringUtils.isBlank(connector.getPassword()) ? "" : stringEncryptor.decrypt(connector.getPassword()));
        DaConfigConnectorRequest connectorRequest = new DaConfigConnectorRequest();
        BeanUtils.copyProperties(connector, connectorRequest);
        //查询修改后的tag是否存在
        if (checkConnectorTagExist(modifySensorRequest.getTag(), connectorRequest)) {
            response.setStatus(STATUS4);
            return response;
        }
        //验证是否已导入sensor
        if (!CollectionUtils.isEmpty(daConfigSensorMapper.selectByAppAndConnectorAndTag(modifySensorRequest.getApplicationId(), modifySensorRequest.getConnectorId(), modifySensorRequest.getTag()))) {
            response.setStatus(STATUS5);
            return response;
        }

        //修改与sensorgroup关联
        daConfigSensorMapper.updateSiecodeOfSensorGroup(modifySensorRequest.getOrigSiecode(), modifySensorRequest.getSiecode());

        //修改sensor信息
        daConfigSensorMapper.updateSensorInfo(modifySensorRequest);

        //更新tag pool
        List<Long> appIds = new ArrayList<>();
        appIds.add(modifySensorRequest.getApplicationId());
        connectorRequest.setApplicationIdList(appIds);
        dataConnectorService.insertOrUpdateConnectorSensorList(connectorRequest, 1);

        // 更新相关app版本
        daConfigApplicationSyncService.daConfigApplicationVersionUpgrade(modifySensorRequest.getApplicationId());
        // 更新相关的测点组版本
        List<Long> groupIdList = daConfigGroupMapper.selectGroupIdListBySieCode(modifySensorRequest.getSiecode());
        daConfigApplicationSyncService.daConfigGroupVersionUpgrade(groupIdList);

        daConfigSensorMapper.updateValidRuleBySieCode(modifySensorRequest.getOrigSiecode(), modifySensorRequest.getSiecode());

        response.setStatus(STATUS0);
        return response;

    }

    private boolean checkConnectorTagExist(String tag, DaConfigConnectorRequest connectorRequest) {

        Map<String, String> resultData = null;
        try {
            resultData = dataConnectorService.selectTagListExact(connectorRequest, tag);
        } catch (Exception e) {
            logger.error("openplant selectTagListExact error : " + e.getMessage(),e);
        }
        if (null == resultData || resultData.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 初始化DaConfigSensor
     * @param data
     * @param applicationId
     * @param connectorId
     * @return
     */
    public DaConfigSensor initConfigSensor(RegistData data, Long applicationId, Long connectorId) {
        DaConfigSensor daConfigSensor = new DaConfigSensor();
        daConfigSensor.setSiecode(data.getSiecode());
        daConfigSensor.setConnectorId(connectorId);
        daConfigSensor.setTag(data.getTag());
        daConfigSensor.setAppId(applicationId);
        //init status and unit
        daConfigSensor.setStatus(DelStatus.IMPORTED.getType());
        daConfigSensor.setFromRegist(CommonConstant.STATUS1);
        daConfigSensor.setUnit(null);
        if (StringUtils.isBlank(data.getDescription())) {
            daConfigSensor.setDescription(data.getSiecode());
        } else {
            daConfigSensor.setDescription(data.getDescription());
        }
        return daConfigSensor;
    }
}
