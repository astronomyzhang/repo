package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigProvider;
import com.siemens.dasheng.web.model.dto.DaProviderSensorMapping;
import com.siemens.dasheng.web.request.QueryProviderRequest;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo class
 * @author xuxin
 * @date 2018/11/28
 */
@Component
public interface DaConfigProviderMapper {
    /**
     * selectIdOR
     * @param
     * @return
     */
    @Select("select DA_CONFIG_PROVIDER$SEQ.nextval from dual")
    @Options(flushCache= true)
    Long selectIdOR();

    /**
     * selectIdPG
     * @param
     * @return
     */
    @Select("select nextval('da_config_provider$seq') from dual")
    @Options(flushCache= true)
    Long selectIdPG();

    /**
     * deleteByPrimaryKey
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     * insert
     * @param record
     * @return
     */
    int insert(DaConfigProvider record);



    /**
     * selectByPrimaryKey
     * @param id
     * @return
     */
    DaConfigProvider selectByPrimaryKey(Long id);

    /**
     * queryListByCondition
     * @param queryProviderRequest
     * @return
     */
    List<DaConfigProvider> queryListByCondition(@Param("params")QueryProviderRequest queryProviderRequest);

    /**
     * queryById
     * @param id
     * @return
     */
    DaConfigProvider queryById(@Param("id") Long id);

    /**
     * updateByPrimaryKeySelective
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(@Param("record") DaConfigProvider record);

    /**
     * selectByConnectId
     * @param id
     * @return
     */
    @Select("select * from da_config_provider where id in (select provider_id from da_config_provider_connector where connector_id = #{id})")
    List<DaConfigProvider> selectByConnectId(@Param("id") Integer id);

    /**
     * Get MockSensorMappingByProviderId
     * @param providerId
     * @return
     */
    @Select("select connectorSensor.siecode sieCode,connectorSensor.connector_id connectorId, connectorSensor.tag tag from da_config_connector_sensor connectorSensor,da_config_connector connector,da_config_provider_connector providerConnector " +
            "where providerConnector.provider_id = #{providerId} and providerConnector.connector_id = connector.id and connector.status =1 and connectorSensor.connector_id = connector.id")
    List<DaProviderSensorMapping> getMockSensorMappingByProviderId(@Param("providerId") long providerId);

    /**
     * Get selectByIds
     * @param providerIds
     * @return
     */
    List<DaConfigProvider> selectByIds(@Param("providerIds") List<Long> providerIds);

    /**
     * Get queryApplicationProvider
     * @param applicationId
     * @return
     */
    @Select("select a.id,a.name,a.description,a.connector_class as connectorClass,c.name as connectorClassName ,a.status,a.availability_rate as availabilityRate from da_config_provider a left join da_config_app_provider b on b.provider_id = a.id " +
            "left join da_config_connector_class c on c.id = a.connector_class where b.app_id = #{applicationId}")
    List<DaConfigProvider> queryApplicationProvider(@Param("applicationId") Long applicationId);

    /**
     *通过提供者获取
     * @param providerIds
     * @param status
     * @return
     */
    List<DaConfigProvider> selectByIdsAndStatus(@Param("providerIds") List<Long> providerIds, @Param("status") Long status);


    /**
     * 获取提供者
     * @param connectorType
     * @param connectorClass
     * @return
     */
    List<DaConfigProvider> multiQueryListByCondition(@Param("connectorType") String connectorType,
                                                     @Param("connectorClass") String connectorClass);
}