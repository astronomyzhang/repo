package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.event.DaAppUpdateEvent;
import com.siemens.dasheng.web.mapper.DaConfigApplicationSyncMapper;
import com.siemens.dasheng.web.model.dto.DaConfigGroupDTO;
import com.siemens.dasheng.web.model.dto.DaConfigSensorDTO;
import com.siemens.dasheng.web.request.DaConfigApplicationSyncRequest;
import com.siemens.dasheng.web.request.DaConfigGroupSyncRequest;
import com.siemens.dasheng.web.response.DaConfigApplicationSyncResponse;
import com.siemens.dasheng.web.response.DaConfigGroupSyncResponse;
import com.siemens.dasheng.web.response.DaConfigSensorSyncResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DaConfig application sync service
 *
 * @author ly
 * @date 2019/05/15
 */
@Service
public class DaConfigApplicationSyncService {

    /**
     * 数据库中app或group的版本未被初始化时默认为该版本
     */
    private static final Long VERSION_NOT_INIT = 0L;

    @Autowired
    DaConfigApplicationSyncMapper syncMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    /**
     * app（包含其被继承的app）版本升级,监听器版
     *
     * @param appId
     */
    public void daConfigApplicationVersionUpgrade(Long appId) {
        if (appId == null) {
            return;
        }
        // notice update event
        applicationEventPublisher.publishEvent(new DaAppUpdateEvent(appId + ""));
    }

    /**
     * app（包含其被继承的app）版本升级
     *
     * @param appId
     */
    public void daConfigApplicationVersionUpgrade2(Long appId) {
        if (appId == null) {
            return;
        }
        Long newAppVersion = generateNewVersion();
        Map<String, Object> map = new HashMap<>(4);
        map.put("appVersion", newAppVersion);
        map.put("appId", appId);
        syncMapper.updateAppVersionByAppId(map);
    }

    /**
     * groupList 版本升级
     *
     * @param groupIdList
     */
    public void daConfigGroupVersionUpgrade(List<Long> groupIdList) {
        if (groupIdList == null || groupIdList.size() == 0) {
            return;
        }
        Long newGroupVersion = generateNewVersion();
        Map<String, Object> map = new HashMap<>(4);
        map.put("groupVersion", newGroupVersion);
        map.put("groupIdList", groupIdList);
        syncMapper.updateGroupVersionByGroupIdList(map);
    }

    /**
     * 根据globalAppId查询自己和继承它的所有测点组列表
     *
     * @param globalAppId 系统appId
     * @return 测点组DTO列表
     */
    public List<DaConfigGroupDTO> getSensorGroupByGlobalAppId(String globalAppId) {
        return syncMapper.selectSensorGroupByGlobalAppId(globalAppId);
    }

    /**
     * 根据groupId查询测点列表
     *
     * @param groupId 测点组id
     * @return 测点DTO列表
     */
    public List<DaConfigSensorDTO> getSensorByGroupId(Long groupId) {
        return syncMapper.selectSensorByGroupId(groupId);
    }

    /**
     * app同步服务
     *
     * @param request
     * @return
     */
    public DaConfigApplicationSyncResponse syncApplication(DaConfigApplicationSyncRequest request) {
        // 需要同步的app全局唯一id
        String globalAppId = request.getGlobalAppId();
        if (globalAppId == null) {
            return null;
        }
        // 需要同步的app旧版本号
        Long oldAppVersion = request.getAppVersion();
        // 获取需要同步的测点组列表
        List<DaConfigGroupSyncRequest> oldGroupList = request.getGroupList();

        // 需要同步给同步方的结果
        DaConfigApplicationSyncResponse response = new DaConfigApplicationSyncResponse();
        response.setGlobalAppId(globalAppId);
        Map<String, Object> daConfigApplication = syncMapper.selectDaConfigApplicationByGlobalAppId(globalAppId);
        if (daConfigApplication == null) {
            return null;
        }

        Long newAppVersionPgsql = (Long) daConfigApplication.get("sensormapping_update_time");
        Long newAppVersionOracle = transferObject2Long(daConfigApplication.get("SENSORMAPPING_UPDATE_TIME"));
        Long newAppVersion = newAppVersionPgsql != null ? newAppVersionPgsql : newAppVersionOracle;
        response.setAppVersion(newAppVersion == null ? VERSION_NOT_INIT : newAppVersion);

        Long appIdPgsql = (Long) daConfigApplication.get("id");
        Long appIdOracle = transferObject2Long(daConfigApplication.get("ID"));
        Long appId = appIdPgsql != null ? appIdPgsql : appIdOracle;
        response.setAppId(appId);

        Long typePgsql = (Long) daConfigApplication.get("type");
        Long typeOracle = transferObject2Long(daConfigApplication.get("TYPE"));
        Long type = typePgsql != null ? typePgsql : typeOracle;
        response.setType(type);



        // 同步需求为app下测点同步，否则为测点组同步
        if (oldGroupList == null) {
            // 如果需要同步的原有版本号和最新的版本号不一致，需要同步最新的测点表
            if (!newAppVersion.equals(oldAppVersion)) {
                List<Map<String, Object>> mapList = syncMapper.selectDaConfigSensorByAppId(appId);
                // 将查询结果转换成DaConfigSensorSyncResponse
                List<DaConfigSensorSyncResponse> sensorList = mapList.stream().map(this::transferMap2Sensor).collect(Collectors.toList());
                response.setSensorList(sensorList);
            }
        } else {
            // 查询appId相关的所有组（包含从父app中继承的组）以及组中所有的测点信息
            List<Map<String, Object>> mapList = syncMapper.selectDaConfigSensorGroupByAppId(appId);
            // 将测点按测点组groupId分组
            Map<Long, List<SensorWithGroup>> sensorGroupByGroupId = mapList.stream().map(this::transferMap2SensorWithGroup).collect(Collectors.groupingBy(SensorWithGroup::getGroupId));
            List<DaConfigGroupSyncResponse> newGroupList = sensorGroupByGroupId.entrySet().stream().map(entry -> {
                // 空测点
                DaConfigSensorSyncResponse empty = new DaConfigSensorSyncResponse();
                Long groupId = entry.getKey();
                List<SensorWithGroup> sensorList = entry.getValue();
                SensorWithGroup groupMessage = sensorList.get(0);
                String groupName = groupMessage.getGroupName();
                String groupDescription = groupMessage.getDescription();
                Long groupVersion = groupMessage.getGroupVersion();
                // 将测点信息从Sensor临时对象中取出
                List<DaConfigSensorSyncResponse> newSensorList = sensorList.stream().map(sensor -> {
                    if (sensor.getSiecode() == null) {
                        return empty;
                    }
                    DaConfigSensorSyncResponse daConfigSensorSyncResponse = new DaConfigSensorSyncResponse();
                    daConfigSensorSyncResponse.setSiecode(sensor.getSiecode());
                    daConfigSensorSyncResponse.setKkscode(sensor.getKkscode());
                    daConfigSensorSyncResponse.setTitle(sensor.getTitle());
                    daConfigSensorSyncResponse.setSystemunit(sensor.getSystemunit());
                    daConfigSensorSyncResponse.setFromRegister(sensor.getFromRegister());
                    return daConfigSensorSyncResponse;
                }).filter(sensor -> sensor != empty).collect(Collectors.toList());

                DaConfigGroupSyncResponse daConfigGroupSyncResponse = new DaConfigGroupSyncResponse();
                daConfigGroupSyncResponse.setGroupId(groupId);
                daConfigGroupSyncResponse.setGroupName(groupName);
                daConfigGroupSyncResponse.setGroupDescription(groupDescription);
                daConfigGroupSyncResponse.setGroupVersion(groupVersion);
                daConfigGroupSyncResponse.setSensorList(newSensorList);
                return daConfigGroupSyncResponse;
            }).collect(Collectors.toList());
            if (oldGroupList.size() != 0) {
                newGroupList = newGroupList.stream().map(newGroup -> {
                    boolean noChange = oldGroupList.stream().anyMatch(oldGroup ->
                            newGroup.getGroupId().equals(oldGroup.getGroupId()) &&
                                    newGroup.getGroupVersion().equals(oldGroup.getGroupVersion()));
                    // 未发生变化时，不进行同步
                    if (noChange) {
                        newGroup.setSensorList(null);
                    }
                    return newGroup;
                }).collect(Collectors.toList());
            }
            response.setGroupList(newGroupList);
        }

        return response;
    }

    /**
     * 数据库中数字类型数据转换为long
     *
     * @param obj
     * @return
     */
    private Long transferObject2Long(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).longValue();
        }

        if (obj instanceof Long) {
            return (Long) obj;
        }
        throw new RuntimeException("transfer " + obj.getClass() + " to Long error");
    }

    /**
     * 版本号生成器（Long类型）
     *
     * @return
     */
    private Long generateNewVersion() {
        return System.currentTimeMillis();
    }

    /**
     * 将map测点信息转换成DaConfigSensorSyncResponse
     *
     * @param map
     * @return
     */
    private DaConfigSensorSyncResponse transferMap2Sensor(Map<String, Object> map) {
        DaConfigSensorSyncResponse sensor = new DaConfigSensorSyncResponse();
        String siecodePg = (String) map.get("siecode");
        String tagPg = (String) map.get("tag");
        String titlePg = (String) map.get("description");
        String systemunitPg = (String) map.get("unit");
        String fromRegisterPg = map.get("from_regist") == null ? null : map.get("from_regist").toString();

        String siecodeOra = (String) map.get("SIECODE");
        String tagOra = (String) map.get("TAG");
        String titleOra = (String) map.get("DESCRIPTION");
        String systemunitOra = (String) map.get("UNIT");
        String fromRegisterOra = map.get("FROM_REGIST") == null ? null : map.get("FROM_REGIST").toString();

        String siecode = siecodePg != null ? siecodePg : siecodeOra;
        String tag = tagPg != null ? tagPg : tagOra;
        String title = titlePg != null ? titlePg : titleOra;
        String systemunit = systemunitPg != null ? systemunitPg : systemunitOra;
        String fromRegister = fromRegisterPg != null ? fromRegisterPg : fromRegisterOra;
        sensor.setSiecode(siecode);
        sensor.setKkscode(tag);
        sensor.setTitle(title);
        sensor.setSystemunit(systemunit);
        sensor.setFromRegister(fromRegister);
        return sensor;
    }

    /**
     * 将map测点(附带group)信息转换成Sensor
     *
     * @param map
     * @return
     */
    private SensorWithGroup transferMap2SensorWithGroup(Map<String, Object> map) {
        SensorWithGroup sensor = new SensorWithGroup();
        // 测点组信息
        Long groupIdPg = (Long) map.get("id");
        String groupNamePg = (String) map.get("name");
        String descriptionPg = (String) map.get("description");
        Long groupVersionPg = (Long) map.get("group_version");

        Long groupIdOra = transferObject2Long(map.get("ID"));
        String groupNameOra = (String) map.get("NAME");
        String descriptionOra = (String) map.get("DESCRIPTION");
        Long groupVersionOra = transferObject2Long(map.get("GROUP_VERSION"));

        Long groupId = groupIdPg != null ? groupIdPg : groupIdOra;
        String groupName = groupNamePg != null ? groupNamePg : groupNameOra;
        String description = descriptionPg != null ? descriptionPg : descriptionOra;
        Long groupVersion = groupVersionPg != null ? groupVersionPg : groupVersionOra;
        // 测点信息
        String siecodePg = (String) map.get("sensor_code");
        String tagPg = (String) map.get("tag");
        String titlePg = (String) map.get("title");
        String systemunitPg = (String) map.get("systemunit");
        String fromRegisterPg = map.get("from_regist") == null ? null : map.get("from_regist").toString();

        String siecodeOra = (String) map.get("SENSOR_CODE");
        String tagOra = (String) map.get("TAG");
        String titleOra = (String) map.get("TITLE");
        String systemunitOra = (String) map.get("SYSTEMUNIT");
        String fromRegisterOra = map.get("FROM_REGIST") == null ? null : map.get("FROM_REGIST").toString();


        String siecode = siecodePg != null ? siecodePg : siecodeOra;
        String tag = tagPg != null ? tagPg : tagOra;
        String title = titlePg != null ? titlePg : titleOra;
        String systemunit = systemunitPg != null ? systemunitPg : systemunitOra;
        String fromRegister = fromRegisterPg != null ? fromRegisterPg : fromRegisterOra;

        sensor.setGroupId(groupId);
        sensor.setGroupName(groupName);
        sensor.setDescription(description);
        sensor.setGroupVersion(groupVersion == null ? VERSION_NOT_INIT : groupVersion);
        sensor.setSiecode(siecode);
        sensor.setKkscode(tag);
        sensor.setTitle(title);
        sensor.setSystemunit(systemunit);
        sensor.setFromRegister(fromRegister);
        return sensor;
    }

    /**
     * 测点信息
     */
    private static class SensorWithGroup {
        Long groupId;
        String groupName;
        String description;
        Long groupVersion;
        String siecode;
        String kkscode;
        String title;
        String systemunit;
        String fromRegister;

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Long getGroupVersion() {
            return groupVersion;
        }

        public void setGroupVersion(Long groupVersion) {
            this.groupVersion = groupVersion;
        }

        public String getSiecode() {
            return siecode;
        }

        public void setSiecode(String siecode) {
            this.siecode = siecode;
        }

        public String getKkscode() {
            return kkscode;
        }

        public void setKkscode(String kkscode) {
            this.kkscode = kkscode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSystemunit() {
            return systemunit;
        }

        public void setSystemunit(String systemunit) {
            this.systemunit = systemunit;
        }

        public String getFromRegister() { return fromRegister; }

        public void setFromRegister(String fromRegister) { this.fromRegister = fromRegister; }
    }
}
