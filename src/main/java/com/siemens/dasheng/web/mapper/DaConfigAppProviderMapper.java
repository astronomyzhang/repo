package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaAppProviderMap;
import com.siemens.dasheng.web.model.DaConfigAppProvider;
import com.siemens.dasheng.web.model.dto.ProviderConnectorCount;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public interface DaConfigAppProviderMapper extends MyMapper<DaConfigAppProvider> {

    /**
     * selectByAppIds
     * @param appIds
     * @return
     */
    List<DaConfigAppProvider> selectByAppIds(@Param("appIds") List<Long> appIds);

    /**
     * 获取应用连接器数量
     * @param appIds
     * @return
     */
    List<DaAppProviderMap> getAppProviderMap(@Param("appIds") String appIds);

    /**
     * 根据appId删除app导入的连接器
     * @param appId
     * @return
     */
    @Delete("delete from da_config_app_provider where app_id = #{appId}")
    int deleteByAppId(@Param("appId") Long appId);

    /**
     * 删除app要删除的提供者
     * @param appId
     * @param providerIds
     * @return
     */
    int deleteByAppIdAndProviderId(@Param("appId") Long appId, @Param("providerIds") String providerIds);

    /**
     * selectIdOR
     * @param
     * @return
     */
    @Select("select DA_CONFIG_APP_PROVIDER$SEQ.nextval from dual")
    @Options(flushCache= true)
    Long selectIdOR();

    /**
     * selectIdPG
     * @param
     * @return
     */
    @Select("select nextval('da_config_app_provider$seq') from dual")
    @Options(flushCache= true)
    Long selectIdPG();

    /**
     * 保存(pgsql)
     * @param daConfigAppProviders
     * @return
     */
    public int saveList(@Param("list") List<DaConfigAppProvider> daConfigAppProviders);

    /**
     * 保存(oracle)
     * @param daConfigAppProviders
     * @return
     */
    public int saveListOracle(@Param("list") List<DaConfigAppProvider> daConfigAppProviders);

    /**
     * selectByProIds
     * @param proIdList
     * @return
     */
    List<DaConfigAppProvider> selectByProIds(@Param("proIdList") List<Long> proIdList);

    /**
     *queryProviderIdByAppId
     * @param appId
     * @return
     */
    @Select("SELECT provider_id  FROM da_config_app_provider where app_id = #{appId}")
    Set<Long> queryProviderIdByAppId(@Param("appId") Long appId);

    /**
     * 获取app下提供者没有连接器
     * @param appId
     * @return
     */
    @Select(" select d.* from ( " +
            "select a.provider_id as providerId,  count(b.connector_id) " +
            "connectorCount from da_config_app_provider a left join " +
            "da_config_provider_connector b on a.provider_id = b.provider_id " +
            "where " +
            "a.app_id = #{appId} " +
            "group by a.provider_id " +
            ") d where d.connectorCount = 0")
    List<ProviderConnectorCount> queryProviderConnectorCount(@Param("appId") Long appId);

    /**
     * 获取provider
     * @param proIdList
     * @param appId
     * @return
     */
    List<DaConfigAppProvider> selectByProIdsAndAppId(@Param("proIdList") List<Long> proIdList, @Param("appId") Long appId);
}