package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.mapper.DaConfigGroupMapper;
import com.siemens.dasheng.web.mapper.DaCrewConfigGroupMapper;
import com.siemens.dasheng.web.model.DaConfigGroup;
import com.siemens.dasheng.web.model.DaCrewConfigGroup;
import com.siemens.dasheng.web.model.dto.GetSensorGroupResponse;
import com.siemens.dasheng.web.model.dto.SensorGroupMapping;
import com.siemens.dasheng.web.request.DaCrewGroupSaveRequest;
import com.siemens.dasheng.web.request.GetCrewSensorGroupRequest;
import com.siemens.dasheng.web.singleton.constant.CommonErrorConstant;
import com.siemens.dasheng.web.util.CollectionUtil;
import com.siemens.dasheng.web.util.ServiceUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.MSG;

/**
 * @author allan
 * Created by z0041dpv on 4/18/2019.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CrewConfigGroupService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaCrewConfigGroupMapper daCrewConfigGroupMapper;

    @Autowired
    private DaConfigGroupMapper daConfigGroupMapper;

    private  static final String DELETE = "delete";

    private  static final String ADD = "add";

    private  static final Long APP_ID = 1653L;


    /**
     * 获取机组下的机组
     * @param getCrewSensorGroupRequest
     * @return
     */
    public List<GetSensorGroupResponse> getCrewSensorGroup(GetCrewSensorGroupRequest getCrewSensorGroupRequest) {
        List<GetSensorGroupResponse> getSensorGroupResponses = new ArrayList<>();
        //获取指定app下面的测点组
        String searchStr = getCrewSensorGroupRequest.getSearchStr();
        if (StringUtils.isNotEmpty(searchStr)) {
            searchStr = searchStr.replaceAll("%", "\\\\%").replaceAll("'", "''").replaceAll("_", "\\\\_").toLowerCase();
        }

        List<DaConfigGroup> daConfigGroups = daConfigGroupMapper.getByFilter(APP_ID, searchStr);

        //获取已经关联的
        Set<Long> groupIdSet = daCrewConfigGroupMapper.queryGroupIdByCrewId(getCrewSensorGroupRequest.getCrewId());
        if (!CollectionUtils.isEmpty(daConfigGroups)) {
            for (DaConfigGroup daConfigGroup : daConfigGroups) {
                GetSensorGroupResponse groupResponse = new GetSensorGroupResponse();

                groupResponse.setQuoted(false);
                groupResponse.setId(daConfigGroup.getId());
                groupResponse.setGroupName(daConfigGroup.getName());
                groupResponse.setDescription(daConfigGroup.getDescription());
                groupResponse.setQuantity( daConfigGroup.getAccount() == null ? Integer.valueOf(0) : daConfigGroup.getAccount().intValue());

                if (groupResponse.getQuoted() == null) {
                    groupResponse.setQuoted(false);
                }
                if (groupIdSet != null) {
                    if (groupIdSet.add(daConfigGroup.getId())) {
                        groupResponse.setChecked(false);
                        groupResponse.setQuoted(false);
                    } else {
                        groupResponse.setChecked(true);
                    }
                } else {
                    groupResponse.setChecked(false);
                    groupResponse.setQuoted(false);
                }

                getSensorGroupResponses.add(groupResponse);
            }
        }

        return getSensorGroupResponses;
    }

    /**
     * 获取已经映射的机组测点组
     * @param getCrewSensorGroupRequest
     * @return
     */
    public List<SensorGroupMapping> getSensorGroupMapping(GetCrewSensorGroupRequest getCrewSensorGroupRequest) {
        List<SensorGroupMapping> sensorGroupMappings = new ArrayList<>();
        if (getCrewSensorGroupRequest == null) {
            return sensorGroupMappings;
        }
        String searchStr = getCrewSensorGroupRequest.getSearchStr();
        if (StringUtils.isNotEmpty(searchStr)) {
            searchStr = searchStr.trim();
            searchStr = searchStr.replaceAll("%", "\\\\%").replaceAll("'", "''").replaceAll("_", "\\\\_").toLowerCase();
        }
        return daCrewConfigGroupMapper.getSensorGroupMapping(getCrewSensorGroupRequest.getPowerPlantId(),
                getCrewSensorGroupRequest.getCrewId(), searchStr);
    }

    /**
     * 过滤参数
     * @param modelMap
     * @param getCrewSensorGroupRequest
     * @return
     */
    public ModelMap filter(ModelMap modelMap, GetCrewSensorGroupRequest getCrewSensorGroupRequest) {
        if (getCrewSensorGroupRequest != null) {
            if (StringUtils.isEmpty(getCrewSensorGroupRequest.getPowerPlantId())) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
        }
        return modelMap;
    }

    /**
     *
     * @param modelMap
     * @param daCrewGroupSaveRequest
     * @return
     */
    public ModelMap saveCrewGroup(ModelMap modelMap, DaCrewGroupSaveRequest daCrewGroupSaveRequest) {
        //校验保存参数
        modelMap = this.vefifyData(daCrewGroupSaveRequest, modelMap);
        if (!(Boolean)modelMap.get(IS_SUCCESS)) {
            return modelMap;
        }
        //获取机组已经勾选的机组
        Set<Long> groupIdSet = daCrewConfigGroupMapper.queryGroupIdByCrewId(daCrewGroupSaveRequest.getCrewId());

        List<Long> paramGroupIdList = daCrewGroupSaveRequest.getGroupIds();

        if (CollectionUtils.isEmpty(paramGroupIdList)) {
            paramGroupIdList = new ArrayList<>();
        }

        List<Long> saveList = new ArrayList<>(groupIdSet);

        //构造数据标志
        Map<String, List<Long>> resultMap = buildUpdateInfoMap(saveList, paramGroupIdList);
        List<Long> addIdList = resultMap.get(ADD);
        List<Long> deleteList = resultMap.get(DELETE);

        //构造新增数据
        if (!CollectionUtils.isEmpty(addIdList)) {
            List<DaCrewConfigGroup> daCrewConfigGroups =  buildAddList(addIdList, daCrewGroupSaveRequest.getCrewId(),
                    daCrewGroupSaveRequest.getPowerPlantId(), APP_ID);
            if (!CollectionUtils.isEmpty(daCrewConfigGroups)) {
                daCrewConfigGroupMapper.saveList(daCrewConfigGroups);
            }
        }
        //删除
        if (!CollectionUtils.isEmpty(deleteList)) {
            String deleteIds = StringUtils.join(deleteList, ",");

            String[] deleteIdStr = deleteIds.split(",");
            String groupIdSqlStr = ServiceUtil.mergeStrForIn(deleteIdStr, "group_id");
            daCrewConfigGroupMapper.deleteByGroupId(groupIdSqlStr, daCrewGroupSaveRequest.getCrewId(), daCrewGroupSaveRequest.getPowerPlantId());
        }

        return  modelMap;
    }

    /**
     * 构造新增数据
     * @param addIdList
     * @param crewId
     * @param powerPlantId
     * @param appId
     * @return
     */
    private List<DaCrewConfigGroup>  buildAddList(List<Long> addIdList, String crewId, String powerPlantId, Long appId) {
        List<DaCrewConfigGroup> daCrewConfigGroups = new ArrayList<>();
        if (!CollectionUtils.isEmpty(addIdList)) {
            DaCrewConfigGroup daCrewConfigGroup = null;
            for (Long addId : addIdList) {
                daCrewConfigGroup = new DaCrewConfigGroup();
                daCrewConfigGroup.setAppId(appId);
                daCrewConfigGroup.setCrewId(crewId);
                daCrewConfigGroup.setGroupId(addId);
                daCrewConfigGroup.setPowerPlantId(powerPlantId);
                daCrewConfigGroups.add(daCrewConfigGroup);
            }
        }
        return daCrewConfigGroups;
    }


    /**
     * 验证保存数据
     * @param daCrewGroupSaveRequest
     * @param modelMap
     * @return
     */
    private ModelMap vefifyData(DaCrewGroupSaveRequest daCrewGroupSaveRequest, ModelMap modelMap) {
        if (daCrewGroupSaveRequest == null) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }
        String crewId = daCrewGroupSaveRequest.getCrewId();
        if (StringUtils.isEmpty(crewId)) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }

        String powerPlantId = daCrewGroupSaveRequest.getPowerPlantId();
        if (StringUtils.isEmpty(powerPlantId)) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
            return modelMap;
        }
        return modelMap;
    }

    /**
     * 构造数据标志
     * @param saveList 已经保存数据
     * @param paramList 页面传过来的数据
     * @return
     */
    private Map<String, List<Long>> buildUpdateInfoMap(List<Long> saveList, List<Long> paramList) {
        Map<String, List<Long>> resutMap = new HashMap<>(2);
        resutMap.put(ADD, CollectionUtil.getAddaListThanbList(paramList, saveList));
        resutMap.put(DELETE, CollectionUtil.getReduceaListThanbList(paramList, saveList));
        return resutMap;
    }
}
