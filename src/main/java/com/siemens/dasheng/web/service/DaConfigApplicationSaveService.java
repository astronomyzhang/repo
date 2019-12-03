package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.apolloconfig.ApolloAppConfig;
import com.siemens.dasheng.web.enums.AppFlagEnum;
import com.siemens.dasheng.web.enums.AppScopeEnum;
import com.siemens.dasheng.web.enums.AvailableStatus;
import com.siemens.dasheng.web.enums.ObjectTypeEnum;
import com.siemens.dasheng.web.event.DaAppUpdateEvent;
import com.siemens.dasheng.web.exception.BusinessException;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.mapperfactory.DaConfigAppExtensionCommon;
import com.siemens.dasheng.web.mapperfactory.DaConfigAppProviderCommon;
import com.siemens.dasheng.web.mapperfactory.DaConfigApplicationMapperCommon;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.dto.*;
import com.siemens.dasheng.web.request.AppFilter;
import com.siemens.dasheng.web.request.AppLockRequest;
import com.siemens.dasheng.web.request.DaConfigApplicationRequest;
import com.siemens.dasheng.web.response.AppLockRespone;
import com.siemens.dasheng.web.response.ResponseObject;
import com.siemens.dasheng.web.singleton.constant.CommonErrorConstant;
import com.siemens.dasheng.web.util.CollectionUtil;
import com.siemens.dasheng.web.util.NumberUtil;
import com.siemens.dasheng.web.util.ServiceUtil;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.CommonErrorConstant.*;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.ATTACHMENT_MAP;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @author allan
 *         Created by ofm on 2019/4/3.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DaConfigApplicationSaveService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * DA_DATA_CENTER 数据库预置，不会变更
     */
    public static final String APPID = "OFM1425281313229372894";

    private static final String PAGE = "0";

    private static final String PAGE_SIZE = "500";

    private static final String DELETE = "delete";

    private static final String ADD = "add";

    private static final int REPEAT_NUM = 1;

    @Autowired
    @Resource(name = "ribbonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private DaConfigAppProviderMapper daConfigAppProviderMapper;

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;

    @Autowired
    private DaConfigApplicationMapperCommon daConfigApplicationMapperCommon;

    @Autowired
    private DaConfigAppExtensionMapper daConfigAppExtensionMapper;

    @Autowired
    private DaConfigAppExtensionCommon daConfigAppExtensionCommon;

    @Autowired
    private DaConfigAppProviderCommon daConfigAppProviderCommon;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private DataProviderService dataProviderService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DataApplicationService dataApplicationService;

    @Autowired
    private DaAppResourceUsageMapper daAppResourceUsageMapper;

    @Autowired
    private DaConfigGroupMapper daConfigGroupMapper;

    @Autowired
    private DaConfigApplicationSyncService daConfigApplicationSyncService;


    /**
     * 获取app信息
     *
     * @param id
     * @return
     */
    public AppInfo getAppInfo(Long id) {
        if (id == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String response = null;
        try {
            response = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getSsoServerName() + "/cas/clientApps/selectAppDetailById/" + id, null, String.class);
        } catch (Exception e) {
            throw new BusinessException("The sso-server service is fail", THIRD_SERVICE_FAIL);
        }

        ResponseObject responseObject = response == null ? null : JSON.parseObject(response, ResponseObject.class);
        if (responseObject != null) {
            if (responseObject.getData() != null) {
                AppInfo appInfo = JSON.parseObject(responseObject.getData(), AppInfo.class);
                return appInfo;
            }
        }
        return null;
    }

    /**
     * get App from Fleet Frame
     *
     * @param appFilter
     * @return
     */
    public List<AppInfo> selectAppList(AppFilter appFilter, boolean isFilter) {
        if (appFilter == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<AppFilter> entity = new HttpEntity<>(appFilter, headers);
        String response = null;
        try {
            response = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getSsoServerName() + "/cas/clientApps/selectAppList", entity, String.class);
        } catch (Exception e) {
            throw new BusinessException("The sso-server service is fail", THIRD_SERVICE_FAIL);
        }

        ResponseObject responseObject = response == null ? null : JSON.parseObject(response, ResponseObject.class);
        if (responseObject != null) {
            if (responseObject.getData() != null) {
                List<AppInfo> tempParseResult = JSON.parseArray(responseObject.getData(), AppInfo.class);
                if (!CollectionUtils.isEmpty(tempParseResult)) {
                    //适配下fullname
                    List<AppInfo> parseResult =  tempParseResult.stream().map(info -> {
                        info.setName(info.getFullname());
                        return info;
                    }).collect(Collectors.toList());

                    List<AppInfo> result = parseResult.stream().filter(app -> (AppFlagEnum.ZERO.getType().equals(app.getFlag())
                            || AppFlagEnum.ONE.getType().equals(app.getFlag())
                            || AppFlagEnum.THREE.getType().equals(app.getFlag()))).collect(Collectors.toList());
                    if (isFilter) {
                        if (!CollectionUtils.isEmpty(result)) {
                            //获取已经注册过的app
                            List<ExistAppId> existAppIdList = daConfigApplicationMapper.selectExistedAppId();
                            if (!CollectionUtils.isEmpty(existAppIdList)) {
                                List<Long> appIdList = existAppIdList.stream().map(ExistAppId::getAppId).collect(Collectors.toList());

                                return result.stream().filter(app -> (!appIdList.contains(app.getId()))).collect(Collectors.toList());
                            }

                        }
                    }
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * sso-server lock
     * @param appLockRequest
     * @return
     * @throws BusinessException
     */
    public boolean appLock(AppLockRequest appLockRequest) throws BusinessException {
        if (appLockRequest == null) {
            return false;
        }
        if (StringUtils.isEmpty(appLockRequest.getLockAppId())) {
            return false;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<AppLockRequest> entity = new HttpEntity<>(appLockRequest, headers);
        String response = null;
        try {
            response = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getSsoServerName() + "/api/app/v2/third-party/lock/register", entity, String.class);
        } catch (Exception e) {
            throw new BusinessException("The sso-server service is fail", THIRD_SERVICE_FAIL);
        }

        ResponseObject responseObject = response == null ? null : JSON.parseObject(response, ResponseObject.class);
        if (responseObject != null) {
            if (responseObject.getData() != null) {
                AppLockRespone appLockRespone = JSON.parseObject(responseObject.getData(), AppLockRespone.class);
                if (appLockRespone != null) {
                    if (appLockRespone.isSuccess()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 获取公用应用
     *
     * @return
     */
    public List<AppPublicInfo> selectPublicAppList(Long id) {
        List<AppPublicInfo> appInfoList = new ArrayList<>();
        List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectPublicAppList(AppScopeEnum.APP_PUBLIC.getType(), id);
        if (!CollectionUtils.isEmpty(daConfigApplications)) {
            List<Long> idList = daConfigApplications.stream().map(DaConfigApplication::getId).collect(Collectors.toList());
            Map<Long, Long> idAppIdMap = appIdMap(daConfigApplications);

            Map<Long, AppInfo> appInfoMap = mapAppInfo();
            AppPublicInfo appPublicInfo = null;
            for (Long l : idList) {
                Long thirdAppId = idAppIdMap.get(l);
                AppInfo temp = appInfoMap.get(thirdAppId);
                if (temp != null) {
                    appPublicInfo = new AppPublicInfo();
                    appPublicInfo.setId(l);
                    appPublicInfo.setAppId(thirdAppId);
                    appPublicInfo.setName(temp.getName());
                    appPublicInfo.setFlag(temp.getFlag());
                    appPublicInfo.setDescription(temp.getDescription());
                    appInfoList.add(appPublicInfo);
                }
            }
            Map<Long, Integer> appProviderNumMap = this.getAppProviderNum(idList);
            for (AppPublicInfo ai : appInfoList) {
                ai.setProviderNum(appProviderNumMap.get(ai.getId()) == null ? Integer.valueOf(0) : appProviderNumMap.get(ai.getId()));
            }
            fillAppAvailability(appInfoList);
        }

        return appInfoList;
    }

    /**
     * 获取应用对象连接器的数量
     *
     * @param idList
     * @return
     */
    public Map<Long, Integer> getAppProviderNum(List<Long> idList) {

        Map<Long, Integer> resultMap = new HashMap<>(64);
        if (!CollectionUtils.isEmpty(idList)) {
            String modelIds = "'" + StringUtils.join(idList, "','") + "'";

            String[] ruleStr = modelIds.split(",");
            String appIds = ServiceUtil.mergeStrForIn(ruleStr, "app_id");

            List<DaAppProviderMap> dpList = daConfigAppProviderMapper.getAppProviderMap(appIds);
            for (DaAppProviderMap da : dpList) {
                resultMap.put(da.getAppId(), da.getNum());
            }
        }
        return resultMap;
    }

    /**
     * 填充公共app可用率
     *
     * @param appPublicInfos
     */
    public void fillAppAvailability(List<AppPublicInfo> appPublicInfos) {
        if (!CollectionUtils.isEmpty(appPublicInfos)) {
            List<Long> appIds = appPublicInfos.stream().map(AppPublicInfo::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(appIds)) {
                List<DaConfigAppProvider> daConfigAppProviders = daConfigAppProviderMapper.selectByAppIds(appIds);
                Map<Long, List<DaConfigAppProvider>> providerMap = daConfigAppProviders.stream().
                        collect(Collectors.groupingBy(DaConfigAppProvider::getAppId));
                Map<Long, Set<Long>> appProviderMap = new HashMap<>(64);
                for (Map.Entry<Long, List<DaConfigAppProvider>> entry : providerMap.entrySet()) {
                    Long appId = entry.getKey();
                    List<Long> providerIds = entry.getValue().stream().map(DaConfigAppProvider::getProviderId).collect(Collectors.toList());
                    List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(providerIds);
                    if (!CollectionUtils.isEmpty(proConList)) {
                        List<Long> connectorIds = proConList.stream().map(DaConfigProviderConnector::getConnectorId).collect(Collectors.toList());

                        appProviderMap.put(appId, new HashSet<>(connectorIds));
                    }
                }
                Map<Long, String[]> proAvailMap = dataProviderService.struProAvailMap(appProviderMap, new HashMap<>(16));

                for (AppPublicInfo appPublicInfo : appPublicInfos) {
                    List<ProviderConnectorCount> providerConnectorCounts = daConfigAppProviderMapper.queryProviderConnectorCount(appPublicInfo.getId());
                    if (proAvailMap == null) {
                        appPublicInfo.setAvailabilityRate("0.00");
                        appPublicInfo.setAvailability(AvailableStatus.UNAVAILABLE.getType());
                    } else {
                        appPublicInfo.setAvailabilityRate((proAvailMap.get(appPublicInfo.getId()) == null) ? "0.00" : proAvailMap.get(appPublicInfo.getId())[0]);
                        appPublicInfo.setAvailability((proAvailMap.get(appPublicInfo.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(appPublicInfo.getId())[1]));
                    }
                    if (!CollectionUtils.isEmpty(providerConnectorCounts)) {
                        if (AvailableStatus.AVAILABLE.getType().equals(appPublicInfo.getAvailability())) {
                            appPublicInfo.setAvailability(AvailableStatus.PARTLY_AVAILABLE.getType());
                        }
                    }
                }
            }
        }

    }


    /**
     * 转换appInfo成map
     *
     * @return
     */
    public Map<Long, AppInfo> mapAppInfo() {
        Map<Long, AppInfo> resutlMap = new HashMap<>(64);
        //获取Fleet frame 所有app
        AppFilter appFilter = new AppFilter();
        appFilter.setPage(PAGE);
        appFilter.setPageSize(PAGE_SIZE);
        List<AppInfo> appInfoList = selectAppList(appFilter, false);
        if (!CollectionUtils.isEmpty(appInfoList)) {
            for (AppInfo appInfo : appInfoList) {
                resutlMap.put(appInfo.getId(), appInfo);
            }
        }
        return resutlMap;
    }

    /**
     * 注册app或者更新app
     *
     * @param daConfigApplicationRequest
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ModelMap registerApp(DaConfigApplicationRequest daConfigApplicationRequest, ModelMap modelMap, String userId) throws Exception {
        //校验保存参数
        modelMap = this.vefifyData(daConfigApplicationRequest, modelMap);
        if (!(Boolean) modelMap.get(IS_SUCCESS)) {
            if (modelMap.get(DATA) != null) {
                MultiProviderView multiProviderView = (MultiProviderView) modelMap.get(DATA);
                if (!multiProviderView.getStatus()) {
                    modelMap.put(IS_SUCCESS, Boolean.TRUE);
                }
            }
            return modelMap;
        }
        DaConfigApplication daConfigApplication = null;
        //新增
        if (daConfigApplicationRequest.getId() == null) {
            //保存app注册信息
            daConfigApplication = buildDaConfigApplication(daConfigApplicationRequest, userId);
            if (daConfigApplication != null) {
                daConfigApplicationMapper.insert(daConfigApplication);
            }

            List<Long> addExtensionIdList = daConfigApplicationRequest.getExtensionIds();
            if (!CollectionUtils.isEmpty(addExtensionIdList)) {
                List<DaConfigAppExtension> daConfigAppExtensions = buildDaConfigAppExtension(addExtensionIdList,
                        daConfigApplication.getId());
                if (!CollectionUtils.isEmpty(daConfigAppExtensions)) {
                    daConfigAppExtensionCommon.saveList(daConfigAppExtensions);
                }
            }

            List<Long> addProviderIdList = daConfigApplicationRequest.getProviderIds();
            if (!CollectionUtils.isEmpty(addProviderIdList)) {
                List<DaConfigAppProvider> daConfigAppProviders = buildDaConfigAppProvider(addProviderIdList,
                        daConfigApplication.getId());
                if (!CollectionUtils.isEmpty(daConfigAppProviders)) {
                    daConfigAppProviderCommon.saveList(daConfigAppProviders);
                }
            }
            //app 加锁
            if (!StringUtils.isEmpty(daConfigApplication.getGlobalAppId())) {
                AppLockRequest appLockRequest = new AppLockRequest();
                appLockRequest.setAppId(APPID);
                appLockRequest.setLockAppId(daConfigApplication.getGlobalAppId());
                this.appLock(appLockRequest);
            }
            //编辑
        } else {
            daConfigApplication = daConfigApplicationMapper.selectById(daConfigApplicationRequest.getId());
            //app已经被删除，不存在
            if (daConfigApplication == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.APP_IS_DELETED);
                return modelMap;
            }

            Set<Long> existAppExtensionList = daConfigAppExtensionMapper.queryExtensionIdByAppId(daConfigApplication.getId());

            List<Long> paramAppExtensionList = daConfigApplicationRequest.getExtensionIds();

            if (CollectionUtils.isEmpty(paramAppExtensionList)) {
                paramAppExtensionList = new ArrayList<>();
            }
            List<Long> saveExtensionList = new ArrayList<>(existAppExtensionList);

            //构造数据标志
            Map<String, List<Long>> resultMap = buildUpdateInfoMap(saveExtensionList, paramAppExtensionList);
            List<Long> addExtensionIdList = resultMap.get(ADD);
            List<Long> deleteExtensionIdList = resultMap.get(DELETE);

            //删除
            if (!CollectionUtils.isEmpty(deleteExtensionIdList)) {
                //校验删除的公共应用是否可以删除
                String deleteIds = StringUtils.join(deleteExtensionIdList, ",");

                String[] deleteIdStr = deleteIds.split(",");
                String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "a.ownappid");
                List<AppOwnSensorUsage> appSensorList = daAppResourceUsageMapper.queryAppOwnSensorUsage(deleteIdSqlStr,
                        daConfigApplication.getGlobalAppId(), ObjectTypeEnum.SENSOR.getType());
                if (!CollectionUtils.isEmpty(appSensorList)) {

                    List<Long> longList = appSensorList.stream().map(AppOwnSensorUsage::getAppId).collect(Collectors.toList());
                    int sum = appSensorList.stream().mapToInt(AppOwnSensorUsage::getUseCount).sum();
                    StringBuilder stringBuilder = new StringBuilder();
                    Map<Long, AppInfo> appInfoMap = mapAppInfo();
                    if (!CollectionUtils.isEmpty(longList)) {
                        for (int i = 0; i < longList.size(); i++) {
                            AppInfo temp = appInfoMap.get(longList.get(i));
                            if (i == 0) {
                                stringBuilder.append(temp.getName());
                            } else {
                                stringBuilder.append(", ");
                                stringBuilder.append(temp.getName());
                            }
                        }

                        Map map = new HashMap<>(5);
                        map.put("vaildAppName", stringBuilder.toString());
                        map.putIfAbsent("number", sum);
                        modelMap.put(MSG, VAILD_APP);
                        modelMap.put(IS_SUCCESS, Boolean.FALSE);
                        modelMap.put(ATTACHMENT_MAP, map);
                        return modelMap;
                    }
                }
                List<AppOwnSensorUsage> appSensorGroupList = daAppResourceUsageMapper.queryAppOwnSensorUsage(deleteIdSqlStr,
                        daConfigApplication.getGlobalAppId(), ObjectTypeEnum.SENSORGROUP.getType());

                if (!CollectionUtils.isEmpty(appSensorGroupList)) {

                    List<Long> longList = appSensorGroupList.stream().map(AppOwnSensorUsage::getAppId).collect(Collectors.toList());
                    int sum = appSensorGroupList.stream().mapToInt(AppOwnSensorUsage::getUseCount).sum();
                    StringBuilder stringBuilder = new StringBuilder();
                    Map<Long, AppInfo> appInfoMap = mapAppInfo();
                    if (!CollectionUtils.isEmpty(longList)) {
                        for (int i = 0; i < longList.size(); i++) {
                            AppInfo temp = appInfoMap.get(longList.get(i));
                            if (i == 0) {
                                stringBuilder.append(temp.getName());
                            } else {
                                stringBuilder.append(", ");
                                stringBuilder.append(temp.getName());
                            }
                        }

                        Map map = new HashMap<>(5);
                        map.put("vaildAppName", stringBuilder.toString());
                        map.putIfAbsent("number", sum);
                        modelMap.put(MSG, VAILD_APP_GROUP);
                        modelMap.put(IS_SUCCESS, Boolean.FALSE);
                        modelMap.put(ATTACHMENT_MAP, map);
                        return modelMap;
                    }
                }


                //验证私有app测点组是否使用了私有app的测点，如果未使用，清除测点组的测点
                String deleteAppIdStr = ServiceUtil.mergeStrForIn(deleteIdStr, "c.app_id");
                List<String> siecodeList = daConfigGroupMapper.selectSiceCodeAppId(daConfigApplication.getId(), deleteAppIdStr);

                if (!CollectionUtils.isEmpty(siecodeList)) {
                    //判断是否使用过了
                    List<DaAppResourceUsage> daAppResourceUsages = daAppResourceUsageMapper.queryUseResourcesBySensorOrGroupOrAppId(siecodeList, ObjectTypeEnum.SENSOR.getType(), daConfigApplication.getGlobalAppId());
                    //未使用解除私有app测点组的测点
                    if (CollectionUtils.isEmpty(daAppResourceUsages)) {
                        List<DaConfigGroup> daConfigGroups = daConfigGroupMapper.getBySingleAppId(daConfigApplication.getId());
                        if (!CollectionUtils.isEmpty(daConfigGroups)) {
                            List<Long> idList = daConfigGroups.stream().map(DaConfigGroup::getId).collect(Collectors.toList());
                            String gropuIdStr = StringUtils.join(idList, ",");

                            String[] groupIdStrs = gropuIdStr.split(",");
                            String groupIdSqlStr = ServiceUtil.mergeStrForIn(groupIdStrs, "group_id");


                            String siecodeStr =  "'" + StringUtils.join(siecodeList, "','") + "'";
                            String[] siecodeStrs = siecodeStr.split(",");
                            String siecodeSqlStr = ServiceUtil.mergeStrForIn(siecodeStrs, "sensor_code");

                            daConfigGroupMapper.delteSensorGroupBySensorCode(groupIdSqlStr, siecodeSqlStr);

                            //更新下机组版本号
                            daConfigApplicationSyncService.daConfigGroupVersionUpgrade(idList);
                        }
                    }
                }
            }

            Set<Long> existProviderIdList = daConfigAppProviderMapper.queryProviderIdByAppId(daConfigApplication.getId());

            List<Long> paramProviderList = daConfigApplicationRequest.getProviderIds();

            if (CollectionUtils.isEmpty(paramProviderList)) {
                paramProviderList = new ArrayList<>();
            }
            List<Long> saveProviderList = new ArrayList<>(existProviderIdList);

            //构造数据标志
            Map<String, List<Long>> resultMap1 = buildUpdateInfoMap(saveProviderList, paramProviderList);
            List<Long> addProviderIdList = resultMap1.get(ADD);
            List<Long> deleteProviderIdList = resultMap1.get(DELETE);


            //删除
            if (!CollectionUtils.isEmpty(deleteProviderIdList)) {
                //校验删除的提供者是否可以删除
                String deleteIds = StringUtils.join(deleteProviderIdList, ",");

                String[] deleteIdStr = deleteIds.split(",");
                String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "b.provider_id");

                List<ProviderUsage> providerSensorList = daAppResourceUsageMapper.queryProviderSensorList(deleteIdSqlStr, daConfigApplication.getId());

                if (!CollectionUtils.isEmpty(providerSensorList)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    StringBuilder stringBuilder1 = new StringBuilder();
                    for (int i = 0; i < providerSensorList.size(); i++) {
                        ProviderUsage temp = providerSensorList.get(i);
                        if (i == 0) {
                            stringBuilder.append(temp.getName());
                            stringBuilder1.append(temp.getUseCount());
                        } else {
                            stringBuilder.append(", ");
                            stringBuilder.append(temp.getName());

                            stringBuilder1.append(", ");
                            stringBuilder1.append(temp.getUseCount());
                        }
                    }

                    Map map = new HashMap<>(5);
                    map.put("vaildProviderName", stringBuilder.toString());
                    map.putIfAbsent("number", stringBuilder1.toString());
                    modelMap.put(MSG, VAILD_PROVIDER);
                    modelMap.put(IS_SUCCESS, Boolean.FALSE);
                    modelMap.put(ATTACHMENT_MAP, map);
                    return modelMap;
                }
            }

            //构造新增数据
            if (!CollectionUtils.isEmpty(addExtensionIdList)) {
                List<DaConfigAppExtension> daConfigAppExtensions = buildDaConfigAppExtension(addExtensionIdList,
                        daConfigApplication.getId());
                if (!CollectionUtils.isEmpty(daConfigAppExtensions)) {
                    daConfigAppExtensionCommon.saveList(daConfigAppExtensions);
                }
            }
            //构造新增数据
            if (!CollectionUtils.isEmpty(addProviderIdList)) {
                List<DaConfigAppProvider> daConfigAppProviders = buildDaConfigAppProvider(addProviderIdList,
                        daConfigApplication.getId());
                if (!CollectionUtils.isEmpty(daConfigAppProviders)) {
                    daConfigAppProviderCommon.saveList(daConfigAppProviders);
                }
            }
            if (!CollectionUtils.isEmpty(deleteExtensionIdList)) {
                //校验删除的公共应用是否可以删除
                String deleteIds = StringUtils.join(deleteExtensionIdList, ",");

                String[] deleteIdStr = deleteIds.split(",");

                String appIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "extension_app_id");
                daConfigAppExtensionMapper.deleteByAppIdAndExtensionIds(daConfigApplication.getId(), appIdSqlStr);
            }

            if (!CollectionUtils.isEmpty(deleteProviderIdList)) {
                //校验删除的提供者是否可以删除
                String deleteIds = StringUtils.join(deleteProviderIdList, ",");

                String[] deleteIdStr = deleteIds.split(",");
                String providerIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "provider_id");
                daConfigAppProviderMapper.deleteByAppIdAndProviderId(daConfigApplication.getId(), providerIdSqlStr);

                long initTagStartTime = System.currentTimeMillis();
                //删除自动更新tag pool
                dataApplicationService.initConnectorTag(daConfigApplication.getId());
                logger.info("init tag cost times = " + (System.currentTimeMillis() - initTagStartTime));

                long clearTagStartTime = System.currentTimeMillis();
                dataApplicationService.clearGhostTag(deleteProviderIdList, daConfigApplication.getId());
                logger.info("clear ghost tag cost times = " + (System.currentTimeMillis() - clearTagStartTime));
            }
            if (!StringUtils.isEmpty(daConfigApplicationRequest.getType())) {
                Integer appType = Integer.valueOf(daConfigApplicationRequest.getType());
                daConfigApplication.setType(Long.valueOf(appType));
                daConfigApplicationMapper.updateByPrimaryKey(daConfigApplication);
            }
        }

        modelMap.put(DATA, daConfigApplication.getId());
        // notice update event
        applicationEventPublisher.publishEvent(new DaAppUpdateEvent(daConfigApplication.getId() + ""));

        return modelMap;
    }

    /**
     * 构造导入连接器
     *
     * @param providerIds
     * @param appId
     * @return
     */
    public List<DaConfigAppProvider> buildDaConfigAppProvider(List<Long> providerIds,
                                                              Long appId) {
        List<DaConfigAppProvider> daConfigAppProviders = new ArrayList<>(32);
        if (providerIds != null && !CollectionUtils.isEmpty(providerIds)) {
            DaConfigAppProvider daConfigAppProvider = null;
            for (Long providerId : providerIds) {
                if (providerId != null) {
                    daConfigAppProvider = new DaConfigAppProvider();
                    daConfigAppProvider.setId(daConfigAppProviderCommon.selectId());
                    daConfigAppProvider.setAppId(appId);
                    daConfigAppProvider.setProviderId(providerId);
                    daConfigAppProviders.add(daConfigAppProvider);
                }
            }
        }
        return daConfigAppProviders;
    }

    /**
     * 构造公用继承app
     *
     * @param extensionIds
     * @param appId
     * @return
     */
    public List<DaConfigAppExtension> buildDaConfigAppExtension(List<Long> extensionIds,
                                                                Long appId) {
        List<DaConfigAppExtension> daConfigAppExtensions = new ArrayList<>(32);
        if (extensionIds != null && !CollectionUtils.isEmpty(extensionIds)) {
            DaConfigAppExtension daConfigAppExtension = null;
            for (Long extensionId : extensionIds) {
                if (extensionId != null) {
                    daConfigAppExtension = new DaConfigAppExtension();
                    daConfigAppExtension.setAppId(appId);
                    daConfigAppExtension.setExtensionAppId(extensionId);
                    daConfigAppExtension.setId(daConfigAppExtensionCommon.selectId());
                    daConfigAppExtensions.add(daConfigAppExtension);
                }
            }
        }

        return daConfigAppExtensions;
    }

    /**
     * 构造注册app
     *
     * @param daConfigApplicationRequest
     * @return
     */
    public DaConfigApplication buildDaConfigApplication(DaConfigApplicationRequest daConfigApplicationRequest, String userId) {
        DaConfigApplication daConfigApplication = new DaConfigApplication();
        daConfigApplication.setFfAppId(daConfigApplicationRequest.getAppId());
        Integer appType = Integer.valueOf(daConfigApplicationRequest.getType());
        daConfigApplication.setType(Long.valueOf(appType));
        daConfigApplication.setId(daConfigApplicationMapperCommon.selectId());
        daConfigApplication.setRegisterDate(System.currentTimeMillis());
        daConfigApplication.setRegisterUserId(userId);
        AppInfo appInfo = getAppInfo(daConfigApplicationRequest.getAppId());
        if (appInfo != null) {
            daConfigApplication.setGlobalAppId(appInfo.getAppid());
        }
        return daConfigApplication;
    }

    /**
     * 校验保存参数
     *
     * @param daConfigApplicationRequest
     * @param modelMap
     * @return
     */
    private ModelMap vefifyData(DaConfigApplicationRequest daConfigApplicationRequest, ModelMap modelMap) throws Exception {
        if (daConfigApplicationRequest == null) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }

        //验证注册的appId是否合法
        if (daConfigApplicationRequest.getAppId() == null) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        } else {
            AppInfo appInfo = getAppInfo(daConfigApplicationRequest.getAppId());
            if (appInfo == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.APP_IS_DELETED);
                return modelMap;
            }
            //是否已经注册
            List<ExistAppId> existAppIdList = daConfigApplicationMapper.selectByAppIdAndId(daConfigApplicationRequest.getAppId(), daConfigApplicationRequest.getId());
            if (!CollectionUtils.isEmpty(existAppIdList)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.APP_IS_REGISTED);
                return modelMap;
            }
        }

        //id不为空
        if (daConfigApplicationRequest.getId() != null) {
            DaConfigApplication daConfigApplication = daConfigApplicationMapper.selectById(daConfigApplicationRequest.getId());
            //app已经被删除，不存在
            if (daConfigApplication == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.APP_IS_DELETED);
                return modelMap;
            }
            if (!daConfigApplicationRequest.getAppId().equals(daConfigApplication.getFfAppId())) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
            Long id = daConfigApplication.getId();
            List<Long> extensionIds = daConfigApplicationRequest.getExtensionIds();
            if (!CollectionUtils.isEmpty(extensionIds)) {
                if (extensionIds.contains(id)) {
                    modelMap.put(IS_SUCCESS, Boolean.FALSE);
                    modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                    return modelMap;
                }
            }
            String typeStr = daConfigApplicationRequest.getType();
            Integer type = Integer.valueOf(typeStr);
            Integer exType = daConfigApplication.getType().intValue();
            if (AppScopeEnum.APP_PRIVATE.getType().equals(type)
                    && AppScopeEnum.APP_PUBLIC.getType().equals(exType)) {
                //校验是否被引用
                List<DaConfigAppExtension> daConfigAppExtensions = daConfigAppExtensionMapper.selectByExtensionAppId(daConfigApplication.getId());
                if (!CollectionUtils.isEmpty(daConfigAppExtensions)) {
                    List<Long> appIdList = daConfigAppExtensions.stream().map(DaConfigAppExtension::getAppId).collect(Collectors.toList());

                    List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectByAppIds(appIdList);

                    List<Long> ffAppIdsList = daConfigApplications.stream().map(DaConfigApplication::getFfAppId).collect(Collectors.toList());

                    Map<Long, AppInfo> idInfoMap = mapAppInfo();
                    StringBuilder inheritAppName = new StringBuilder();
                    if (!CollectionUtils.isEmpty(ffAppIdsList)) {
                        for (int i = 0; i < ffAppIdsList.size(); i++) {
                            AppInfo appInfo = idInfoMap.get(ffAppIdsList.get(i));
                            if (i == 0) {
                                inheritAppName.append(appInfo.getName());
                            } else {
                                inheritAppName.append(", ");
                                inheritAppName.append(appInfo.getName());
                            }
                        }
                        Map map = new HashMap<>(5);
                        map.put("inheritAppName", inheritAppName.toString());
                        map.put("number", appIdList.size());
                        modelMap.put(MSG, ACCESS_COPE_ILLEGAL);
                        modelMap.put(IS_SUCCESS, Boolean.FALSE);
                        modelMap.put(ATTACHMENT_MAP, map);
                    }

                }
            }
        }
        if (daConfigApplicationRequest.getAppType() == null) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        } else {
            String appTypeStr = daConfigApplicationRequest.getAppType();
            if (!NumberUtil.isInteger(appTypeStr)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
            Integer appType = Integer.valueOf(appTypeStr);
            if (!(AppFlagEnum.ONE.getType().equals(appType)
                    || AppFlagEnum.THREE.getType().equals(appType)
                    || AppFlagEnum.ZERO.getType().equals(appType))) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
        }
        if (daConfigApplicationRequest.getType() == null) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        } else {
            String typeStr = daConfigApplicationRequest.getType();
            if (!NumberUtil.isInteger(typeStr)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
            Integer type = Integer.valueOf(typeStr);
            if (!(AppScopeEnum.APP_PRIVATE.getType().equals(type) || AppScopeEnum.APP_PUBLIC.getType().equals(type))) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
            if (AppScopeEnum.APP_PUBLIC.getType().equals(type)) {
                if (!CollectionUtils.isEmpty(daConfigApplicationRequest.getExtensionIds())) {
                    modelMap.put(IS_SUCCESS, Boolean.FALSE);
                    modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                    return modelMap;
                }
            }
        }
        String notExistProviderStr = this.exceptProvider(daConfigApplicationRequest);
        if (StringUtils.isNotBlank(notExistProviderStr)) {
            Map map = new HashMap<>(5);
            map.put("illegalProviderId", notExistProviderStr);
            modelMap.put(MSG, PROVIDER_ILLEGAL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(ATTACHMENT_MAP, map);
            return modelMap;
        }
        String notExtensionStr = exceptExtension(daConfigApplicationRequest);
        if (StringUtils.isNotBlank(notExtensionStr)) {
            Map map = new HashMap<>(5);
            map.put("illegalExtensionId", notExtensionStr);
            modelMap.put(MSG, EXTENSION_ILLEGAL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(ATTACHMENT_MAP, map);
            return modelMap;
        }
        Map<Long, String> providerIdNameMap = new HashMap<>(16);
        Map<Long, String> connectorIdNameMap = new HashMap<>(16);
        List<Map<Long, List<String>>> mapList = new ArrayList<>();
        Map<Long, Map<String, String>> connectmap = new HashMap<>(16);
        getProviderInfo(daConfigApplicationRequest, providerIdNameMap, connectorIdNameMap, mapList, connectmap);

        if (!CollectionUtils.isEmpty(mapList)) {

            List<MultiConnecotrInfoList> multiConnecotrInfoLists = exceptMultiConnector2(mapList, providerIdNameMap, connectmap);
            if (!CollectionUtils.isEmpty(multiConnecotrInfoLists)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                MultiProviderView multiProviderView = new MultiProviderView();
                multiProviderView.setStatus(Boolean.FALSE);
                multiProviderView.setMultiConnecotrInfoLists(multiConnecotrInfoLists);
                modelMap.put(DATA, multiProviderView);
            }
        }

        return modelMap;
    }

    /**
     * 返回不合法公共应用
     *
     * @param daConfigApplicationRequest
     * @return
     */
    public String exceptExtension(DaConfigApplicationRequest daConfigApplicationRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Long> extensionIds = daConfigApplicationRequest.getExtensionIds();
        if (!CollectionUtils.isEmpty(extensionIds)) {
            List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectByAppIds(extensionIds);
            if (!CollectionUtils.isEmpty(daConfigApplications)) {

                List<Long> existPIdList = daConfigApplications.stream().map(DaConfigApplication::getId).collect(Collectors.toList());
                List<DaConfigApplication> statusP = daConfigApplicationMapper.selectByIdsAndType(extensionIds, 1L);
                if (!CollectionUtils.isEmpty(statusP)) {
                    List<Long> longList = statusP.stream().map(DaConfigApplication::getId).collect(Collectors.toList());
                    existPIdList.removeAll(longList);
                }
                if (!CollectionUtils.isEmpty(existPIdList)) {
                    Map<Long, AppInfo> appInfoMap = mapAppInfo();
                    List<DaConfigApplication> daConfigApplicationList = daConfigApplicationMapper.selectByAppIds(existPIdList);
                    Map<Long, Long> idAppIdMap = appIdMap(daConfigApplicationList);

                    for (int i = 0; i < existPIdList.size(); i++) {
                        Long appId = existPIdList.get(i);
                        Long thirdAppId = idAppIdMap.get(appId);
                        AppInfo appInfo = appInfoMap.get(thirdAppId);
                        if (i == 0) {
                            stringBuilder.append(appInfo.getName());
                        } else {
                            stringBuilder.append(",");
                            stringBuilder.append(appInfo.getName());
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 返回不合法连接器ID
     *
     * @param daConfigApplicationRequest
     * @return
     */
    public String exceptProvider(DaConfigApplicationRequest daConfigApplicationRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Long> providerIds = daConfigApplicationRequest.getProviderIds();
        if (!CollectionUtils.isEmpty(providerIds)) {
            List<DaConfigProvider> daConfigProviders = daConfigProviderMapper.selectByIds(providerIds);
            if (!CollectionUtils.isEmpty(daConfigProviders)) {
                //转map
                Map<Long, DaConfigProvider> appleMap = daConfigProviders.stream().collect(Collectors.toMap(DaConfigProvider::getId, a -> a, (k1, k2) -> k1));

                List<Long> existPIdList = daConfigProviders.stream().map(DaConfigProvider::getId).collect(Collectors.toList());
                List<DaConfigProvider> statusP = daConfigProviderMapper.selectByIdsAndStatus(providerIds, 1L);
                if (!CollectionUtils.isEmpty(statusP)) {
                    List<Long> longList = statusP.stream().map(DaConfigProvider::getId).collect(Collectors.toList());
                    existPIdList.removeAll(longList);
                }

                for (int i = 0; i < existPIdList.size(); i++) {
                    Long appId = existPIdList.get(i);
                    DaConfigProvider modelVersion = appleMap.get(appId);
                    if (i == 0) {
                        stringBuilder.append(modelVersion.getName());
                    } else {
                        stringBuilder.append(",");
                        stringBuilder.append(modelVersion.getName());
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 获取应用继承的app
     *
     * @param appId
     * @return
     */
    public List<AppInfoExtension> inheritAppList(Long appId) {
        List<AppInfoExtension> appInfoList = new ArrayList<>();
        if (appId == null) {
            return appInfoList;
        }
        List<DaConfigAppExtension> daConfigAppExtensions = daConfigAppExtensionMapper.selectByAppId(appId);
        if (!CollectionUtils.isEmpty(daConfigAppExtensions)) {
            List<Long> longList = daConfigAppExtensions.stream().map(DaConfigAppExtension::getExtensionAppId).collect(Collectors.toList());
            List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectByAppIds(longList);
            Map<Long, Long> idAppIdMap = appIdMap(daConfigApplications);

            Map<Long, AppInfo> appInfoMap = mapAppInfo();
            AppInfoExtension appInfoExtension = null;
            for (Long aId : longList) {
                Long thirdAppId = idAppIdMap.get(aId);
                AppInfo appInfo = appInfoMap.get(thirdAppId);
                if (appInfo != null) {
                    appInfoExtension = new AppInfoExtension();
                    appInfoExtension.setId(aId);
                    appInfoExtension.setAppId(thirdAppId);
                    appInfoExtension.setFlag(appInfo.getFlag());
                    appInfoExtension.setName(appInfo.getName());
                    appInfoList.add(appInfoExtension);
                }
            }
        }
        return appInfoList;
    }

    /**
     * id和appid map
     *
     * @param daConfigApplications
     * @return
     */
    public Map<Long, Long> appIdMap(List<DaConfigApplication> daConfigApplications) {
        return daConfigApplications.stream().collect(Collectors.toMap(DaConfigApplication::getId, DaConfigApplication::getFfAppId, (k1, k2) -> k1));
    }

    /**
     * 筛选多个连接器相互之间有没有重复连接器
     *
     * @param providerConnectorMapList
     * @return
     */
    public String exceptMultiConnector(List<Map<Long, List<String>>> providerConnectorMapList) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean flag = false;
        if (!CollectionUtils.isEmpty(providerConnectorMapList)) {
            for (int i = 0; i < providerConnectorMapList.size(); i++) {
                if (flag) {
                    break;
                }
                for (int j = i + 1; j < providerConnectorMapList.size(); j++) {
                    Map<Long, List<String>> first = providerConnectorMapList.get(i);
                    Map<Long, List<String>> end = providerConnectorMapList.get(j);
                    Set<Long> firstKeySet = first.keySet();
                    Long firstKey = firstKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                    List<String> firstValue = first.get(firstKey);

                    Set<Long> endKeySet = end.keySet();
                    Long endKey = endKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                    List<String> endValue = end.get(endKey);

                    if (!Collections.disjoint(firstValue, endValue)) {
                        DaConfigProvider firstProvider = daConfigProviderMapper.queryById(firstKey);
                        DaConfigProvider endProvider = daConfigProviderMapper.queryById(endKey);
                        if (firstProvider != null) {
                            stringBuilder.append(firstProvider.getName());
                        }
                        if (endProvider != null) {
                            stringBuilder.append(",");
                            stringBuilder.append(endProvider.getName());
                        }
                        flag = true;
                        break;
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 构造提供者连接器
     *
     * @param daConfigApplicationRequest
     * @return
     */
    public List<Map<Long, List<String>>> getProviderConnector(DaConfigApplicationRequest daConfigApplicationRequest) {
        List<Map<Long, List<String>>> mapList = new ArrayList<>(32);
        List<Long> providerIds = daConfigApplicationRequest.getProviderIds();
        if (!CollectionUtils.isEmpty(providerIds)) {
            List<DaConfigProvider> statusP = daConfigProviderMapper.selectByIdsAndStatus(providerIds, 1L);
            if (!CollectionUtils.isEmpty(statusP)) {
                List<Long> longList = statusP.stream().map(DaConfigProvider::getId).collect(Collectors.toList());
                for (Long providerId : longList) {
                    Map<Long, List<String>> temp = null;
                    List<DaConfigConnector> daConfigConnectors = daConfigConnectorMapper.queryConnectorListByProviderId(providerId);
                    if (!CollectionUtils.isEmpty(daConfigConnectors)) {
                        temp = new HashMap<>(16);
                        List<String> result = daConfigConnectors.stream().map(DaConfigConnector::getConnectorInfo).collect(Collectors.toList());
                        temp.put(providerId, result);
                    }
                    if (temp != null) {
                        mapList.add(temp);
                    }
                }
            }
        }
        return mapList;
    }

    /**
     * 返回provider重复connector
     *
     * @param providerConnectorMapList
     * @param providerIdNameMap
     * @param connectorIdNameMap
     * @param connectmap
     * @return
     */
    public List<MultiProivderInfo> exceptMultiConnector(List<Map<Long, List<String>>> providerConnectorMapList,
                                                        Map<Long, String> providerIdNameMap,
                                                        Map<Long, String> connectorIdNameMap,
                                                        Map<Long, Map<String, String>> connectmap) {
        List<MultiProivderInfo> multiProivderInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(providerConnectorMapList)) {
            for (int i = 0; i < providerConnectorMapList.size(); i++) {
                Map<Long, List<String>> first = providerConnectorMapList.get(i);
                for (int j = i + 1; j < providerConnectorMapList.size(); j++) {
                    Map<Long, List<String>> end = providerConnectorMapList.get(j);
                    Set<Long> firstKeySet = first.keySet();
                    Long firstKey = firstKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                    List<String> firstValue = first.get(firstKey);

                    Set<Long> endKeySet = end.keySet();
                    Long endKey = endKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                    List<String> endValue = end.get(endKey);

                    List<String> intersection = firstValue.stream().filter(item -> endValue.contains(item)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(intersection)) {
                        String preName = providerIdNameMap.get(firstKey);
                        String nextName = providerIdNameMap.get(endKey);
                        MultiProivderInfo multiProivderInfo = buildMultiProvider(preName, nextName, intersection,
                                firstKey, endKey, connectmap);
                        multiProivderInfoList.add(multiProivderInfo);
                    }
                }
            }
        }
        return multiProivderInfoList;
    }


    /**
     * 返回provider重复connector
     *
     * @param providerConnectorMapList
     * @param providerIdNameMap
     * @param connectmap
     * @return
     */
    public List<MultiConnecotrInfoList> exceptMultiConnector2(List<Map<Long, List<String>>> providerConnectorMapList,
                                                              Map<Long, String> providerIdNameMap,
                                                              Map<Long, Map<String, String>> connectmap) {
        List<MultiConnecotrInfoList> multiConnecotrInfoLists = new ArrayList<>();

        //计数connector是否下次比较
        ConcurrentHashMap<String, AtomicInteger> concurrentHashMap = new ConcurrentHashMap<>(16);

        if (!CollectionUtils.isEmpty(providerConnectorMapList)) {
            for (int i = 0; i < providerConnectorMapList.size(); i++) {
                Map<Long, List<String>> first = providerConnectorMapList.get(i);
                Set<Long> firstKeySet = first.keySet();
                Long firstKey = firstKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                //第一个provider名称
                String firstProviderName = providerIdNameMap.get(firstKey);
                List<String> firstValue = first.get(firstKey);
                List<MultiProviderItem> items = null;

                Map<String, String> firstConnectorNameMap = connectmap.get(firstKey);

                for (String conn : firstValue) {
                    items = new ArrayList<>();
                    String firstConnectName = firstConnectorNameMap.get(conn);

                    if (concurrentHashMap.get(firstConnectName) != null) {
                        continue;
                    }
                    MultiProviderItem item = new MultiProviderItem();
                    item.setProviderName(firstProviderName);
                    item.setConnectorName(firstConnectName);
                    items.add(item);

                    for (int j = i + 1; j < providerConnectorMapList.size(); j++) {
                        Map<Long, List<String>> end = providerConnectorMapList.get(j);
                        Set<Long> endKeySet = end.keySet();
                        Long endKey = endKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                        //后续provider名称
                        String sencondyProviderName = providerIdNameMap.get(endKey);
                        List<String> endValue = end.get(endKey);
                        Map<String, String> connectorNameMap = connectmap.get(endKey);
                        if (endValue.contains(conn)) {
                            String connectName = connectorNameMap.get(conn);
                            AtomicInteger atomicInteger = concurrentHashMap.get(connectName);
                            if (atomicInteger == null) {
                                atomicInteger = new AtomicInteger(0);
                                atomicInteger.incrementAndGet();
                                concurrentHashMap.putIfAbsent(connectName, atomicInteger);

                            }
                            if (!merge(items, connectName, sencondyProviderName)) {
                                MultiProviderItem itemSe = new MultiProviderItem();
                                itemSe.setConnectorName(connectName);
                                itemSe.setProviderName(sencondyProviderName);
                                items.add(itemSe);
                            }
                        }
                    }
                    if (!CollectionUtils.isEmpty(items) && items.size() > REPEAT_NUM) {
                        MultiConnecotrInfoList temp = new MultiConnecotrInfoList();
                        temp.setItems(items);
                        multiConnecotrInfoLists.add(temp);
                    } else if (!CollectionUtils.isEmpty(items) && items.size() == REPEAT_NUM){
                        MultiProviderItem iitem = items.get(0);
                        if (iitem != null) {
                            String[] splitStr = iitem.getProviderName().split(",");
                            if (splitStr.length > REPEAT_NUM) {
                                MultiConnecotrInfoList temp = new MultiConnecotrInfoList();
                                temp.setItems(items);
                                multiConnecotrInfoLists.add(temp);
                            }
                        }
                    }
                }
            }
        }
        return multiConnecotrInfoLists;
    }

    /**
     * 合并
     *
     * @param items
     * @param connectName
     * @param sencondyProviderName
     * @return
     */
    private boolean merge(List<MultiProviderItem> items, String connectName, String sencondyProviderName) {
        boolean flag = false;
        for (MultiProviderItem item : items) {
            if (item.getConnectorName().equals(connectName)) {
                flag = true;
                item.setProviderName(item.getProviderName() + "," + sencondyProviderName);
                return flag;
            }
        }
        return flag;
    }


    /**
     * @param providerConnectorMapList
     * @param providerIdNameMap
     * @param connectorIdNameMap
     * @param connectmap
     * @return
     */
    public List<MultiConnectorInfo> exceptMultiConnector1(List<Map<Long, List<String>>> providerConnectorMapList,
                                                          Map<Long, String> providerIdNameMap,
                                                          Map<Long, String> connectorIdNameMap,
                                                          Map<Long, Map<String, String>> connectmap) {
        List<MultiConnectorInfo> multiProivderInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(providerConnectorMapList)) {
            for (int i = 0; i < providerConnectorMapList.size(); i++) {
                Map<Long, List<String>> first = providerConnectorMapList.get(i);
                Set<Long> firstKeySet = first.keySet();
                Long firstKey = firstKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                List<String> firstValue = first.get(firstKey);

                for (String conn : firstValue) {

                    List<Long> repeatProvider = new ArrayList<>();
                    for (int j = i + 1; j < providerConnectorMapList.size(); j++) {
                        Map<Long, List<String>> end = providerConnectorMapList.get(j);
                        Set<Long> endKeySet = end.keySet();
                        Long endKey = endKeySet.stream().limit(1).collect(Collectors.toList()).get(0);
                        List<String> endValue = end.get(endKey);

                        if (endValue.contains(conn)) {
                            repeatProvider.add(endKey);
                        }
                    }
                    repeatProvider.add(firstKey);
                    if (!CollectionUtils.isEmpty(repeatProvider) && repeatProvider.size() > REPEAT_NUM) {
                        MultiConnectorInfo multiConnectorInfo = buildMultiProvider1(firstKey, conn, repeatProvider, providerIdNameMap,
                                connectmap);
                        multiProivderInfoList.add(multiConnectorInfo);
                    }
                }
            }
        }
        return multiProivderInfoList;
    }

    /**
     * buildMultiProvider1
     *
     * @param firstKey
     * @param connectorName
     * @param repeatProvider
     * @param providerIdNameMap
     * @param connectmap
     * @return
     */
    private MultiConnectorInfo buildMultiProvider1(Long firstKey, String connectorName, List<Long> repeatProvider, Map<Long, String> providerIdNameMap,
                                                   Map<Long, Map<String, String>> connectmap) {
        MultiConnectorInfo multiConnectorInfo = new MultiConnectorInfo();
        Map<String, String> firstMap = connectmap.get(firstKey);
        String repeat = firstMap.get(connectorName);
        multiConnectorInfo.setConnectorName(repeat);

        Set<String> repeatNameSet = new HashSet<>();
        for (Long p : repeatProvider) {
            repeatNameSet.add(providerIdNameMap.get(p));
        }
        String repeatNameStr = String.join(",", repeatNameSet);
        multiConnectorInfo.setRepeatProviderName(repeatNameStr);

        return multiConnectorInfo;
    }

    /**
     * buildMultiProvider
     *
     * @param preName
     * @param nextName
     * @param intersection
     * @param firstKey
     * @param endKey
     * @param connectmap
     * @return
     */
    private MultiProivderInfo buildMultiProvider(String preName, String nextName, List<String> intersection,
                                                 Long firstKey, Long endKey, Map<Long, Map<String, String>> connectmap) {
        MultiProivderInfo multiProivderInfo = new MultiProivderInfo();
        multiProivderInfo.setPreName(preName);
        multiProivderInfo.setNextName(nextName);
        Set<String> repeatNameSet = new HashSet<>();

        for (String inter : intersection) {
            Map<String, String> firstMap = connectmap.get(firstKey);
            String repeat = firstMap.get(inter);
            if (StringUtils.isNotEmpty(repeat)) {
                repeatNameSet.add(repeat);
            }
            Map<String, String> endMap = connectmap.get(endKey);
            repeat = endMap.get(inter);
            if (StringUtils.isNotEmpty(repeat)) {
                repeatNameSet.add(repeat);
            }
        }
        String repeatNameStr = String.join(",", repeatNameSet);
        multiProivderInfo.setRepeatConnectorName(repeatNameStr);
        return multiProivderInfo;
    }


    /**
     * get provider info
     *
     * @param daConfigApplicationRequest
     * @param providerIdNameMap
     * @param connectorIdNameMap
     * @param mapList
     * @param connectmap
     */
    public void getProviderInfo(DaConfigApplicationRequest daConfigApplicationRequest, Map<Long, String> providerIdNameMap,
                                Map<Long, String> connectorIdNameMap, List<Map<Long, List<String>>> mapList,
                                Map<Long, Map<String, String>> connectmap) {

        List<Long> providerIds = daConfigApplicationRequest.getProviderIds();
        if (!CollectionUtils.isEmpty(providerIds)) {
            List<DaConfigProvider> statusP = daConfigProviderMapper.selectByIdsAndStatus(providerIds, 1L);
            if (!CollectionUtils.isEmpty(statusP)) {
                Map<Long, String> idMap = statusP.stream().collect(Collectors.toMap(e -> e.getId(), e -> e.getName()));
                providerIdNameMap.putAll(idMap);
                List<Long> longList = statusP.stream().map(DaConfigProvider::getId).collect(Collectors.toList());
                for (Long providerId : longList) {
                    Map<Long, List<String>> temp = null;
                    List<DaConfigConnector> daConfigConnectors = daConfigConnectorMapper.queryConnectorListByProviderId(providerId);
                    if (!CollectionUtils.isEmpty(daConfigConnectors)) {
                        temp = new HashMap<>(16);
                        List<String> result = daConfigConnectors.stream().map(DaConfigConnector::getConnectorInfo).collect(Collectors.toList());
                        Map<String, String> connectorMap = daConfigConnectors.stream().collect(Collectors.toMap(e -> e.getConnectorInfo(), e -> e.getName()));
                        connectmap.put(providerId, connectorMap);
                        Map<Long, String> idNameMap = daConfigConnectors.stream().collect(Collectors.toMap(e -> e.getId(), e -> e.getName()));
                        connectorIdNameMap.putAll(idNameMap);
                        temp.put(providerId, result);
                    }
                    if (temp != null) {
                        mapList.add(temp);
                    }
                }
            }
        }
    }


    /**
     * 构造数据标志
     *
     * @param saveList  已经保存数据
     * @param paramList 页面传过来的数据
     * @return
     */
    private Map<String, List<Long>> buildUpdateInfoMap(List<Long> saveList, List<Long> paramList) {
        Map<String, List<Long>> resutMap = new HashMap<>(2);
        resutMap.put(ADD, CollectionUtil.getAddaListThanbList(paramList, saveList));
        resutMap.put(DELETE, CollectionUtil.getReduceaListThanbList(paramList, saveList));
        return resutMap;
    }

    /**
     * 校验删除provider是否符合规则
     *
     * @param modelMap
     * @param deleteProviderIdList
     * @return
     */
    public ModelMap vefifyAppProvider(ModelMap modelMap, List<Long> deleteProviderIdList, Long appId) {
        if (!CollectionUtils.isEmpty(deleteProviderIdList)) {
            String deleteIds = StringUtils.join(deleteProviderIdList, ",");

            String[] deleteIdStr = deleteIds.split(",");
            String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "provider_id");
            Set<String> sieCodeList = daConfigSensorMapper.querySiecodeByAppIdAndProviderId(appId, deleteIdSqlStr);
            if (!CollectionUtils.isEmpty(sieCodeList)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
        }
        return modelMap;
    }

    /**
     * 校验删除的继承app
     *
     * @param modelMap
     * @param deleteExtensionIdList
     * @return
     */
    public ModelMap vefifyPublicApp(ModelMap modelMap, List<Long> deleteExtensionIdList, Long appId) {
        if (!CollectionUtils.isEmpty(deleteExtensionIdList)) {
            String deleteIds = StringUtils.join(deleteExtensionIdList, ",");

            String[] deleteIdStr = deleteIds.split(",");
            String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "a.app_id");
            Set<String> sieCodeList = daConfigSensorMapper.querySiecodeByAppId(deleteIdSqlStr);
            if (!CollectionUtils.isEmpty(sieCodeList)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
        }
        return modelMap;
    }


}
