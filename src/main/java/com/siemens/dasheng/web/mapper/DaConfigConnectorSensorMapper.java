package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.model.DaConfigConnectorSensorPlus;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author xuxin
 */
@Component
public interface DaConfigConnectorSensorMapper extends MyMapper<DaConfigConnectorSensor> {

    /**
     * selectIdOR
     * @param
     * @return
     */
    @Select("select DA_CONFIG_CONNECTOR_SENSOR$SEQ.nextval from dual")
    @Options(flushCache= true)
    Long selectIdOR();

    /**
     * selectIdPG
     * @param
     * @return
     */
    @Select("select nextval('da_config_connector_sensor$seq') from dual")
    @Options(flushCache= true)
    Long selectIdPG();

    /**
     * Delete <connector - sensor> mapping table records by connectorId
     *
     * @param connectorId
     */
    @Delete("delete from da_config_connector_sensor where connector_id = #{connectorId}")
    void deleteByConnectorId(@Param("connectorId") Long connectorId);

    /**
     * batch insert(oracle)
     *
     * @param daConfigConnectorSensorList
     * @return
     */
    int saveListOracle(@Param("list") List<DaConfigConnectorSensor> daConfigConnectorSensorList);

    /**
     * selectCountByCondition
     *
     * @param connectorStatus
     * @param status
     * @param searchContent
     * @param connectorIds
     * @param applicationId
     * @return
     */
    int selectCountByCondition(@Param("connectorStatus") Long connectorStatus, @Param("status") Long status, @Param("searchContent") String searchContent, @Param("connectorIds") List<Long> connectorIds, @Param("appId") Long applicationId);


    /**
     * selectListByConditionPG
     *
     * @param applicationId
     * @param connectorIds
     * @param connectorStatus
     * @param status
     * @param searchContent
     * @param i
     * @param pageSize
     * @return
     */
    List<DaConfigConnectorSensorPlus> selectListByConditionPG(@Param("appId") Long applicationId, @Param("connectorIds") List<Long> connectorIds, @Param("connectorStatus") Long connectorStatus, @Param("status") Long status, @Param("searchContent") String searchContent, @Param("start") int i, @Param("pageNum") int pageSize);

    /**
     * selectListByConnectorId
     * @param connectorId
     * @return result
     */
    List<DaConfigConnectorSensorPlus> selectListByConnectorId(@Param("connectorId") Long connectorId);


    /**
     * selectListByConnectorIdAndSiecode
     * @param connectorId
     * @param tag
     * @return result
     */
    List<DaConfigConnectorSensorPlus> selectListByConnectorIdAndSiecode(@Param("connectorId") Long connectorId, @Param("tag") String tag);


    /**
     * selectListByConditionORAC
     *
     * @param applicationId
     * @param connectorIds
     * @param connectorStatus
     * @param status
     * @param searchContent
     * @param i
     * @param pageSize
     * @return
     */
    List<DaConfigConnectorSensorPlus> selectListByConditionORAC(@Param("appId") Long applicationId, @Param("connectorIds") List<Long> connectorIds, @Param("connectorStatus") Long connectorStatus, @Param("status") Long status, @Param("searchContent") String searchContent, @Param("start") int i, @Param("pageNum") int pageSize);


    /**
     * updateStatusToDelByCondition
     *
     * @param tagList
     * @param connectorId
     * @return
     */
    int updateStatusToDelByCondition(@Param("tagList") Set<String> tagList, @Param("connectorId") Long connectorId);

    /**
     * updateStatusToExistByCondition
     *
     * @param tagList
     * @param connectorId
     * @return
     */
    int updateStatusToExistByCondition(@Param("tagList") List<String> tagList, @Param("connectorId") Long connectorId);

    /**
     * selectByIds
     *
     * @param conSensorIds
     * @return
     */
    List<DaConfigConnectorSensorPlus> selectByIds(@Param("ids") List<Long> conSensorIds);

    /**
     * updateStatusImportedByIds
     *
     * @param conSensorIds
     * @return
     */
    int updateStatusImportedByIds(@Param("ids") List<Long> conSensorIds);

    /**
     * deleteUnImportTagByTagList
     *
     * @param connectorId
     * @param delTagList
     * @return
     */
    int deleteUnImportTagByTagList(@Param("connectorId") Long connectorId, @Param("tagList") Set<String> delTagList);

    /**
     * Delete <connector - sensor> mapping table records by connectorId
     *
     * @param connectorIds
     */
    @Delete("delete from da_config_connector_sensor where 1=1 and (${connectorIds})")
    void batchDeleteByConnectorId(@Param("connectorIds") String connectorIds);

    /**
     * deleteByConnectorIdAndTag
     * @param connectorId
     * @param tag
     */
    @Delete("delete from da_config_connector_sensor where connector_id = #{connectorId} and tag = #{tag}")
    void deleteByConnectorIdAndTag(@Param("connectorId") Long connectorId,@Param("tag") String tag);

    /**
     * updatePrefixById
     * @param id
     * @param daPrefix
     * @param hdaPrefix
     */
    @Update("update da_config_connector_sensor set da_prefix = #{daPrefix},hda_prefix = #{hdaPrefix} where id = #{id}")
    void updatePrefixById(@Param("id") Long id,@Param("daPrefix") String daPrefix,@Param("hdaPrefix") String hdaPrefix);

    /**
     * 根据tag列表查询
     * @param sqlStr
     * @return
     */
    @Select("select a.connector_id as connectorId, a.tag from da_config_connector_sensor a where (${sqlStr})")
    List<DaConfigConnectorSensor> selectListByTags(@Param("sqlStr") String sqlStr);
}