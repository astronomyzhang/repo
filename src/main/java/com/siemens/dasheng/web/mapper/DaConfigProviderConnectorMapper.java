package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigProviderConnector;
import com.siemens.dasheng.web.model.dto.DaCoreConnector;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/28
 */
@Component
public interface DaConfigProviderConnectorMapper extends MyMapper<DaConfigProviderConnector> {



    /**
     * updateByPrimaryKey
     *
     * @param recordList
     * @return
     */
    int batchInsertOR(@Param("recordList") List<DaConfigProviderConnector> recordList);

    /**
     * batchInsertPG
     *
     * @param recordList
     * @return
     */
    int batchInsertPG(@Param("recordList") List<DaConfigProviderConnector> recordList);

    /**
     * deleteByProviderId
     *
     * @param providerId
     * @return
     */
    @Delete("delete from da_config_provider_connector where provider_id = #{providerId}")
    int deleteByProviderId(@Param("providerId") Long providerId);

    /**
     * deleteByConnectId
     *
     * @param id
     */
    @Delete("delete from da_config_provider_connector where connector_id = #{id}")
    void deleteByConnectId(@Param("id") Integer id);

    /**
     * get active connector list
     *
     * @param providerIds
     * @return
     */
    List<DaCoreConnector> queryActiveProConList(@Param("providerIds") List<Long> providerIds);

    /**
     * selectByProIds
     *
     * @param providerIds
     * @return
     */
    List<DaConfigProviderConnector> selectByProIds(@Param("providerIds") List<Long> providerIds);

    /**
     * selectActiveProviderByProIds
     *
     * @param providerIds
     * @return
     */
    List<DaConfigProviderConnector> selectActiveProviderByProIds(@Param("providerIds") List<Long> providerIds);

    /**
     *获取ghost connector
     * @param connectorId
     * @param appId
     * @return
     */
    @Select(" select  distinct b.id from da_config_provider_connector a,  " +
            "da_config_connector b, " +
            "da_config_app_provider c " +
            "where a.connector_id = b.id " +
            "and a.provider_id = c.provider_id  " +
            "and (${connectorId})  " +
            "and c.app_id != #{appId} ")
    List<Long> selectGhostConnector(@Param("connectorId") String connectorId, @Param("appId") Long appId);

    /**
     * selectGhostConnectorExcludeProviderId
     * @param connectorIds
     * @param providerId
     * @return
     */
    @Select(" select  distinct b.id from da_config_provider_connector a,  " +
            "da_config_connector b, " +
            "da_config_app_provider c " +
            "where a.connector_id = b.id " +
            "and a.provider_id = c.provider_id  " +
            "and (${connectorId})  " +
            "and a.provider_id != #{providerId} ")
    List<Long> selectGhostConnectorExcludeProviderId(@Param("connectorId") String connectorIds, @Param("providerId") Long providerId);
}