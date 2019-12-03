package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaAppResourceUsage;
import com.siemens.dasheng.web.model.TrendwarningValidsensorrule;
import com.siemens.dasheng.web.model.dto.AppOwnSensorUsage;
import com.siemens.dasheng.web.model.dto.ProviderUsage;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * DaAppResourceUsageMapper
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
public interface DaAppResourceUsageMapper  extends MyMapper<DaAppResourceUsage> {

    /**
     * selectIdOR
     * @param
     * @return
     */
    @Select("select da_app_resource$SEQ.nextval from dual")
    @Options(flushCache= true)
    Long selectIdOR();

    /**
     * selectIdPG
     * @param
     * @return
     */
    @Select("select nextval('da_app_resource$seq') from dual")
    @Options(flushCache= true)
    Long selectIdPG();

    /**
     * 批量保存
     * @param daAppResourceUsages
     * @return
     */
    public int saveList(List<DaAppResourceUsage> daAppResourceUsages);


    /**
     * 获取已经使用资源
     * @param consumerobjectId
     * @param objectId
     * @param objectType
     * @param appId
     * @return
     */
    public List<DaAppResourceUsage> queryUseResource(@Param("consumerobjectId") String consumerobjectId, @Param("objectId") String objectId,
                                                     @Param("objectType") String objectType, @Param("appId") String appId);

    /**
     * 释放资源
     * @param consumerobjectId
     * @param objectId
     * @param objectType
     * @param appId
     * @return
     */
    public int releaseResource(@Param("consumerobjectId") String consumerobjectId, @Param("objectId") String objectId,
                               @Param("objectType") String objectType, @Param("appId") String appId);

    /**
     * 批量获取已经使用资源
     * @param consumerobjectId
     * @param objectIds
     * @param objectType
     * @param appId
     * @return
     */
    public List<DaAppResourceUsage> queryUseResourceList(@Param("consumerobjectId") String consumerobjectId, @Param("objectIds") List<String> objectIds,
                                                     @Param("objectType") String objectType, @Param("appId") String appId);


    /**
     * 获取继承app测点使用情况
     * @param ownAppId
     * @param appId
     * @param objectType
     * @return
     */
    @Select(" select a.ownappid as ownAppId, b.ff_app_id as appId,  count(a.objectid) as useCount " +
            "from da_app_resource a, da_config_application b " +
            "where a.ownappid = b.id " +
            "and (${ownAppId}) " +
            "and a.appid = #{appId}  " +
            " and a.objecttype = #{objectType} " +
            "group by a.ownappid, b.ff_app_id")
    List<AppOwnSensorUsage> queryAppOwnSensorUsage(@Param("ownAppId") String ownAppId,
                                                   @Param("appId") String appId, @Param("objectType") String objectType);


    /**
     * 获取提供者下测点使用情况
     * @param providerId
     * @return
     */
    @Select("  select b.provider_id as providerId, e.name,  count(d.objectid) as useCount from da_config_provider  a , " +
            " da_config_provider_connector b, " +
            " da_config_sensor c, " +
            " da_app_resource d, " +
            " da_config_provider e " +
            " where " +
            " a.id = b.provider_id " +
            " and b.connector_id = c.connector_id " +
            " and c.siecode = d.objectid " +
            " and b.provider_id = e.id " +
            " and (${providerId}) " +
            " group by b.provider_id, e.name ")
    List<ProviderUsage> queryProviderUsage(@Param("providerId") String providerId);

    /**
     * 提供者导入的测点
     * @param providerId
     * @param appId
     * @return
     */
    @Select(" select b.provider_id as providerId, c.name , count(a.siecode) as useCount " +
            "from da_config_sensor a, da_config_provider_connector b, da_config_provider c " +
            "where a.connector_id = b.connector_id " +
            "and b.provider_id = c.id " +
            "and (${providerId})  " +
            "and a.app_id = #{appId} " +
            "group by  b.provider_id, c.name ")
    List<ProviderUsage> queryProviderSensorList(@Param("providerId") String providerId, @Param("appId") Long appId);

    /**
     * 获取对应资源引用情况
     * @param objectId
     * @param objectType
     * @param appId
     * @return
     */
    public List<DaAppResourceUsage> queryUseResourceByConnection(@Param("objectId") String objectId,
                                                     @Param("objectType") String objectType, @Param("appId") String appId);


    /**
     * 获取对应资源引用情况
     * @param objectId
     * @param objectType
     * @return
     */
    public List<DaAppResourceUsage> queryUseResourceBySensorOrGroup(@Param("objectId") String objectId,
                                                                 @Param("objectType") String objectType);


    /**
     * 获取对应资源引用情况
     * @param objectIds
     * @param objectType
     * @return
     */
    public List<DaAppResourceUsage> queryUseResourcesBySensorOrGroup(@Param("objectIds") List<String> objectIds,
                                                                    @Param("objectType") String objectType);

    /**
     * 获取测点在本应用使用情况
     * @param objectIds
     * @param objectType
     * @param appId
     * @return
     */
    public List<DaAppResourceUsage> queryUseResourcesBySensorOrGroupOrAppId(@Param("objectIds") List<String> objectIds,
                                                                     @Param("objectType") String objectType, @Param("appId") String appId);


    /**
     * TrendwarningValidsensorrule
     * @param id
     * @return
     */
    @Select("select * from trendwarning_validsensorrule where id = #{id}")
    public TrendwarningValidsensorrule getSensorRuleById(@Param("id") String id);



}