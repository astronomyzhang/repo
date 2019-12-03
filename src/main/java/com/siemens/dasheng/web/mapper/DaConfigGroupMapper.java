package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigGroup;
import com.siemens.dasheng.web.model.DaConfigGroupPlus;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author ly
 * @date 2019/4/9
 */
@Component
public interface DaConfigGroupMapper {

    /**
     * delete
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * delete
     *
     * @param map
     * @return
     */
    int deleteSensorWithGroup(Map<String, Object> map);

    /**
     * insert
     *
     * @param record
     * @return
     */
    int insert(DaConfigGroup record);

    /**
     * insert selective
     *
     * @param record
     * @return
     */
    int insertSelectivePg(DaConfigGroup record);

    /**
     * insert selective
     *
     * @param record
     * @return
     */
    int insertSelectiveOrac(DaConfigGroup record);

    /**
     * insert sensor list
     *
     * @param list
     * @return
     */
    int insertSensorListPg(List<Map<String, Object>> list);

    /**
     * insert sensor list
     *
     * @param list
     * @return
     */
    int insertSensorListOrac(List<Map<String, Object>> list);

    /**
     * select by primary key
     *
     * @param id
     * @return
     */
    DaConfigGroup selectByPrimaryKey(Long id);

    /**
     * 根据sieCode获取appId
     *
     * @param sieCode
     * @return
     */
    Long selectAppIdBySieCode(String sieCode);

    /**
     * 根据sieCode获取groupId list
     *
     * @param sieCode
     * @return
     */
    List<Long> selectGroupIdListBySieCode(String sieCode);

    /**
     * select group list
     *
     * @param queryMap
     * @return
     */
    List<DaConfigGroupPlus> selectGroupListByAppId(Map<String, Object> queryMap);

    /**
     * select sensor list
     *
     * @param map
     * @return
     */
    List<String> selectSensorListByAppId(Map<String, Object> map);

    /**
     * select sensor list with group
     *
     * @param map
     * @return
     */
    List<String> selectSensorListWithGroup(Map<String, Object> map);


    /**
     * 获取私有app测点组选择了私有app测点组的测点
     *
     * @param appId
     * @param extensionAppId
     * @return
     */
    @Select(" select a.sensor_code  from da_config_sensor_group a , da_config_group b, da_config_sensor c " +
            "where a.group_id = b.id and b.app_id = #{appId}  and a.sensor_code = c.siecode and (${extensionAppId})   ")
    List<String> selectSiceCodeAppId(@Param("appId") Long appId, @Param("extensionAppId") String extensionAppId);

    /**
     * update by primary key selective
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DaConfigGroup record);

    /**
     * update by primary key
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(DaConfigGroup record);

    /**
     * 获取制定app下的测点组
     *
     * @param appId
     * @param searchStr
     * @return
     */
    List<DaConfigGroup> getByFilter(@Param("appId") Long appId, @Param("searchStr") String searchStr);

    /**
     * 根据appId获取机组
     *
     * @param appId
     * @return
     */
    @Select(" select * from da_config_group where 1=1 and (${appId})   ")
    List<DaConfigGroup> getByAppId(@Param("appId") String appId);

    /**
     * 获取机组
     * @param appId
     * @return
     */
    @Select(" select * from da_config_group where 1=1 and app_id =  #{appId}  ")
    List<DaConfigGroup> getBySingleAppId(@Param("appId") Long appId);

    /**
     * 删除测点组里面的测点
     * @param groupIds
     * @param sensorList
     * @return
     */
    @Delete(" delete from da_config_sensor_group where 1=1 and  (${groupIds}) and (${sensorList}) ")
    int delteSensorGroupBySensorCode(@Param("groupIds") String groupIds, @Param("sensorList") String sensorList);

}