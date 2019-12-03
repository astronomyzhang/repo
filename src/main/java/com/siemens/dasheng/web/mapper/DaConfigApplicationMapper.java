package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigApplication;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.dto.AppWithTime;
import com.siemens.dasheng.web.model.dto.DaConnectorStatus;
import com.siemens.dasheng.web.model.dto.ExistAppId;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public interface DaConfigApplicationMapper extends MyMapper<DaConfigApplication> {


    /**
     * select DaConnectorStatusList FleetFrameAppIdList
     *
     * @param appIds (FleetFrame app Ids)
     * @return
     */
    List<DaConnectorStatus> selectDaConnectorStatusListByFleetFrameAppIdList(@Param("appIds") List<String> appIds);

    /**
     * select Connected DaConfigConnectorList by appId
     *
     * @param appId
     * @return
     */
    @Select("select connector.id,connector.name,connector.data_type as dataType, connector.connector_type as connectorType, connector.archived_database as archivedDatabase,connector.connect_approach as connectApproach,connector.description,\n" +
            "connector.server_host as serverHost, connector.port,connector.sqldas,connector.user_name as userName,connector.password,  connector.connect_timeout as connectTimeout,connector.command_timeout as commandTimeout,connector.reconnect_inteval as reconnectInteval,\n" +
            "connector.reconnect_times as reconnectTimes,connector.status , connector.connector_class as connectorClass,connector.updated_by as updatedBy,connector.update_date as updateDate,connector.db_name as dbName,connector.database,\n" +
            "connector.connector_info as connectorInfo from\n" +
            "client_app ffapp,da_config_application daapp,da_config_app_provider\n" +
            "appProvider,\n" +
            "da_config_provider_connector providerConnector,da_config_connector connector\n" +
            "where ffapp.appid = #{appId} and ffapp.id = daapp.ff_app_id and daapp.id = appProvider.app_id and\n" +
            "appProvider.provider_id = providerConnector.provider_id and providerConnector.connector_id = connector.id and connector.status = 1 and connector.connect_approach ='OSIPIAFSERVER'")
    List<DaConfigConnector> selecyConnectedDaConfigConnectorListByAppId(@Param("appId") String appId);

    /**
     * Get PublicFleetFrameAppIdList By PrivateAppId
     *
     * @param appId
     * @return
     */
    @Select("select ffapp.appid from client_app ffapp,da_config_application daapp where daapp.id in (" +
            "select appExtension.extension_app_id from client_app ffapp,da_config_application daapp,da_config_app_extension appExtension " +
            "where ffapp.appid = #{appId} and daapp.ff_app_id = ffapp.id and daapp.id = appExtension.app_id ) and ffapp.id = daapp.ff_app_id")
    List<String> getPublicFleetFrameAppIdListByPrivateAppId(@Param("appId") String appId);

    /**
     * updatePrivateAppSensorMappingUpdateTimeByPublicAppId
     *
     * @param publicAppId
     * @param updateTime
     * @return
     */
    @Update("update da_config_application set sensormapping_update_time = #{updateTime} " +
            "where id in (select app.id from da_config_application app,da_config_app_extension appAxtension " +
            "where  appAxtension.extension_app_id = #{publicAppId} and appAxtension.app_id = app.id)")
    int updatePrivateAppSensorMappingUpdateTimeByPublicAppId(@Param("publicAppId") Long publicAppId, @Param("updateTime") Long updateTime);

    /**
     * 更新
     *
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKey(DaConfigApplication record);

    /**
     * Update sensor mapping updatetime
     *
     * @param record
     * @return
     */
    int updateSensorMappingUpdateTimeByPrimaryKey(DaConfigApplication record);

    /**
     * 获取某个类型的app private, public
     *
     * @param type
     * @param id
     * @return
     */
    List<DaConfigApplication> selectPublicAppList(@Param("type") Integer type, @Param("id") Long id);

    /**
     * 获取已经注册的app
     *
     * @return
     */
    List<ExistAppId> selectExistedAppId();

    /**
     * 获取注册app
     *
     * @param appIds
     * @return
     */
    List<DaConfigApplication> selectByAppIds(@Param("appIds") List<Long> appIds);

    /**
     * 获取注册app
     *
     * @param appIds
     * @param type
     * @return
     */
    List<DaConfigApplication> selectByIdsAndType(@Param("appIds") List<Long> appIds, @Param("type") Long type);

    /**
     * selectIdOR
     *
     * @param
     * @return
     */
    @Select("select DA_CONFIG_APPLICATION$SEQ.nextval from dual")
    @Options(flushCache = true)
    Long selectIdOR();

    /**
     * selectIdPG
     *
     * @param
     * @return
     */
    @Select("select nextval('da_config_application$seq') from dual")
    @Options(flushCache = true)
    Long selectIdPG();


    /**
     * selectAppList
     *
     * @return
     */
    @Select("select id as id, ff_app_id as  ffAppId, type as type,global_app_id as globalAppId  from da_config_application")
    List<DaConfigApplication> selectAppList();

    /**
     * selectAppById
     *
     * @param id
     * @return
     */
    @Select("select id as id, ff_app_id as  ffAppId, type as type,register_date as dateTime,register_user_id as userName from da_config_application where id = #{id}")
    AppWithTime selectAppById(@Param("id") Long id);

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    DaConfigApplication selectById(@Param("id") Long id);

    /**
     * 获取已经注册的app
     *
     * @param appId
     * @return
     */
    List<ExistAppId> selectByAppId(@Param("appId") Long appId);

    /**
     * 通过appid和ID获取
     *
     * @param appId
     * @param id
     * @return
     */
    List<ExistAppId> selectByAppIdAndId(@Param("appId") Long appId, @Param("id") Long id);


    /**
     * 根据ID获取
     *
     * @param appId
     * @return
     */
    DaConfigApplication selectByFleetAppId(@Param("appId") String appId);

    /**
     * GetAppIdByGlobalId
     * @param globalId
     * @return
     */
    @Select("select id from da_config_application where global_app_id = #{globalId} and type = 1")
    Long getPublicAppIdByGlobalId(@Param("globalId") String globalId);

}