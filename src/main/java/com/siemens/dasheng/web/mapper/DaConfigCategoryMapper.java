package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigCategory;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2018/11/21
 */
@Component
public interface DaConfigCategoryMapper {
    /**
     * deleteByPrimaryKey
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * insertDaConfigCategory
     * @param record
     * @return
     */
    int insert(DaConfigCategory record);

    /**
     * insertSelectiveDaConfigCategory
     * @param record
     * @return
     */
    int insertSelective(DaConfigCategory record);

    /**
     * insertSelectiveDaConfigCategory
     * @param id
     * @return
     */
    DaConfigCategory selectByPrimaryKey(String id);

    /**
     * insertSelectiveDaConfigCategory
     * @return
     */
    @Select("select id,name,database_id as databaseId from da_config_category")
    List<DaConfigCategory> selectAllList();

    /**
     * updateByPrimaryKeySelective
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DaConfigCategory record);

    /**
     * updateByPrimaryKey
     * @param record
     * @return
     */
    int updateByPrimaryKey(DaConfigCategory record);
}