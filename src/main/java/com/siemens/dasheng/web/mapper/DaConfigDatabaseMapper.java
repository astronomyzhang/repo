package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigDatabase;
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
public interface DaConfigDatabaseMapper {
    /**
     * deleteByPrimaryKey
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * insert
     * @param record
     * @return
     */
    int insert(DaConfigDatabase record);

    /**
     * insertSelective
     * @param record
     * @return
     */
    int insertSelective(DaConfigDatabase record);

    /**
     * selectByPrimaryKey
     * @param id
     * @return
     */
    DaConfigDatabase selectByPrimaryKey(String id);

    /**
     * selectAllList
     * @return
     */
    @Select("select id,name,class_id as classId from da_config_database")
    List<DaConfigDatabase> selectAllList();

    /**
     * updateByPrimaryKeySelective
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DaConfigDatabase record);

    /**
     * updateByPrimaryKey
     * @param record
     * @return
     */
    int updateByPrimaryKey(DaConfigDatabase record);
}