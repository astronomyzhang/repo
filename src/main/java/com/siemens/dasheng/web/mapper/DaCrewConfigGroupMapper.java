package com.siemens.dasheng.web.mapper;

import com.siemens.dasheng.web.model.DaCrewConfigGroup;
import com.siemens.dasheng.web.model.dto.SensorGroupMapping;
import com.siemens.dasheng.web.util.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author allan
 * Created by z0041dpv on 4/18/2019.
 */
@Component
public interface DaCrewConfigGroupMapper  extends MyMapper<DaCrewConfigGroup>  {
    /**
     *insert
     * @param record
     * @return
     */
    @Override
    int insert(DaCrewConfigGroup record);

    /**
     *insertSelective
     * @param record
     * @return
     */
    @Override
    int insertSelective(DaCrewConfigGroup record);

    /**
     *updateByPrimaryKeySelective
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKeySelective(DaCrewConfigGroup record);

    /**
     * updateByPrimaryKey
     * @param record
     * @return
     */
    @Override
    int updateByPrimaryKey(DaCrewConfigGroup record);

    /**
     * 获取已经映射的机组测点组
     * @param powerPlantId
     * @param crewId
     * @param searchStr
     * @return
     */
    public List<SensorGroupMapping> getSensorGroupMapping(@Param("powerPlantId") String powerPlantId,
                                                          @Param("crewId") String crewId,
                                                          @Param("searchStr") String searchStr);

    /**
     * 获取机组已经关联的
     * @param crewId
     * @return
     */
    @Select("select group_id from da_config_crew_group where crew_id = #{crewId}")
    Set<Long> queryGroupIdByCrewId(@Param("crewId") String crewId);

    /**
     * 多个保存
     * @param daCrewConfigGroups
     * @return
     */
    public int saveList(List<DaCrewConfigGroup> daCrewConfigGroups);

    /**
     * 批量插入oracle
     * @param daCrewConfigGroups
     * @return
     */
    public int saveListOracle(List<DaCrewConfigGroup> daCrewConfigGroups);

    /**
     * 删除关联关系
     * @param groupIds
     * @param crewId
     * @param powerPlantId
     * @return
     */
    int deleteByGroupId(@Param("groupIds") String groupIds, @Param("crewId") String crewId, @Param("powerPlantId") String powerPlantId);



}