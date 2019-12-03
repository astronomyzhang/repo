package com.siemens.dasheng.web.service;


import com.siemens.dasheng.web.enums.*;
import com.siemens.dasheng.web.event.DaProviderUpdateEvent;
import com.siemens.dasheng.web.generalmodel.dataconnector.*;
import com.siemens.dasheng.web.mapper.*;
import com.siemens.dasheng.web.model.*;
import com.siemens.dasheng.web.model.dto.AppInfo;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.singleton.conf.ConnectorCache;
import com.siemens.dasheng.web.singleton.conf.ConnectorTagMatch;
import com.siemens.dasheng.web.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.SAME_CONNECTOR_NOT_IMPORT;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @author liming
 * @Date: 2019/1/7 8:23
 */
@Service
public class DataConnectorApplyService {

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DaConfigProviderMapper daConfigProviderMapper;

    @Autowired
    private DaConfigProviderConnectorMapper daConfigProviderConnectorMapper;

    @Autowired
    private DaConfigApplicationMapper daConfigApplicationMapper;

    @Autowired
    private DaConfigApplicationSaveService daConfigApplicationSaveService;

    @Autowired
    private ConnectorTagMatch connectorTagMatch;


    @Autowired
    private StringEncryptor stringEncryptor;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    @Autowired
    private DaConfigAppProviderMapper daConfigAppProviderMapper;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int CHECKNO1 = 1;

    private static final int CHECKNO2 = 2;

    private static final int CHECKNO3 = 3;

    private static final int CHECKNO4 = 4;

    private static final int CHECKNO5 = 5;

    private static final String STATUS1 = "1";

    private static final String STATUS2 = "2";

    private static final int SQLDAS_LINUX = 1;

    private static final int SQLDAS_WINDOWS = 2;

    private static final int OPENPLANT_LINUX = 3;

    private static final int SDK_WINDOWS = 4;

    private static final int PI_AF = 5;

    private static final int DEFAULT = 0;

    private static final String NAME = "name";

    /**
     * 获取 dataConnector
     *
     * @param request
     * @return
     */
    public List<QueryConnectorResponse> queryConnectors(QueryConnectorRequest request) {

        request.setSearchStr(request.getSearchStr() != null ? request.getSearchStr().trim().replaceAll("/", "//").replaceAll("%", "/%").replaceAll("_", "/_") : null);

        List<QueryConnectorResponse> connectors = daConfigConnectorMapper.queryConnectors(request);
        if (null != connectors && connectors.size() > 0) {
            for (QueryConnectorResponse connector : connectors) {
                List<DaConfigProvider> daConfigProviders = daConfigProviderMapper.selectByConnectId(connector.getId());
                if (null != daConfigProviders && daConfigProviders.size() > 0) {
                    Integer activateNo = 0;
                    for (DaConfigProvider daConfigProvider : daConfigProviders) {
                        if (null != daConfigProvider.getStatus() && daConfigProvider.getStatus() == 1) {
                            activateNo++;
                        }
                    }
                    connector.setQuoteNo(daConfigProviders.size());
                    connector.setActivateNo(activateNo);
                } else {
                    connector.setQuoteNo(0);
                    connector.setActivateNo(0);
                }
            }
        }

        return connectors;
    }

    /**
     * 删除连接
     *
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public DeleteConnectorResponse deleteConnector(DeleteConnectorRequest request) {

        DeleteConnectorResponse deleteConnectorResponse = new DeleteConnectorResponse();
        Long id = request.getId();
        if (null != id) {
            DaConfigConnector daConfigConnector = daConfigConnectorMapper.selectById(request.getId());
            if (null != daConfigConnector) {
                List<String> importedTest = queryConnectorsIsImported(request.getId());
                if(!CollectionUtils.isEmpty(importedTest)){
                    deleteConnectorResponse.setCheckNo(CHECKNO4);
                    return deleteConnectorResponse;
                }
                List<DaConfigProvider> daConfigProviders = daConfigProviderMapper.selectByConnectId(request.getId().intValue());
                List<String> names = new ArrayList<>();
                List<String> activeNames = new ArrayList<>();
                if (null != daConfigProviders && daConfigProviders.size() > 0) {
                    for (DaConfigProvider daConfigProvider : daConfigProviders) {
                        names.add(daConfigProvider.getName());
                        if (daConfigProvider.getStatus() == 1) {
                            activeNames.add(daConfigProvider.getName());
                        }
                    }
                }

                if (names.size() == 0) {
                    daConfigConnectorMapper.deleteById(request.getId());
                    //删除成功
                    deleteConnectorResponse.setCheckNo(0);
                } else {
                    if (null != request.getConfirm() && request.getConfirm()) {
                        if (activeNames.size() == 0) {
                            daConfigConnectorMapper.deleteById(request.getId());
                            daConfigProviderConnectorMapper.deleteByConnectId(request.getId().intValue());
                            deleteConnectorResponse.setCheckNo(0);
                            deleteConnectorResponse.setNames(names);
                        } else {
                            //该连接的饮用者已经激活，不能删除
                            deleteConnectorResponse.setCheckNo(2);
                            deleteConnectorResponse.setNames(activeNames);
                        }
                    } else {
                        //需要确认才能删除
                        deleteConnectorResponse.setCheckNo(3);
                        deleteConnectorResponse.setNames(names);
                    }
                }

            } else {
                //该连接不存在
                deleteConnectorResponse.setCheckNo(1);
            }
        } else {
            //该连接不存在
            deleteConnectorResponse.setCheckNo(1);
        }

        return deleteConnectorResponse;
    }



    @Transactional(rollbackFor = Exception.class)
    public UpdateConnectorResponse updateConnector(UpdateConnectorRequest request, String username) throws Exception {
        UpdateConnectorResponse response = new UpdateConnectorResponse();
        DaConfigConnectorRequest daConfigConnectorRequest = new DaConfigConnectorRequest();
        BeanUtils.copyProperties(request, daConfigConnectorRequest);
        daConfigConnectorRequest.setId(Long.valueOf(request.getId()));

        DaConfigConnector daConfigConnector = daConfigConnectorMapper.selectById(request.getId().longValue());
        if (null != daConfigConnector) {
            //如果不修改连接信息和验证信息，做直接修改操作
            daConfigConnector.setPassword(StringUtils.isEmpty(daConfigConnector.getPassword()) ? "" : stringEncryptor.decrypt(daConfigConnector.getPassword()));
            boolean infos = daConfigConnector.getConnectorInfo().equals(dataConnectorService.structureConnectorInfo(daConfigConnectorRequest)) &&
                    ((StringUtils.isEmpty(daConfigConnector.getUserName()) && StringUtils.isEmpty(request.getUserName()))|| (!StringUtils.isEmpty(daConfigConnector.getUserName()) && daConfigConnector.getUserName().equals(request.getUserName()))) &&
                    ((StringUtils.isEmpty(daConfigConnector.getPassword()) && StringUtils.isEmpty(request.getPassword())) || (!StringUtils.isEmpty(daConfigConnector.getPassword()) && daConfigConnector.getPassword().equals(request.getPassword())));
            if(infos)
            {
                daConfigConnectorMapper.updateUnConInfoById(request, username, System.currentTimeMillis());
                response.setUpdate(true);
                return response;
            }

            List<String> names = new ArrayList<>();
            boolean variyTest = false;
            //需要去验证这个连接是否可以连接上
            try {
                long bgTime = System.currentTimeMillis();
                ModelMap modelMap = dataConnectorService.testConnector(daConfigConnectorRequest);
                long endTime = System.currentTimeMillis();
                logger.info("Test connect use time: {}ms", (endTime - bgTime));
                variyTest = (boolean) ((Map<String, Object>) modelMap.get("data")).get("status");
                if (variyTest) {
                    request.setStatus(ConnecteStatus.CONNECTABLE.getType());
                } else {
                    request.setStatus(ConnecteStatus.UNCONNECTABLE.getType());
                }
            } catch (Exception e) {
                request.setStatus(ConnecteStatus.UNCONNECTABLE.getType());
            }

            List<DaConfigProvider> daConfigProviders = daConfigProviderMapper.selectByConnectId(request.getId());

            if (null != daConfigProviders) {
                for (DaConfigProvider provider : daConfigProviders) {
                    // refresh daProvider's sensormapping_update_time colum
                    applicationEventPublisher.publishEvent(new DaProviderUpdateEvent(provider.getId() + ""));
                }
            }

            if (!variyTest) {
                if (null != daConfigProviders && daConfigProviders.size() > 0) {
                    for (DaConfigProvider daConfigProvider : daConfigProviders) {
                        if (daConfigProvider.getStatus() == 1L) {
                            names.add(daConfigProvider.getName());
                        }
                    }
                }
                if (names.size() > 0) {
                    response.setUpdate(false);
                    response.setStatus(CHECKNO5);
                    response.setNames(names);
                    return response;
                }
            }


            //验证确保修改后的connector在各个app下都不重复
            Map<String,Object> isRepeat = queryAppConnectorIsRepeat(daConfigConnectorRequest,daConfigConnector);
            if(null != isRepeat){
                //重复connector,不可修改
                if(isRepeat.get(STATUS).toString().equals(STATUS1)){
                    response.setStatus(CHECKNO1);
                    response.setNames((ArrayList)isRepeat.get(NAME));
                    response.setUpdate(Boolean.FALSE);
                }else if(isRepeat.get(STATUS).toString().equals(STATUS2)){
                    response.setStatus(CHECKNO2);
                    response.setNames((ArrayList)isRepeat.get(NAME));
                    response.setUpdate(Boolean.FALSE);
                }
                return response;
            }

            //验证connector是否被sensor引用
            List<String> importTest = queryConnectorsIsImported(daConfigConnector.getId());
            if(!CollectionUtils.isEmpty(importTest)){
                //查询实时数据库，计算匹配度
                double isMatch = calTagListMatchRate(daConfigConnectorRequest);
                logger.info("================================"+ connectorTagMatch.getMatchRate() +"============================");

                if(isMatch < connectorTagMatch.getMatchRate()){
                    response.setStatus(CHECKNO3);
                    response.setUpdate(Boolean.FALSE);
                    response.setMatchName(String.format("%.2f",isMatch*100)+"%");
                    response.setNames(importTest);
                    return response;
                }
                if (null == request.getConfirm() || !request.getConfirm()) {
                    response.setStatus(CHECKNO4);
                    response.setUpdate(Boolean.FALSE);
                    response.setMatchName(String.format("%.4f",isMatch));
                    response.setNames(importTest);
                    return response;
                }
            }


            daConfigConnectorRequest.setPassword(StringUtils.isBlank(daConfigConnectorRequest.getPassword()) ? "" : daConfigConnectorRequest.getPassword().trim());
            String connectorInfo = dataConnectorService.structureConnectorInfo(daConfigConnectorRequest);

            request.setName(request.getName().trim());
            request.setPassword(StringUtils.isBlank(request.getPassword()) ? "" : stringEncryptor.encrypt(request.getPassword().trim()));
            daConfigConnectorMapper.updateById(request, username, System.currentTimeMillis(),connectorInfo);
            //更新缓存信息
            ConnectorCache.getInstance().put(Long.valueOf(request.getId()), variyTest);
            //查询connector 是否被application引用
            if(checkConnectorIsImportedApplication(daConfigConnector.getId())){
                //更新tag pool
                dataConnectorService.insertOrUpdateConnectorSensorList(daConfigConnectorRequest, 1);
            }
            response.setUpdate(true);
            response.setNames(names);
            return response;
        } else {
            return null;
        }

    }

    private boolean checkConnectorIsImportedApplication(Long connectorId) {
        //查询connector的app列表
        DaConfigProviderConnector proCon = new DaConfigProviderConnector();
        proCon.setConnectorId(connectorId);
        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.select(proCon);
        if(CollectionUtils.isEmpty(proConList)){
            return false;
        }
        List<Long> proIdList = struProIdList(proConList);
        List<DaConfigAppProvider> appProviderList = daConfigAppProviderMapper.selectByProIds(proIdList);
        if(CollectionUtils.isEmpty(appProviderList)){
            return false;
        }
        return true;
    }

    private Map<String,Object> queryAppConnectorIsRepeat(DaConfigConnectorRequest connectorRequest, DaConfigConnector daConfigConnector) throws Exception{
        //查询connector的app列表
        DaConfigProviderConnector proCon = new DaConfigProviderConnector();
        proCon.setConnectorId(connectorRequest.getId());
        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.select(proCon);
        if(CollectionUtils.isEmpty(proConList)){
            return null;
        }
        List<Long> proIdList = struProIdList(proConList);

        //查询provider内部重复
        List<String> repeatList = new ArrayList<>();
        for(Long proId : proIdList){
            DaConfigProviderConnector p  = new DaConfigProviderConnector();
            p.setProviderId(proId);
            List<DaConfigProviderConnector> proConnList = daConfigProviderConnectorMapper.select(p);
            List<Long> connIds = new ArrayList<>();
            for(DaConfigProviderConnector pc : proConnList){
                if(!pc.getConnectorId().equals(connectorRequest.getId())){
                    connIds.add(pc.getConnectorId());
                }
            }

            if(!CollectionUtils.isEmpty(connIds)){
                List<DaConfigConnector> connList = daConfigConnectorMapper.selectConnectorsByIds(connIds);
                for(DaConfigConnector conn : connList){
                    if(conn.getConnectorInfo().equals(dataConnectorService.structureConnectorInfo(connectorRequest))){
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(daConfigConnector.getName()).append("， ").append(conn.getName());
                        repeatList.add(stringBuilder.toString());
                    }
                }

            }
        }
        if(!CollectionUtils.isEmpty(repeatList)){
            Map<String,Object> tmap = new HashMap<>(4);
            tmap.put(STATUS,STATUS1);
            tmap.put(NAME,repeatList);
            return tmap;
        }

        List<DaConfigAppProvider> appProviderList = daConfigAppProviderMapper.selectByProIds(proIdList);
        //List<Long> onlyProIds = new ArrayList<>();

        if(!CollectionUtils.isEmpty(appProviderList)){
            Map<Long, AppInfo> appMap = daConfigApplicationSaveService.mapAppInfo();
            Set<Long> appIds = struAppIds(appProviderList);
            label : for(Long appId : appIds){
                //查询app下所有connector,排除自身connector
                DaConfigAppProvider ap = new DaConfigAppProvider();
                ap.setAppId(appId);
                List<DaConfigAppProvider> appProList = daConfigAppProviderMapper.select(ap);

                String providerName = "";
                outer : for(DaConfigAppProvider apt : appProList){
                    DaConfigProviderConnector temp = new DaConfigProviderConnector();
                    temp.setProviderId(apt.getProviderId());
                    List<DaConfigProviderConnector> tempProConList = daConfigProviderConnectorMapper.select(temp);
                    for(DaConfigProviderConnector pc : tempProConList){
                        if(pc.getConnectorId().equals(connectorRequest.getId())){
                            providerName = daConfigProviderMapper.queryById(apt.getProviderId()).getName();
                            break outer;
                        }
                    }
                }

                DaConfigApplication application = daConfigApplicationMapper.selectAppById(appId);
                for(DaConfigAppProvider apt : appProList){
                    DaConfigProviderConnector temp = new DaConfigProviderConnector();
                    temp.setProviderId(apt.getProviderId());
                    List<DaConfigProviderConnector> tempProConList = daConfigProviderConnectorMapper.select(temp);
                    List<Long> connectorIds = new ArrayList<>();
                    for(DaConfigProviderConnector pc : tempProConList){
                        if(!pc.getConnectorId().equals(connectorRequest.getId())){
                            connectorIds.add(pc.getConnectorId());
                        }
                    }
                    if(CollectionUtils.isEmpty(connectorIds)){
                        continue;
                    }
                    List<DaConfigConnector> connectorList = daConfigConnectorMapper.selectConnectorsByIds(connectorIds);
                    //判断app下connector全都不同
                    for(DaConfigConnector tempCon : connectorList){
                        if(dataConnectorService.structureConnectorInfo(connectorRequest).equals(tempCon.getConnectorInfo())){
                            StringBuilder str = new StringBuilder();
                            str.append("(").append(daConfigConnector.getName()).append(" in ").append(providerName).append("， ").append(tempCon.getName()).append(" in ").append(daConfigProviderMapper.selectByPrimaryKey(apt.getProviderId()).getName()).append(") in ").append(appMap.get(application.getFfAppId()).getName());
                            repeatList.add(str.toString());
                            continue label;

                        }
                    }
                }
            }
            if(!CollectionUtils.isEmpty(repeatList)){
                Map<String,Object> tmap = new HashMap<>(4);
                tmap.put(STATUS,STATUS2);
                tmap.put(NAME,repeatList);
                return tmap;
            }

        }
        return null;
    }

    private List<DaConfigConnector> queryConnectorListByProIds(List<Long> proIds, Long conId) {
        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
        List<Long> connectorIds = new ArrayList<>();
        for(DaConfigProviderConnector pc : proConList){
            if(!pc.getConnectorId().equals(conId)){
                connectorIds.add(pc.getConnectorId());
            }
        }
        if(CollectionUtils.isEmpty(connectorIds)){
            return null;
        }
        return daConfigConnectorMapper.selectConnectorsByIds(connectorIds);
    }

    private List<Long> struProIds(List<DaConfigAppProvider> appProviderList) {
        List<Long> proIds = new ArrayList<>();
        for(DaConfigAppProvider ap : appProviderList){
            proIds.add(ap.getProviderId());
        }
        return proIds;
    }

    private List<DaConfigConnector> queryConnectorListByAppIds(List<Long> appIds, Long conId) {
        List<DaConfigAppProvider> appProList = daConfigAppProviderMapper.selectByAppIds(appIds);
        List<Long> proIds = new ArrayList<>();
        for(DaConfigAppProvider ap : appProList){
            proIds.add(ap.getProviderId());
        }
        List<DaConfigProviderConnector> proConList = daConfigProviderConnectorMapper.selectByProIds(proIds);
        List<Long> connectorIds = new ArrayList<>();
        for(DaConfigProviderConnector pc : proConList){
            if(!pc.getConnectorId().equals(conId)){
                connectorIds.add(pc.getConnectorId());
            }
        }
        if(CollectionUtils.isEmpty(connectorIds)){
            return null;
        }
        return daConfigConnectorMapper.selectConnectorsByIds(connectorIds);

    }

    private Set<Long> struAppIds(List<DaConfigAppProvider> appProviderList) {
        Set<Long> appIds = new HashSet<>();
        for(DaConfigAppProvider ap : appProviderList){
            appIds.add(ap.getAppId());
        }
        return appIds;
    }

    private List<Long> struProIdList(List<DaConfigProviderConnector> proConList) {
        List<Long> proIds = new ArrayList<>();
        for(DaConfigProviderConnector pc : proConList){
            proIds.add(pc.getProviderId());
        }
        return proIds;
    }

    private double calTagListMatchRate(DaConfigConnectorRequest connectorRequest) throws Exception{
        Map<String,String> tagMap = dataConnectorService.selectTagList(connectorRequest);
        List<String> newTagList = struTagList(tagMap);
        //查询原来已存表tagList
        DaConfigConnectorSensor cs = new DaConfigConnectorSensor();
        cs.setConnectorId(connectorRequest.getId());
        List<DaConfigConnectorSensor> existCsList = daConfigConnectorSensorMapper.select(cs);
        List<String> existTagList = struExistTagList(existCsList);
        //减少的tag列表
        List<String> reduceTagList = CollectionUtil.getReduceaListThanbList(newTagList,existTagList);
        double sum = existTagList.size();
        double matchnum = sum - reduceTagList.size();
        return matchnum/sum ;
    }

    private List<String> struExistTagList(List<DaConfigConnectorSensor> existCsList) {
        List<String> tagList = new ArrayList<>();
        if(CollectionUtils.isEmpty(existCsList)){
            return tagList;
        }
        for ( DaConfigConnectorSensor cs : existCsList){
            tagList.add(cs.getTag());
        }
        return tagList;
    }

    private List<String> struTagList(Map<String, String> tagMap) {
        List<String> tagList = new ArrayList<>();
        if(null == tagMap || tagMap.isEmpty()){
            return tagList;
        }
        for(String key : tagMap.keySet()){
            tagList.add(key);
        }
        return tagList;
    }


    private List<String> queryConnectorsIsImported(Long connectorId) {
        List<String> retList = new ArrayList<>();
        DaConfigSensor sensor = new DaConfigSensor();
        sensor.setConnectorId(connectorId);
        List<DaConfigSensor> sensorList = daConfigSensorMapper.select(sensor);
        if(CollectionUtils.isEmpty(sensorList)){
            return null;
        }
        List<Long> appIds = new ArrayList<>();
        for(DaConfigSensor dsensor : sensorList){
            appIds.add(dsensor.getAppId());
        }
        List<DaConfigApplication> appList = daConfigApplicationMapper.selectByAppIds(appIds);
        Map<Long, AppInfo> appInfoMap = daConfigApplicationSaveService.mapAppInfo();
        for(DaConfigApplication app : appList){
            retList.add(appInfoMap.get(app.getFfAppId()).getName());
        }
        return retList;
    }


}
