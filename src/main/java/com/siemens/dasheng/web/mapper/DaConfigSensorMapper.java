package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.model.DaConfigSensor;
import com.siemens.dasheng.web.model.DaConfigSensorPlus;
import com.siemens.dasheng.web.model.dto.AppSensor;
import com.siemens.dasheng.web.model.dto.AppSieCode;
import com.siemens.dasheng.web.model.dto.AppUsageCount;
import com.siemens.dasheng.web.model.dto.ProviderSensor;
import com.siemens.dasheng.web.request.SensorRegistrationRequest;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public interface DaConfigSensorMapper extends MyMapper<DaConfigSensor> {

    /**
     * GetSensorListByAppId
     * @param id
     * @return
     */
    @Select("select * from da_config_sensor where app_id = #{id}")
    List<DaConfigSensor> getSensorListByAppId(@Param("id") Long id);


    /**
     * select valid ruleId
     *
     * @param validRuleId
     * @return
     */
    @Select("select id from trendwarning_validsensorrule where id = #{validRuleId}")
    String selectValidSensorRule(@Param("validRuleId") String validRuleId);

    /**
     * selectIdOR
     *
     * @param
     * @return
     */
    @Select("select DA_CONFIG_CONNECTOR$SEQ.nextval from dual")
    @Options(flushCache = true)
    Long selectIdOR();

    /**
     * selectIdPG
     *
     * @param
     * @return
     */
    @Select("select nextval('da_config_connector$seq') from dual")
    @Options(flushCache = true)
    Long selectIdPG();

    /**
     * batch insert(oracle)
     *
     * @param daConfigSensorList
     * @return
     */
    int saveListOracle(@Param("list") List<DaConfigSensor> daConfigSensorList);

    /**
     * selectBySiecodes
     *
     * @param siecodes
     * @return
     */
    List<DaConfigSensor> selectBySiecodes(@Param("siecodes") List<String> siecodes);

    /**
     * select all sensors
     *
     * @return
     */
    List<DaConfigSensor> selectAllSensorList();

    /**
     * select all sensors by appId
     *
     * @param appId
     * @return
     */
    List<DaConfigSensorPlus> selectAvailableSensorListByAppId(Long appId);

    /**
     * selectBySiecode
     *
     * @param siecode
     * @return
     */
    @Select("select siecode, connector_id as connectorId, tag, app_id as appId, status, description, unit, from_regist as fromRegist from da_config_sensor " +
            " where siecode = #{siecode} ")
    DaConfigSensor selectBySiecode(@Param("siecode") String siecode);


    /**
     * select simple info by siecode
     *
     * @param siecode
     * @return
     */
    @Select("select t1.siecode as siecode, t1.app_id, t2.global_app_id from da_config_sensor t1 left join da_config_application t2 on t1.app_id = t2.id " +
            " where siecode = #{siecode} ")
    Map<String, Object> selectSimpleSensorInfoBySieCode(@Param("siecode") String siecode);

    /**
     * updateStatusToDelByCondition
     *
     * @param delTagList
     * @param applicationIds
     * @param connectorId
     * @return
     */
    int updateStatusToDelByCondition(@Param("tagList") Set<String> delTagList, @Param("applicationIds") List<Long> applicationIds, @Param("connectorId") Long connectorId);

    /**
     * selectCountByCondition
     *
     * @param appId
     * @param status
     * @param scope
     * @param searchContent
     * @param extendAppIds
     * @return
     */
    int selectCountByCondition(@Param("appId") Long appId, @Param("status") Long status, @Param("scope") Integer scope, @Param("searchContent") String searchContent, @Param("extendAppIds") List<Long> extendAppIds);

    /**
     * selectListByConditionPG
     *
     * @param appId
     * @param status
     * @param scope
     * @param searchContent
     * @param extendAppIds
     * @param i
     * @param pageSize
     * @return
     */
    List<DaConfigSensorPlus> selectListByConditionPG(@Param("appId") Long appId, @Param("status") Long status, @Param("scope") Integer scope, @Param("searchContent") String searchContent, @Param("extendAppIds") List<Long> extendAppIds, @Param("start") int i, @Param("pageNum") int pageSize);

    /**
     * selectListByConditionORAC
     *
     * @param appId
     * @param status
     * @param scope
     * @param searchContent
     * @param extendAppIds
     * @param i
     * @param pageSize
     * @return
     */
    List<DaConfigSensorPlus> selectListByConditionORAC(@Param("appId") Long appId, @Param("status") Long status, @Param("scope") Integer scope, @Param("searchContent") String searchContent, @Param("extendAppIds") List<Long> extendAppIds, @Param("start") int i, @Param("pageNum") int pageSize);


    /**
     * 获取数据提供者下的sensor
     *
     * @param appId
     * @param providerIds
     * @return
     */
    @Select("select siecode from da_config_sensor where connector_id " +
            "    in(select connector_id from da_config_provider_connector where 1=1 and (${providerIds})) " +
            "    and app_id = #{appId};")
    Set<String> querySiecodeByAppIdAndProviderId(@Param("appId") Long appId, @Param("providerIds") String providerIds);


    /**
     * 根据appids获取sensor
     *
     * @param appIds
     * @return
     */
    @Select("select a.siecode from da_config_sensor a, da_config_provider_connector b, " +
            "da_config_app_provider c,da_config_application  d " +
            "where a.connector_id = b.connector_id and b.provider_id = c.provider_id and c.app_id = d.id " +
            " and a.app_id = d.id and (${appIds})   ")
    Set<String> querySiecodeByAppId(@Param("appIds") String appIds);

    /**
     * 获取应用拥有的测点数量
     *
     * @param appIds
     * @return
     */
    @Select("select app_id as appId, b.ff_app_id as ffAppId,  count(siecode) as sensorCout " +
            " from da_config_sensor a,  " +
            "   da_config_application b " +
            "where  a.app_id = b.id and (${appIds})  group by app_id, b.ff_app_id ")
    List<AppSensor> queryAppSensorMap(@Param("appIds") String appIds);

    /**
     * selectByConnectorIds
     *
     * @param connectorIds
     * @param appIds
     * @return
     */
    List<DaConfigSensor> selectByConnectorIds(@Param("connectorIds") List<Long> connectorIds, @Param("appIds") List<Long> appIds);

    /**
     * 获取应用的提供者拥有的测点数量
     *
     * @param appId
     * @return
     */
    @Select("select b.provider_id as providerId, d.name,  count(a.siecode) as sensorCount from da_config_sensor a, " +
            " da_config_provider_connector b, " +
            " da_config_app_provider c ," +
            " da_config_provider d " +
            " where a.connector_id = b.connector_id " +
            " and b.provider_id = c.provider_id " +
            " and b.provider_id = d.id " +
            "    and a.app_id = #{appId} " +
            " and c.app_id = #{appId} " +
            " group by b.provider_id,  d.name ")
    List<ProviderSensor> queryProviderSensorMap(@Param("appId") Long appId);

    /**
     * 获取app下的测点
     *
     * @param appIds
     * @return
     */
    @Select(" select siecode as sieCode, app_id as appId from da_config_sensor where 1=1 and  (${appIds}) ")
    List<AppSieCode> querySieCodeMap(@Param("appIds") String appIds);

    /**
     * 获取应用使用
     *
     * @param appIds
     * @return
     */
    @Select(" select a.appid as globalAppId, b.ff_app_id as ffAppId, count(objectid) as sensorCout  " +
            " from da_app_resource a, da_config_application b  " +
            "  where a.appid = b.global_app_id " +
            " and (${appIds})" +
            " group by a.appid, b.ff_app_id ")
    List<AppUsageCount> queryAppUsage(@Param("appIds") String appIds);


    /**
     * 获取资源所属的app
     *
     * @param sieCode
     * @return
     */
    @Select("   select siecode as sieCode, app_id as appId from da_config_sensor where siecode =  #{sieCode} ")
    List<AppSieCode> querySieCode(@Param("sieCode") String sieCode);

    /**
     * updateStatusToImportedByCondition
     *
     * @param tempTagList
     * @param connectorId
     * @return
     */
    int updateStatusToImportedByCondition(@Param("tagList") List<String> tempTagList, @Param("connectorId") Long connectorId);

    /**
     * deleteBySieCode
     *
     * @param siecode
     * @return
     */
    @Delete("delete from da_config_sensor where siecode =  #{siecode}")
    int deleteBySieCode(@Param("siecode") String siecode);

    /**
     * deleteRelationBySieCode
     *
     * @param siecode
     * @return
     */
    @Delete("delete from da_config_sensor_group where sensor_code = #{siecode}")
    int deleteRelationBySieCode(@Param("siecode") String siecode);


    /**
     * deleteRelationBySieCode
     *
     * @param siecode
     * @return
     */
    @Delete("delete from trendwarning_validsensorrule where sensorid = #{siecode}")
    int deleteValidRuleBySieCode(@Param("siecode") String siecode);


    /**
     * updateValidRuleBySieCode
     *
     * @param oldSiecode
     * @param newSiecode
     * @return
     */
    @Update("update trendwarning_validsensorrule set sensorid = #{newSiecode} where sensorid = #{oldSiecode}")
    int updateValidRuleBySieCode(@Param("oldSiecode") String oldSiecode, @Param("newSiecode") String newSiecode);

    /**
     * selectByAppAndConnectorAndTag
     *
     * @param applicationId
     * @param connectorId
     * @param tag
     * @return
     */
    @Select("select * from da_config_sensor where app_id = #{appId} and connector_id = #{connectorId} and tag = #{tag}")
    List<DaConfigSensor> selectByAppAndConnectorAndTag(@Param("appId") Long applicationId, @Param("connectorId") Long connectorId, @Param("tag") String tag);

    /**
     * updateSiecodeOfSensorGroup
     *
     * @param origSiecode
     * @param siecode
     */
    @Update("update da_config_sensor_group set sensor_code = #{siecode} where sensor_code = #{origSiecode}")
    void updateSiecodeOfSensorGroup(@Param("origSiecode") String origSiecode, @Param("siecode") String siecode);

    /**
     * updateSensorInfo
     *
     * @param modifySensorRequest
     */
    @Update("update da_config_sensor set sieCode = #{request.siecode},connector_id = #{request.connectorId},tag = #{request.tag},status = 1,description = #{request.description}  where siecode = #{request.origSiecode}")
    void updateSensorInfo(@Param("request") SensorRegistrationRequest modifySensorRequest);

    /**
     * updateDescriptionBySiecode
     *
     * @param siecode
     * @param description
     */
    @Update("update da_config_sensor set description = #{description}  where siecode = #{siecode}")
    void updateDescriptionBySiecode(@Param("siecode") String siecode, @Param("description") String description);

    /**
     * updateDescriptionBySiecode
     *
     * @param origSiecode
     * @param siecode
     * @param description
     * @return result
     */
    @Update("update da_config_sensor set sieCode = #{siecode},description = #{description}  where siecode = #{origSiecode}")
    void updateSiecodeDescriptionBySiecode(@Param("siecode") String siecode, @Param("description") String description, @Param("origSiecode") String origSiecode);


    /**
     * selectListByConnectorIdAndSiecode
     *
     * @param connectorId
     * @param tag
     * @param applicationId
     * @return result
     */
    List<DaConfigSensor> selectListByConnectorIdAndSiecode(@Param("connectorId") Long connectorId, @Param("tag") String tag, @Param("applicationId") Long applicationId);

    /**
     * selectSensorCounts
     *
     * @return
     */
    @Select("select count(1) from da_config_sensor")
    int selectSensorCounts();


    /**
     * 批量删除
     * @param connectorId
     * @param delTagList
     * @return
     */
    int deleteBatchByConnectorId(@Param("connectorId") Long connectorId, @Param("tagList") Set<String> delTagList);


    /**
     * 根据connectorid和tag列表查询
     * @param connectorId
     * @param applicationId
     * @param sqlStr
     * @return
     */
    @Select("select a.siecode, a.tag from da_config_sensor a where a.connector_id = #{connectorId} and a.app_id = #{applicationId} and (${sqlStr})")
    List<DaConfigSensor> selectListByConnectorIdAndTags(@Param("connectorId") Long connectorId, @Param("applicationId") Long applicationId, @Param("sqlStr") String sqlStr);

}