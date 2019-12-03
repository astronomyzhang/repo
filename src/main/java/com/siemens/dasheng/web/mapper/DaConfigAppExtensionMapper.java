package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigAppExtension;
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
public interface DaConfigAppExtensionMapper extends MyMapper<DaConfigAppExtension> {
    /**
     * 保存
     * @param record
     * @return
     */
    int insert(DaConfigAppExtension record);

    /**
     * 保存
     * @param record
     * @return
     */
    int insertSelective(DaConfigAppExtension record);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DaConfigAppExtension record);

    /**
     * 更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(DaConfigAppExtension record);

    /**
     * 根据appId删除公用app
     * @param appId
     * @return
     */
    @Delete("delete from da_config_app_extension where app_id = #{appId}")
    int deleteByAppId(@Param("appId") Long appId);

    /**
     * 删除app下要删除的继承app
     * @param appId
     * @param extensionIds
     * @return
     */
    int deleteByAppIdAndExtensionIds(@Param("appId") Long appId, @Param("extensionIds") String extensionIds);

    /**
     * selectIdOR
     * @param
     * @return
     */
    @Select("select DA_CONFIG_APP_EXTENSION$SEQ.nextval from dual")
    @Options(flushCache= true)
    Long selectIdOR();

    /**
     * selectIdPG
     * @param
     * @return
     */
    @Select("select nextval('da_config_app_extension$seq') from dual")
    @Options(flushCache= true)
    Long selectIdPG();

    /**
     * 保存
     * @param daConfigAppExtensions
     * @return
     */
    public int saveList(List<DaConfigAppExtension> daConfigAppExtensions);

    /**
     * 保存oracle
     * @param daConfigAppExtensions
     * @return
     */
    public int saveListOracle(List<DaConfigAppExtension> daConfigAppExtensions);


    /**
     * selectByAppId
     * @param ffAppId
     * @return
     */
    @Select("SELECT app_id as appId,extension_app_id as extensionAppId FROM DA_CONFIG_APP_EXTENSION where app_id = #{appId}")
    List<DaConfigAppExtension> selectByAppId(@Param("appId") Long ffAppId);

    /**
     * selectByExtensionAppId
     * @param extensionAppId
     * @return
     */
    @Select("SELECT app_id as appId,extension_app_id as extensionAppId FROM DA_CONFIG_APP_EXTENSION where extension_app_id = #{extensionAppId}")
    List<DaConfigAppExtension> selectByExtensionAppId(@Param("extensionAppId") Long extensionAppId);

    /**
     * 获取app继承的appid
     * @param appId
     * @return
     */
    @Select("SELECT extension_app_id  FROM DA_CONFIG_APP_EXTENSION where app_id = #{appId}")
    Set<Long> queryExtensionIdByAppId(@Param("appId") Long appId);
}