package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.generalmodel.dataconnector.QueryConnectorRequest;
import com.siemens.dasheng.web.generalmodel.dataconnector.QueryConnectorResponse;
import com.siemens.dasheng.web.generalmodel.dataconnector.UpdateConnectorRequest;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.DaConfigConnectorPlus;
import com.siemens.dasheng.web.model.DaConfigProvider;
import com.siemens.dasheng.web.model.DaConnectorConfig;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public interface DaConfigConnectorMapper extends MyMapper<DaConfigConnector> {
    /**
     * selectIdOR
     * @param
     * @return
     */
    @Select("select DA_CONFIG_CONNECTOR$SEQ.nextval from dual")
    @Options(flushCache= true)
    Long selectIdOR();

    /**
     * selectIdPG
     * @param
     * @return
     */
    @Select("select nextval('da_config_connector$seq') from dual")
    @Options(flushCache= true)
    Long selectIdPG();


    /**
     * insert
     * @param record
     * @return
     */
    @Override
    int insert(DaConfigConnector record);

    /**
     * updateByPrimaryKeySelective
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKeySelective(DaConfigConnector record);

    /**
     * updateByPrimaryKey
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKey(DaConfigConnector record);

    /**
     * selectConnectorByClass
     * @param classId
     * @return
     */
    @Select("select id,name,data_type as dataType ,connector_type as connectorType ,archived_database as archivedDatabase ,connect_approach as connectApproach,description,server_host as serverHost,port,sqldas,connector_class as connectorClass,status,db_name as dbName,database,connector_info as connectorInfo  from DA_CONFIG_CONNECTOR where connector_class = #{classId}")
    List<DaConfigConnector> selectConnectorByClass(@Param("classId") String classId);

    /**
     * selectIdsByIds
     * @param connectorIds
     * @return
     */
    List<DaConfigConnector> selectConnectorsByIds(@Param("connectorIds") List<Long> connectorIds);

    /**
     * selectIdsByIds
     * @return
     */
    @Select("select id,name,data_type as dataType,connector_type as connectorType,archived_database as archivedDatabase,connect_approach as connectApproach,description,server_host as serverHost,port,sqldas,connector_class as connectorClass,user_name as userName,password,connector_info as connectorInfo,url,da_server_name as daServerName,hda_server_name as hdaServerName,gateway,database from da_config_connector ")
    List<DaConfigConnector> selectAllConnectors();

    /**
     * queryConnectorListByProviderId
     * @param providerId
     * @return
     */
    List<DaConfigConnector> queryConnectorListByProviderId(Long providerId);


    /**
     * queryConnectorListByProviderId
     * @param providerIds
     * @return List<DaConfigConnector>
     */
    List<DaConfigConnectorPlus> queryConnectorListByProviderIds(@Param("providerIds") List<Long> providerIds);

    /**
     *queryConnectorReferencedProviders
     * @param connectorID
     * @return
     */
    List<DaConfigProvider> queryConnectorReferencedProviders(Long connectorID);

    /**
     * select connector by connectorID
     * @param connectorID
     * @return
     */
    DaConfigConnector selectConnectorById(Long connectorID);

    /**
     * queryConnectors
     * @param request
     * @return
     */
    List<QueryConnectorResponse> queryConnectors(@Param("request") QueryConnectorRequest request);

    /**
     * selectById
     * @param id
     * @return
     */
    @Select("select id,name,data_type as dataType,connector_type as connectorType,archived_database as archivedDatabase,connect_approach as connectApproach,description,server_host as serverHost,port,sqldas,connector_class as connectorClass,user_name as userName,password,connector_info as connectorInfo,url,da_server_name as daServerName,hda_server_name as hdaServerName, gateway from da_config_connector where id = #{id}")
    DaConfigConnector selectById(@Param("id") Long id);

    /**
     * deleteById
     * @param id
     */
    @Delete("delete from da_config_connector where id = #{id}")
    void deleteById(@Param("id") Long id);

    /**
     * updateById
     * @param request
     * @param username
     * @param currentTimeMillis
     * @param connectorInfo
     */
    @Update("update da_config_connector set name = #{request.name},archived_database = #{request.archivedDatabase},connect_approach = #{request.connectApproach},description= #{request.description}," +
            "server_host = #{request.serverHost},port = #{request.port},sqldas = #{request.sqldas},user_name = #{request.userName},password = #{request.password},connect_timeout= #{request.connectTimeout},command_timeout = #{request.commandTimeout},reconnect_inteval = #{request.reconnectInteval},reconnect_times = #{request.reconnectTimes}," +
            "status = #{request.status},updated_by = #{username},update_date = #{currentTime},db_name = #{request.dbName},database = #{request.database},connector_info = #{connectorInfo}," +
            "url = #{request.url},da_server_name = #{request.daServerName}, hda_server_name = #{request.hdaServerName}, gateway = #{request.gateway}"+
            "where id = #{request.id}")
    void updateById(@Param("request") UpdateConnectorRequest request, @Param("username") String username, @Param("currentTime") long currentTimeMillis,@Param("connectorInfo") String connectorInfo);

    /**
     * updateUnConInfoById
     * @param request
     * @param username
     * @param currentTimeMillis
     */
    @Update("update da_config_connector set name = #{request.name},description= #{request.description}," +
            "connect_timeout= #{request.connectTimeout},command_timeout = #{request.commandTimeout},reconnect_inteval = #{request.reconnectInteval},reconnect_times = #{request.reconnectTimes}," +
            "updated_by = #{username},update_date = #{currentTime},db_name = #{request.dbName}" +
            "where id = #{request.id}")
    void updateUnConInfoById(@Param("request") UpdateConnectorRequest request,@Param("username") String username,@Param("currentTime") long currentTimeMillis);

    /**
     * batchUpdateStatusByPrimaryKey
     * @param connectorList
     */
    void batchUpdateStatusByPrimaryKey(@Param("connectorList") List<DaConfigConnector> connectorList);

    /**
     * selectIdsByIdsAsc
     * @param connectorIds
     * @param connectorStatus
     * @return
     */
    List<DaConnectorConfig> selectConnectorsByIdsAsc(@Param("connectorIds") List<Long> connectorIds,@Param("connectorStatus") Long connectorStatus);

    /**
     * selectByConnectInfo
     * @param connectorInfo
     * @return
     */
    @Select("select id,name,data_type as dataType,connector_type as connectorType,archived_database as archivedDatabase," +
            "connect_approach as connectApproach,description,server_host as serverHost,port,sqldas," +
            "connector_class as connectorClass,user_name as userName,password from da_config_connector where  connector_info = #{connectorInfo} ")
    DaConfigConnector selectByConnectInfo(@Param("connectorInfo") String connectorInfo);


}