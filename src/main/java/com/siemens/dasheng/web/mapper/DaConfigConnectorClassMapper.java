package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigConnectorClass;
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
public interface DaConfigConnectorClassMapper {
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
    int insert(DaConfigConnectorClass record);

    /**
     * insertSelective
     * @param record
     * @return
     */
    int insertSelective(DaConfigConnectorClass record);

    /**
     * selectByPrimaryKey
     * @param id
     * @return
     */
    DaConfigConnectorClass selectByPrimaryKey(String id);

    /**
     * selectAllList
     * @return
     */
    @Select("select id,name ,connector_type_id as connectorTypeId from da_config_connector_class")
    List<DaConfigConnectorClass> selectAllList();

    /**
     * updateByPrimaryKeySelective
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DaConfigConnectorClass record);

    /**
     * updateByPrimaryKey
     * @param record
     * @return
     */
    int updateByPrimaryKey(DaConfigConnectorClass record);
}