package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.apolloconfig.ApolloAppConfig;
import com.siemens.dasheng.web.enums.AvailableStatus;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.dto.*;
import com.siemens.dasheng.web.response.AplicationName;
import com.siemens.dasheng.web.response.AppDetail;
import com.siemens.dasheng.web.response.InheritedApp;
import com.siemens.dasheng.web.response.ResponseObject;
import com.siemens.dasheng.web.util.ServiceUtil;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liming
 * @Date: 2019/4/8 10:39
 */
@Service
public class ApplicationViewService {

    @Autowired
    DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    @Resource(name = "ribbonRestTemplate")
    private RestTemplate ribbonRestTemplate;

    @Autowired
    DaConfigAppProviderMapper daConfigAppProviderMapper;

    @Autowired
    DaConfigProviderMapper daConfigProviderMapper;

    @Autowired
    DaConfigAppExtensionMapper daConfigAppExtensionMapper;

    @Autowired
    DataProviderService dataProviderService;

    @Autowired
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private DaConfigApplicationSaveService daConfigApplicationSaveService;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    public List<AplicationName> selectAppList() {

        List<AplicationName> applicationNames = new ArrayList<>();
        List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectAppList();
        if (null != daConfigApplications && daConfigApplications.size() > 0) {
            Map<Long, AppInfo> appInfoMap = daConfigApplicationSaveService.mapAppInfo();
            for (DaConfigApplication daConfigApplication : daConfigApplications) {
                AppInfo appInfo = appInfoMap.get(daConfigApplication.getFfAppId());
                if (appInfo != null) {
                    AplicationName aplicationName = new AplicationName();
                    aplicationName.setId(daConfigApplication.getId());
                    aplicationName.setAppid(appInfo.getAppid());
                    aplicationName.setName(appInfo.getName());
                    aplicationName.setType(appInfo.getFlag());
                    aplicationName.setGlobalAppId(daConfigApplication.getGlobalAppId());
                    applicationNames.add(aplicationName);
                }
            }
        }
        Map<Integer, List<AplicationName>> groupBy = applicationNames.stream().collect(Collectors.groupingBy(AplicationName::getType));
        List<AplicationName> groupList = new ArrayList<>();
        for (Map.Entry<Integer, List<AplicationName>> entry : groupBy.entrySet()) {
            List<AplicationName> value =  entry.getValue();
            if (!CollectionUtils.isEmpty(value)) {
                Collections.sort(value, Comparator.comparing(AplicationName::getName));
                groupList.addAll(value);
            }
        }
        return groupList;
    }

    public AppDetail selectAppDetail(Long id) {
        AppDetail appDetail = new AppDetail();
        AppWithTime appWithTime = daConfigApplicationMapper.selectAppById(id);
        if (null != appWithTime) {
            String response = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getSsoServerName() + "/cas/clientApps/selectAppDetailById/" + appWithTime.getFfAppId(), null, String.class);
            ResponseObject responseObject = JSON.parseObject(response, ResponseObject.class);
            if (responseObject != null) {
                if (responseObject.getData() != null) {
                    AppInfo appInfo = JSON.parseObject(responseObject.getData(), AppInfo.class);
                    if (null != appInfo) {
                        //获得应用详细信息
                        setAppDetail(appDetail, appWithTime, responseObject, appInfo);
                    } else {
                        return null;
                    }
                }
            }
        } else {
            return null;
        }

        return appDetail;
    }

    private void setAppDetail(AppDetail appDetail, AppWithTime appWithTime, ResponseObject responseObject, AppInfo appInfo) {
        appDetail.setDescription(appInfo.getDescription());
        appDetail.setScope(appWithTime.getType());
        appDetail.setType(appInfo.getFlag());
        appDetail.setRegistoredUser(appWithTime.getUserName());
        appDetail.setDateTime(appWithTime.getDateTime());
        appDetail.setFfAppId(appWithTime.getFfAppId());
        appDetail.setName(appInfo.getFullname());
        List<DaConfigAppExtension> extensions1 = daConfigAppExtensionMapper.selectByExtensionAppId(appWithTime.getId());
        if (null != extensions1 && extensions1.size() > 0) {
            appDetail.setInherit(true);
        } else {
            appDetail.setInherit(false);
        }
        List<DaConfigProvider> daConfigProviders = daConfigProviderMapper.queryApplicationProvider(appWithTime.getId());
        //添加可利用率
        setAvailability(daConfigProviders);

        List<ProviderSensor> providerSensorList = daConfigSensorMapper.queryProviderSensorMap(appWithTime.getId());
        Map<Long, Integer> providerSensorMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(providerSensorList)) {
            providerSensorMap = providerSensorList.stream().collect(Collectors.toMap(ProviderSensor::getProviderId, ProviderSensor::getSensorCount));

        }
        setSelected(daConfigProviders, providerSensorMap);
        appDetail.setProviders(daConfigProviders);



        List<DaConfigAppExtension> extensions = daConfigAppExtensionMapper.selectByAppId(appWithTime.getId());
        if (null != extensions && extensions.size() > 0) {
            List<InheritedApp> inheritedApps = new ArrayList<>();
            List<Long> longList = extensions.stream().map(DaConfigAppExtension::getExtensionAppId).collect(Collectors.toList());
            List<DaConfigApplication> daConfigApplications = daConfigApplicationMapper.selectByAppIds(longList);

            String deleteIds = StringUtils.join(longList, ",");

            String[] deleteIdStr = deleteIds.split(",");
            String deleteIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "app_id");
            List<AppSensor> appSensorList = daConfigSensorMapper.queryAppSensorMap(deleteIdSqlStr);
            Map<Long, Integer> appSensorMap = new HashMap<>(16);
            if (!CollectionUtils.isEmpty(appSensorList)) {
                appSensorMap = appSensorList.stream().collect(Collectors.toMap(AppSensor::getAppId, AppSensor::getSensorCout));
            }

            Map<Long, Long> idMap = daConfigApplications.stream().collect(Collectors.toMap(DaConfigApplication::getId, DaConfigApplication::getFfAppId, (k1, k2) -> k1));

            for (DaConfigAppExtension extension : extensions) {
                String responseChildren = ribbonRestTemplate.postForObject("http://" + ApolloAppConfig.getInstance().getSsoServerName() + "/cas/clientApps/selectAppDetailById/" + idMap.get(extension.getExtensionAppId()), null, String.class);
                ResponseObject responseObjectChildren = JSON.parseObject(responseChildren, ResponseObject.class);
                if (responseObject != null) {
                    if (responseObject.getData() != null) {
                        AppInfo appInfoChildren = JSON.parseObject(responseObjectChildren.getData(), AppInfo.class);
                        if (null != appInfoChildren) {
                            InheritedApp inheritedApp = new InheritedApp();
                            inheritedApp.setName(appInfoChildren.getFullname());
                            inheritedApp.setDescription(appInfoChildren.getDescription());
                            inheritedApp.setFlag(appInfoChildren.getFlag());
                            inheritedApp.setKey(extension.getExtensionAppId());
                            inheritedApp.setId(extension.getExtensionAppId());
                            List<DaConfigProvider> daConfigProvidersChildren = daConfigProviderMapper.queryApplicationProvider(extension.getExtensionAppId());
                            setAvailability(daConfigProvidersChildren);
                            inheritedApp.setProviders(daConfigProvidersChildren);
                            Integer sensorCount = appSensorMap.get(extension.getExtensionAppId());
                            if (sensorCount != null) {
                                inheritedApp.setSelected(Boolean.TRUE);
                            } else {
                                inheritedApp.setSelected(Boolean.FALSE);
                            }
                            inheritedApps.add(inheritedApp);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(inheritedApps)) {
                fillAvailability(inheritedApps);
            }
            appDetail.setPublicApplications(inheritedApps);
        }
    }

    private void setAvailability(List<DaConfigProvider> daConfigProviders) {
        //添加可利用率
        if (null != daConfigProviders && daConfigProviders.size() > 0) {
            List<Long> proIds = dataProviderService.struProIds(daConfigProviders);
            List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
            //key=proId ,value=conIdList
            Map<Long, Set<Long>> proConSetMap = dataProviderService.struProConSetMap(proConList);
            //key=proId,value=avail_rate
            Map<Long, String[]> proAvailMap = dataProviderService.struProAvailMap(proConSetMap, new HashMap<>(10));
            if (null != proAvailMap) {
                for (DaConfigProvider provider : daConfigProviders) {
                    provider.setAvailabilityRate((proAvailMap.get(provider.getId()) == null) ? "0.00" : proAvailMap.get(provider.getId())[0]);
                    provider.setAvailability((proAvailMap.get(provider.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(provider.getId())[1]));
                }
            } else {
                System.err.println("can't get proAvailMap");
            }
        }
    }

    /**
     * 填充继承app可获得率
     *
     * @param inheritedApps
     */
    public void fillAvailability(List<InheritedApp> inheritedApps) {
        if (!CollectionUtils.isEmpty(inheritedApps)) {
            List<Long> appIds = inheritedApps.stream().map(InheritedApp::getId).collect(Collectors.toList());
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

                for (InheritedApp inheritedApp : inheritedApps) {
                    List<ProviderConnectorCount> providerConnectorCounts = daConfigAppProviderMapper.queryProviderConnectorCount(inheritedApp.getId());

                    if (proAvailMap == null) {
                        inheritedApp.setAvailabilityRate("0.00");
                        inheritedApp.setAvailability(AvailableStatus.UNAVAILABLE.getType());
                    } else {
                        inheritedApp.setAvailabilityRate((proAvailMap.get(inheritedApp.getId()) == null) ? "0.00" : proAvailMap.get(inheritedApp.getId())[0]);
                        inheritedApp.setAvailability((proAvailMap.get(inheritedApp.getId()) == null) ? AvailableStatus.UNAVAILABLE.getType() : Long.valueOf(proAvailMap.get(inheritedApp.getId())[1]));
                    }
                    if (!CollectionUtils.isEmpty(providerConnectorCounts)) {
                        if (AvailableStatus.AVAILABLE.getType().equals(inheritedApp.getAvailability())) {
                            inheritedApp.setAvailability(AvailableStatus.PARTLY_AVAILABLE.getType());
                        }
                    }


                }
            }
        }

    }

    /**
     * 填充是否可以移除
     * @param daConfigProviders
     * @param providerSensorMap
     */
    private void setSelected(List<DaConfigProvider> daConfigProviders, Map<Long, Integer> providerSensorMap) {
        if (null != daConfigProviders && daConfigProviders.size() > 0) {
            for (DaConfigProvider provider : daConfigProviders) {
                Integer sensorCount = providerSensorMap.get(provider.getId());
                if (sensorCount != null) {
                    provider.setSelected(Boolean.TRUE);
                } else {
                    provider.setSelected(Boolean.FALSE);
                }
            }
        }
    }

}
