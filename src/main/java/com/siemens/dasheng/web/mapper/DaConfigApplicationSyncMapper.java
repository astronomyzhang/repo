package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.dto.DaConfigGroupDTO;
import com.siemens.dasheng.web.model.dto.DaConfigSensorDTO;

import java.util.List;
import java.util.Map;

/**
 * DaConfig application sync mapper
 *
 * @author ly
 * @date 2019/05/16
 */
public interface DaConfigApplicationSyncMapper {

    /**
     * 根据globalAppId查询application
     *
     * @param globalAppId
     * @return
     */
    Map<String, Object> selectDaConfigApplicationByGlobalAppId(String globalAppId);

    /**
     * 根据appId查询sensor
     *
     * @param appId
     * @return
     */
    List<Map<String, Object>> selectDaConfigSensorByAppId(Long appId);

    /**
     * 根据groupId查询sensor
     *
     * @param groupId
     * @return
     */
    List<Map<String, Object>> selectDaConfigSensorGroupByAppId(Long groupId);

    /**
     * 根据AppId修改相关的子app及其本身的版本号
     *
     * @param map
     * @return
     */
    int updateAppVersionByAppId(Map<String, Object> map);

    /**
     * 根据groupId列表修改相关的group版本号
     *
     * @param map
     * @return
     */
    int updateGroupVersionByGroupIdList(Map<String, Object> map);

    /**
     * 根据globalAppId查询自己和继承它的所有测点组列表
     *
     * @param globalAppId 系统appId
     * @return 测点组DTO列表
     */
    List<DaConfigGroupDTO> selectSensorGroupByGlobalAppId(String globalAppId);

    /**
     * 根据groupId查询测点列表
     *
     * @param groupId 测点组id
     * @return 测点DTO列表
     */
    List<DaConfigSensorDTO> selectSensorByGroupId(Long groupId);
}
