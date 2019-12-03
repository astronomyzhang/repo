package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaConfigConnectorType;
import org.apache.ibatis.annotations.Param;
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
public interface DaConfigConnectorTypeMapper {
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
    int insert(DaConfigConnectorType record);

    /**
     * insertSelective
     * @param record
     * @return
     */
    int insertSelective(DaConfigConnectorType record);

    /**
     * selectByPrimaryKey
     * @param id
     * @return
     */
    DaConfigConnectorType selectByPrimaryKey(String id);

    /**
     * selectByDataType
     * @return
     */
    @Select("select id,name,data_type as dataType from da_config_connector_type order by id desc")
    List<DaConfigConnectorType> selectByDataType();

    /**
     * updateByPrimaryKeySelective
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(DaConfigConnectorType record);

    /**
     * updateByPrimaryKey
     * @param record
     * @return
     */
    int updateByPrimaryKey(DaConfigConnectorType record);
}