package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.request.DaCrewGroupSaveRequest;
import com.siemens.dasheng.web.request.GetCrewSensorGroupRequest;
import com.siemens.dasheng.web.service.CrewConfigGroupService;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.OPERATE_FAIL;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.OPERATION_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @author allan
 * Created by z0041dpv on 4/18/2019.
 */
@Api(value = "crew and config group controller", description = "crew and config group controller")
@Controller
@RequestMapping("/crew/config/group")
public class DaCrewConfigGroupController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CrewConfigGroupService crewConfigGroupService;

    @ApiOperation(value = "get crew sensor group", notes = "get crew sensor group")
    @RequestMapping(value = "/getCrewSensorGroup", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "CREW", businessDescription = "get crew sensor group", webUrl = "/crew/config/group/getCrewSensorGroup")
    public ModelMap getCrewSensorGroup(@RequestBody GetCrewSensorGroupRequest getCrewSensorGroupRequest,
                                       HttpServletRequest httpServletRequest) {
        logger.info("getCrewSensorGroup,"+ JSON.toJSONString(getCrewSensorGroupRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            if (getCrewSensorGroupRequest == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap =  crewConfigGroupService.filter(modelMap, getCrewSensorGroupRequest);
            if (!(Boolean)modelMap.get(IS_SUCCESS)) {
                return modelMap;
            }

            modelMap.put(DATA,  crewConfigGroupService.getCrewSensorGroup(getCrewSensorGroupRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
            return modelMap;
        } catch (Exception e) {
            logger.error("getCrewSensorGroup errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "get crew sensor group", notes = "get crew sensor group")
    @RequestMapping(value = "/getSensorGroupMapping", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "CREW", businessDescription = "get crew sensor group", webUrl = "/crew/config/group/getSensorGroupMapping")
    public ModelMap getSensorGroupMapping(@RequestBody GetCrewSensorGroupRequest getCrewSensorGroupRequest,
                                       HttpServletRequest httpServletRequest) {
        logger.info("getSensorGroupMapping,"+ JSON.toJSONString(getCrewSensorGroupRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            if (getCrewSensorGroupRequest == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap =  crewConfigGroupService.filter(modelMap, getCrewSensorGroupRequest);
            if (!(Boolean)modelMap.get(IS_SUCCESS)) {
                return modelMap;
            }

            modelMap.put(DATA,  crewConfigGroupService.getSensorGroupMapping(getCrewSensorGroupRequest));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
            return modelMap;
        } catch (Exception e) {
            logger.error("getSensorGroupMapping errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "saveCrewGroup", notes = "saveCrewGroup")
    @RequestMapping(value = "/saveCrewGroup", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType = "CREW", businessDescription = "get crew sensor group", webUrl = "/crew/config/group/getSensorGroupMapping")
    public ModelMap saveCrewGroup(@RequestBody DaCrewGroupSaveRequest daCrewGroupSaveRequest, HttpServletRequest httpServletRequest) {
        logger.info("saveCrewGroup,"+ JSON.toJSONString(daCrewGroupSaveRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            if (daCrewGroupSaveRequest == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap.put(MSG, OPERATION_SUCCESS);
            modelMap =  crewConfigGroupService.saveCrewGroup(modelMap, daCrewGroupSaveRequest);
            return modelMap;
        } catch (Exception e) {
            logger.error("saveCrewGroup errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }


}
