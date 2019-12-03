package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.enums.ObjectTypeEnum;
import com.siemens.dasheng.web.enums.OpertionTypeEnum;
import com.siemens.dasheng.web.exception.BusinessException;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.model.DaAppResourceUsage;
import com.siemens.dasheng.web.model.DaConfigApplication;
import com.siemens.dasheng.web.model.DaConfigGroup;
import com.siemens.dasheng.web.model.TrendwarningValidsensorrule;
import com.siemens.dasheng.web.model.dto.AppSieCode;
import com.siemens.dasheng.web.request.ObjectUsageRequest;
import com.siemens.dasheng.web.response.ThirdResponse;
import com.siemens.dasheng.web.singleton.constant.CommonErrorConstant;
import com.siemens.dasheng.web.util.CollectionUtil;
import com.siemens.dasheng.web.util.NumberUtil;
import com.siemens.dasheng.web.util.RedisLock;
import com.siemens.dasheng.web.util.ServiceUtil;
import com.siemens.ofmcommon.enums.ErrorCodeEnum;
import com.siemens.ofmcommon.utils.HttpResponseUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.CommonErrorConstant.*;
import static com.siemens.ofmcommon.constant.HttpResponseConstant.*;
import static com.siemens.ofmcommon.enums.ErrorCodeEnum.PARAMETER_VALIDATE_ERROR;
import static com.siemens.ofmcommon.enums.ErrorCodeEnum.SERVER_UNKNOWN_ERROR;

/**
 * @author allan
 *         Created by z0041dpv on 5/5/2019.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AppThirdService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    /**
     * 不存在测点
     */
    private static final String ONE = "one";

    /**
     * 存在测点
     */
    private static final String TWO = "two";

    /**
     * 保存失败测点
     */
    private static final String THREE = "three";

    @Autowired
    private StringRedisTemplate redisOPCTemplate;

    @Autowired
    private DaAppResourceUsageMapper daAppResourceUsageMapper;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private DaConfigAppExtensionMapper daConfigAppExtensionMapper;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DaConfigGroupMapper daConfigGroupMapper;


    /**
     * batch opertation
     *
     * @param objectUsageRequest
     * @param modelMap
     * @param type
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelMap multiSensorUsage(ObjectUsageRequest objectUsageRequest, ModelMap modelMap, boolean type) throws Exception {

        ConcurrentHashMap<String, AtomicInteger> concurrentHashMap = new ConcurrentHashMap<>(16);
        //valid common data not null
        modelMap = vefifyObjectNotNull(objectUsageRequest, modelMap, true);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }
        modelMap = vefifyObjectSensorEnum(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }

        modelMap = vefifyObjectExist(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }

        //获取存在和不存在数据
        Map<String, List<String>> resutMap = filterObject(objectUsageRequest);
        List<String> notExistSensorList = resutMap.get(ONE);
        List<String> existSensorList = resutMap.get(TWO);
        //存在不合法的siecode 返回提示
        if (!CollectionUtils.isEmpty(notExistSensorList)) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, SERVER_UNKNOWN_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);

            ThirdResponse thirdResponse = new ThirdResponse();
            thirdResponse.setInValidSensorList(notExistSensorList);
            thirdResponse.setVaildSensorList(existSensorList);
            modelMap.put(DATA, thirdResponse);
            return modelMap;
        }

        List<String> failList = new ArrayList<>();
        List<String> okList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(existSensorList)) {

            List<DaAppResourceUsage> daAppResourceUsageList = daAppResourceUsageMapper.queryUseResourceList(objectUsageRequest.getConsumerObjectId(),
                    existSensorList, ObjectTypeEnum.SENSOR.getType(), objectUsageRequest.getAppId());

            if (type) {
                if (!CollectionUtils.isEmpty(daAppResourceUsageList)) {
                    List<String> longList = daAppResourceUsageList.stream().map(DaAppResourceUsage::getObjectid).collect(Collectors.toList());
                    failList.addAll(longList);
                    existSensorList.removeAll(longList);
                }

                //都可以保存
                if (!CollectionUtils.isEmpty(existSensorList)) {
                    commonOperation(existSensorList, type, objectUsageRequest, concurrentHashMap);
                }
            } else {
                //数据库没有要释放的
                if (CollectionUtils.isEmpty(daAppResourceUsageList)) {
                    failList.addAll(existSensorList);
                } else {
                    List<String> longList = daAppResourceUsageList.stream().map(DaAppResourceUsage::getObjectid).collect(Collectors.toList());

                    existSensorList.removeAll(longList);

                    failList.addAll(existSensorList);

                    if (!CollectionUtils.isEmpty(longList)) {
                        commonOperation(longList, type, objectUsageRequest, concurrentHashMap);
                    }
                }
            }
        }
        for (Map.Entry<String, AtomicInteger> entry : concurrentHashMap.entrySet()) {
            int count = entry.getValue().get();
            String objectId = entry.getKey();
            if (count > 0) {
                failList.add(objectId);
            } else if (count == 0) {
                okList.add(objectId);
            }
        }
        modelMap.put(SUCCESS, Boolean.TRUE);
        ThirdResponse thirdResponse = new ThirdResponse();
        thirdResponse.setFailSensorList(failList);
        thirdResponse.setOkSensorList(okList);
        modelMap.put(DATA, thirdResponse);

        return modelMap;
    }

    /**
     * 公有保存或者释放方法
     *
     * @param existSensorList
     * @param type
     * @param objectUsageRequest
     * @param concurrentHashMap
     */
    private void commonOperation(List<String> existSensorList, boolean type, ObjectUsageRequest objectUsageRequest
            , ConcurrentHashMap<String, AtomicInteger> concurrentHashMap) {

        if (!CollectionUtils.isEmpty(existSensorList)) {

            for (String objectId : existSensorList) {
                AtomicInteger atomicInteger = new AtomicInteger(0);
                concurrentHashMap.putIfAbsent(objectId, atomicInteger);
            }
            for (String objectId : existSensorList) {
                ObjectUsageRequest save = buildRequest(objectUsageRequest, objectId);
                try {
                    lockSave(save, type);
                } catch (Exception e) {
                    LOGGER.error("save into db error: " + e.getMessage(),e);
                    concurrentHashMap.get(objectId).incrementAndGet();
                }
            }
        }
    }


    /**
     * 构造
     *
     * @param objectUsageRequest
     * @return
     */
    private Map<String, List<String>> filterObject(ObjectUsageRequest objectUsageRequest) {
        Map<String, List<String>> resutMap = new HashMap<>(2);
        //存在的测点
        List<String> existSensorList = new ArrayList<>();
        if (objectUsageRequest != null) {
            List<String> objectIds = objectUsageRequest.getObjectIds();
            String appId = objectUsageRequest.getAppId();
            List<Long> appIds = new ArrayList<>();
            DaConfigApplication daConfigApplication = daConfigApplicationMapper.selectByFleetAppId(appId);
            //获取该应用继承appid
            Set<Long> extensionAppId = daConfigAppExtensionMapper.queryExtensionIdByAppId(daConfigApplication.getId());
            appIds.add(daConfigApplication.getId());
            if (!CollectionUtils.isEmpty(extensionAppId)) {
                appIds.addAll(extensionAppId);
            }
            String appIdStr = StringUtils.join(appIds, ",");

            String[] appIdStrs = appIdStr.split(",");
            String appIdSqlStr = ServiceUtil.mergeStrForIn(appIdStrs, "app_id");
            List<AppSieCode> appSieCodeList = daConfigSensorMapper.querySieCodeMap(appIdSqlStr);
            //未导入测点
            if (CollectionUtils.isEmpty(appSieCodeList)) {
                resutMap.put(ONE, objectIds);
                resutMap.put(TWO, existSensorList);
                return resutMap;

            } else {
                List<String> appSiecodes = appSieCodeList.stream().map(AppSieCode::getSieCode).collect(Collectors.toList());
                List<String> notExists = CollectionUtil.getAddaListThanbList(objectIds, appSiecodes);
                resutMap.put(ONE, notExists);
                List<String> tempObjectIds = new ArrayList<>();
                tempObjectIds.addAll(objectIds);

                tempObjectIds.removeAll(notExists);

                resutMap.put(TWO, tempObjectIds);
                return resutMap;
            }
        }

        return resutMap;
    }

    /**
     * sensorUsage
     *
     * @param objectUsageRequest
     * @param modelMap
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelMap sensorUsage(ObjectUsageRequest objectUsageRequest, ModelMap modelMap, boolean type) throws Exception {
        //valid common data not null
        modelMap = vefifyObjectNotNull(objectUsageRequest, modelMap, false);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }
        modelMap = vefifyObjectExist(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }

        modelMap = vefifyObjectSensorEnum(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }
        try {
            lockSave(objectUsageRequest, type);
        } catch (Exception e) {
            LOGGER.error("lock sensorUsage error : " + e.getMessage(),e);
        }
        return modelMap;
    }

    /**
     * 保存模块
     *
     * @param objectUsageRequest
     * @param type
     */
    public void lockSave(ObjectUsageRequest objectUsageRequest, boolean type) throws Exception {
        String appId = objectUsageRequest.getAppId();
        String objectId = objectUsageRequest.getObjectId();
        String objectType = objectUsageRequest.getObjectType();
        String consumerObjectId = objectUsageRequest.getConsumerObjectId();
        String opertionType = objectUsageRequest.getOpertionType();


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(appId);
        stringBuilder.append(objectId);
        stringBuilder.append(objectType);
        stringBuilder.append(consumerObjectId);
        stringBuilder.append(opertionType);

        String key = stringBuilder.toString();


        // redis lock
        RedisLock redisLock = new RedisLock(key, redisOPCTemplate);
        try {
            if (redisLock.lock()) {
                List<DaAppResourceUsage> daAppResourceUsages = buildDaAppResourceList(objectUsageRequest, ObjectTypeEnum.SENSOR.getType());
                if (type) {
                    if (OpertionTypeEnum.USE.getType().equals(opertionType)) {
                        //
                        List<DaAppResourceUsage> daAppResourceUsageList = daAppResourceUsageMapper.queryUseResource(consumerObjectId, objectId, objectType, appId);
                        if (!CollectionUtils.isEmpty(daAppResourceUsages)
                                && CollectionUtils.isEmpty(daAppResourceUsageList)) {
                            daAppResourceUsageMapper.saveList(daAppResourceUsages);
                        } else {
                            throw new BusinessException("The sensor is used by the app !", SENSOR_IS_USED);
                        }
                    }
                } else {
                    if (OpertionTypeEnum.RELEASE.getType().equals(opertionType)) {
                        daAppResourceUsageMapper.releaseResource(consumerObjectId, objectId, objectType, appId);
                    }
                }
            } else {
                if (OpertionTypeEnum.USE.getType().equals(opertionType)) {
                    throw new BusinessException("The sensor is used by the app !", SENSOR_IS_USED);
                } else if (OpertionTypeEnum.RELEASE.getType().equals(opertionType)) {
                    throw new BusinessException("The sensor is release by the app !", SENSOR_IS_RELEASE);
                } else {
                    throw new RuntimeException("unknown error!");
                }
            }
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }
    }

    /**
     * sensorGroupUsage
     *
     * @param objectUsageRequest
     * @param modelMap
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelMap sensorGroupUsage(ObjectUsageRequest objectUsageRequest, ModelMap modelMap, boolean type) throws Exception {
        //valid common data not null
        modelMap = vefifyObjectNotNull(objectUsageRequest, modelMap, false);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }
        modelMap = vefifyObjectExist(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }

        modelMap = vefifySensorGroupExist(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }


        String objectType = objectUsageRequest.getObjectType();
        if (!ObjectTypeEnum.SENSORGROUP.getType().equals(objectType)) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }

        String appId = objectUsageRequest.getAppId();
        String objectId = objectUsageRequest.getObjectId();
        String consumerObjectId = objectUsageRequest.getConsumerObjectId();
        String opertionType = objectUsageRequest.getOpertionType();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(appId);
        stringBuilder.append(objectId);
        stringBuilder.append(objectType);
        stringBuilder.append(consumerObjectId);
        stringBuilder.append(opertionType);

        String key = stringBuilder.toString();

        // redis lock
        RedisLock redisLock = new RedisLock(key, redisOPCTemplate);
        try {
            if (redisLock.lock()) {
                List<DaAppResourceUsage> daAppResourceUsages = buildDaAppResourceList(objectUsageRequest, ObjectTypeEnum.SENSORGROUP.getType());
                if (type) {
                    if (OpertionTypeEnum.USE.getType().equals(opertionType)) {
                        //
                        List<DaAppResourceUsage> daAppResourceUsageList = daAppResourceUsageMapper.queryUseResource(consumerObjectId, objectId, objectType, appId);
                        if (!CollectionUtils.isEmpty(daAppResourceUsages)
                                && CollectionUtils.isEmpty(daAppResourceUsageList)) {
                            daAppResourceUsageMapper.saveList(daAppResourceUsages);
                        } else {
                            throw new BusinessException("The sensor group is used by the app !", SENSOR_GROUP_IS_USED);
                        }
                    }
                } else {
                    if (OpertionTypeEnum.RELEASE.getType().equals(opertionType)) {
                        daAppResourceUsageMapper.releaseResource(consumerObjectId, objectId, objectType, appId);
                    }
                }

            } else {
                if (OpertionTypeEnum.USE.getType().equals(opertionType)) {
                    throw new BusinessException("The sensor group is used by the app !", SENSOR_GROUP_IS_USED);
                } else if (OpertionTypeEnum.RELEASE.getType().equals(opertionType)) {
                    throw new BusinessException("The sensor group  is release by the app !", SENSOR_GROUP_IS_RELEASE);
                } else {
                    throw new RuntimeException("unknown error!");
                }
            }
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }

        return modelMap;
    }

    /**
     * 校验参数是否非空
     *
     * @param objectUsageRequest
     * @param modelMap
     * @return
     */
    public ModelMap vefifyObjectNotNull(ObjectUsageRequest objectUsageRequest, ModelMap modelMap, boolean isBatch) {
        if (objectUsageRequest == null) {
            modelMap = buildCommonTip(modelMap);
            return modelMap;
        }
        String appId = objectUsageRequest.getAppId();
        if (StringUtils.isEmpty(appId)) {
            modelMap = buildCommonTip(modelMap);
            return modelMap;
        }
        if (isBatch) {
            List<String> objectIds = objectUsageRequest.getObjectIds();
            if (CollectionUtils.isEmpty(objectIds)) {
                modelMap = buildCommonTip(modelMap);
                return modelMap;
            }
        } else {
            String objectId = objectUsageRequest.getObjectId();
            if (StringUtils.isEmpty(objectId)) {
                modelMap = buildCommonTip(modelMap);
                return modelMap;
            }
        }

        String objectType = objectUsageRequest.getObjectType();
        if (StringUtils.isEmpty(objectType)) {
            modelMap = buildCommonTip(modelMap);
            return modelMap;
        }
        String opertionType = objectUsageRequest.getOpertionType();
        if (StringUtils.isEmpty(opertionType)) {
            modelMap = buildCommonTip(modelMap);
            return modelMap;
        }
        return modelMap;
    }

    /**
     * 校验参数对应值系统是否真实存在
     *
     * @param objectUsageRequest
     * @param modelMap
     * @return
     */
    public ModelMap vefifyObjectExist(ObjectUsageRequest objectUsageRequest, ModelMap modelMap) {
        DaConfigApplication daConfigApplication = daConfigApplicationMapper.selectByFleetAppId(objectUsageRequest.getAppId());
        if (daConfigApplication == null) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
        }
        return modelMap;
    }

    /**
     * 校验
     *
     * @param objectUsageRequest
     * @param modelMap
     * @return
     */
    public ModelMap vefifySensorGroupExist(ObjectUsageRequest objectUsageRequest, ModelMap modelMap) {

        String objectId = objectUsageRequest.getObjectId();
        Long objectIdL = 0L;
        if (NumberUtil.isInteger(objectId)) {
            objectIdL = Long.valueOf(objectId);
        }

        List<Long> appIds = new ArrayList<>();
        DaConfigApplication daConfigApplication = daConfigApplicationMapper.selectByFleetAppId(objectUsageRequest.getAppId());
        //获取该应用继承appid
        Set<Long> extensionAppId = daConfigAppExtensionMapper.queryExtensionIdByAppId(daConfigApplication.getId());
        appIds.add(daConfigApplication.getId());
        if (!CollectionUtils.isEmpty(extensionAppId)) {
            appIds.addAll(extensionAppId);
        }
        String appIdStr = StringUtils.join(appIds, ",");

        String[] appIdStrs = appIdStr.split(",");
        String appIdSqlStr = ServiceUtil.mergeStrForIn(appIdStrs, "app_id");

        List<DaConfigGroup> daConfigGroupList = daConfigGroupMapper.getByAppId(appIdSqlStr);
        if (CollectionUtils.isEmpty(daConfigGroupList)) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
        } else {
            List<Long> idList = daConfigGroupList.stream().map(DaConfigGroup::getId).collect(Collectors.toList());
            if (!idList.contains(objectIdL)) {
                modelMap.put(SUCCESS, Boolean.FALSE);
                modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            }
        }
        return modelMap;
    }


    /**
     * 校验测点操作类型
     *
     * @param objectUsageRequest
     * @param modelMap
     * @return
     */
    public ModelMap vefifyObjectSensorEnum(ObjectUsageRequest objectUsageRequest, ModelMap modelMap) {
        String objectType = objectUsageRequest.getObjectType();
        if (!ObjectTypeEnum.SENSOR.getType().equals(objectType)) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }
        String opertionType = objectUsageRequest.getOpertionType();
        if (!OpertionTypeEnum.RELEASE.getType().equals(opertionType)
                && !OpertionTypeEnum.USE.getType().equals(opertionType)) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }

        return modelMap;
    }

    /**
     * 构造app资源使用
     *
     * @param objectUsageRequest
     * @return
     */
    private List<DaAppResourceUsage> buildDaAppResourceList(ObjectUsageRequest objectUsageRequest, String type) {
        List<DaAppResourceUsage> daAppResourceUsages = new ArrayList<>(2);
        DaAppResourceUsage daAppResourceUsage = new DaAppResourceUsage();
        daAppResourceUsage.setObjectid(objectUsageRequest.getObjectId());
        if (ObjectTypeEnum.SENSOR.getType().equals(type)) {
            List<AppSieCode> appSieCodeList = daConfigSensorMapper.querySieCode(objectUsageRequest.getObjectId());
            if (!CollectionUtils.isEmpty(appSieCodeList)) {
                AppSieCode appSieCode = appSieCodeList.get(0);
                if (appSieCode != null) {
                    daAppResourceUsage.setOwnappid(appSieCode.getAppId());
                }
            }
        } else if (ObjectTypeEnum.SENSORGROUP.getType().equals(type)) {
            Long id = Long.valueOf(objectUsageRequest.getObjectId());
            DaConfigGroup daConfigGroup = daConfigGroupMapper.selectByPrimaryKey(id);
            if (daConfigGroup != null) {
                daAppResourceUsage.setOwnappid(daConfigGroup.getAppId());
            }
        } else if (ObjectTypeEnum.SENSORRULE.getType().equals(type)) {
            TrendwarningValidsensorrule validsensorrule = daAppResourceUsageMapper.getSensorRuleById(objectUsageRequest.getObjectId());
            if( null != validsensorrule){
                DaConfigApplication application = daConfigApplicationMapper.selectByFleetAppId(validsensorrule.getGlobalappid());
                daAppResourceUsage.setOwnappid(application == null ? null : application.getId());
            }
        }


        daAppResourceUsage.setObjecttype(objectUsageRequest.getObjectType());
        daAppResourceUsage.setCreatedate(System.currentTimeMillis());
        daAppResourceUsage.setAppid(objectUsageRequest.getAppId());
        daAppResourceUsage.setRelease("0");
        daAppResourceUsage.setConsumerobject(objectUsageRequest.getConsumerObject());
        daAppResourceUsage.setConsumerdescription(objectUsageRequest.getConsumerDescription());
        daAppResourceUsage.setConsumerobjectId(objectUsageRequest.getConsumerObjectId());
        daAppResourceUsages.add(daAppResourceUsage);

        return daAppResourceUsages;
    }

    /**
     * 构造请求
     *
     * @param objectUsageRequest
     * @param objectId
     * @return
     */
    private ObjectUsageRequest buildRequest(ObjectUsageRequest objectUsageRequest, String objectId) {
        ObjectUsageRequest result = new ObjectUsageRequest();
        result.setAppId(objectUsageRequest.getAppId());
        result.setObjectId(objectId);
        result.setObjectType(objectUsageRequest.getObjectType());
        result.setOpertionType(objectUsageRequest.getOpertionType());
        result.setConsumerObject(objectUsageRequest.getConsumerObject());
        result.setConsumerDescription(objectUsageRequest.getConsumerDescription());
        result.setConsumerObjectId(objectUsageRequest.getConsumerObjectId());
        return result;
    }

    /**
     * build common tip
     *
     * @param modelMap
     * @return
     */
    ModelMap buildCommonTip(ModelMap modelMap) {
        if (modelMap != null) {
            modelMap.put(SUCCESS, Boolean.FALSE);
            modelMap.put(CODE, PARAMETER_VALIDATE_ERROR.getCode());
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
        }
        return modelMap;
    }

    public ModelMap sensorRuleUsage(ObjectUsageRequest objectUsageRequest, ModelMap modelMap, boolean type) {
        //valid common data not null
        modelMap = vefifyObjectNotNull(objectUsageRequest, modelMap, false);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }
        modelMap = vefifyObjectExist(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }

        modelMap = vefifySensorRuleExist(objectUsageRequest, modelMap);
        if (!(Boolean) modelMap.get(SUCCESS)) {
            return modelMap;
        }


        String objectType = objectUsageRequest.getObjectType();
        if (!ObjectTypeEnum.SENSORRULE.getType().equals(objectType)) {
            return HttpResponseUtil.ofmFailResponseMap(PARAMETER_VALIDATE_ERROR);
        }

        String appId = objectUsageRequest.getAppId();
        String objectId = objectUsageRequest.getObjectId();
        String consumerObjectId = objectUsageRequest.getConsumerObjectId();
        String opertionType = objectUsageRequest.getOpertionType();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(appId);
        stringBuilder.append(objectId);
        stringBuilder.append(objectType);
        stringBuilder.append(consumerObjectId);
        stringBuilder.append(opertionType);

        String key = stringBuilder.toString();

        // redis lock
        RedisLock redisLock = new RedisLock(key, redisOPCTemplate);
        try {
            if (redisLock.lock()) {
                List<DaAppResourceUsage> daAppResourceUsages = buildDaAppResourceList(objectUsageRequest, ObjectTypeEnum.SENSORRULE.getType());
                if (type) {
                    if (OpertionTypeEnum.USE.getType().equals(opertionType)) {
                        //
                        List<DaAppResourceUsage> daAppResourceUsageList = daAppResourceUsageMapper.queryUseResource(consumerObjectId, objectId, objectType, appId);
                        if (!CollectionUtils.isEmpty(daAppResourceUsages)
                                && CollectionUtils.isEmpty(daAppResourceUsageList)) {
                            daAppResourceUsageMapper.saveList(daAppResourceUsages);
                        } else {
                            LOGGER.info("The sensor rule is used by the app !");
                            //throw new BusinessException("The sensor rule is used by the app !", SENSOR_RULE_IS_USED);
                        }
                    }
                } else {
                    if (OpertionTypeEnum.RELEASE.getType().equals(opertionType)) {
                        daAppResourceUsageMapper.releaseResource(consumerObjectId, objectId, objectType, appId);
                    }
                }

            } else {
                if (OpertionTypeEnum.USE.getType().equals(opertionType)) {
                    throw new BusinessException("The sensor rule is used by the app !", SENSOR_RULE_IS_USED);
                } else if (OpertionTypeEnum.RELEASE.getType().equals(opertionType)) {
                    throw new BusinessException("The sensor rule  is release by the app !", SENSOR_RULE_IS_RELEASE);
                } else {
                    throw new RuntimeException("unknown error!");
                }
            }
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }

        return modelMap;
    }

    private ModelMap vefifySensorRuleExist(ObjectUsageRequest objectUsageRequest, ModelMap modelMap) {

        String objectId = objectUsageRequest.getObjectId();


        List<Long> appIds = new ArrayList<>();
        DaConfigApplication daConfigApplication = daConfigApplicationMapper.selectByFleetAppId(objectUsageRequest.getAppId());
        //获取该应用继承appid
        Set<Long> extensionAppId = daConfigAppExtensionMapper.queryExtensionIdByAppId(daConfigApplication.getId());
        appIds.add(daConfigApplication.getId());
        if (!CollectionUtils.isEmpty(extensionAppId)) {
            appIds.addAll(extensionAppId);
        }

        List<DaConfigApplication> applicationList = daConfigApplicationMapper.selectByAppIds(appIds);
        if(CollectionUtils.isEmpty(applicationList)){
            return HttpResponseUtil.ofmFailResponseMap(PARAMETER_VALIDATE_ERROR);
        }
        List<String> globalAppIds = applicationList.stream().map(DaConfigApplication::getGlobalAppId).collect(Collectors.toList());

        //查询测点策略
        TrendwarningValidsensorrule sensorrule = daAppResourceUsageMapper.getSensorRuleById(objectId);
        if(null == sensorrule || !globalAppIds.contains(sensorrule.getGlobalappid())){
            return HttpResponseUtil.ofmFailResponseMap(PARAMETER_VALIDATE_ERROR);
        }
        return modelMap;
    }
}
