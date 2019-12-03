package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.request.DaConfigApplicationSyncRequest;
import com.siemens.dasheng.web.service.DaConfigApplicationSyncService;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import static com.siemens.dasheng.web.singleton.constant.ModelConstant.DATA;

/**
 * @author ly
 * @date 2019/05/15
 */
@Api(value = "config app sync", description = "config app sync")
@Controller
@RequestMapping("/config/sync")
public class DaConfigApplicationSyncController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigApplicationSyncService syncService;

    @ApiOperation(value = "sync app", notes = "sync app")
    @PostMapping("/syncApp")
    @ResponseBody
    @LogEventName(operateType = "SYNCAPP", businessDescription = "select app detail", webUrl = "/config/sync/syncApp")
    public ModelMap syncDaConfigApplication(@RequestBody DaConfigApplicationSyncRequest request) {
        logger.info("syncDaConfigApplication,"+ JSON.toJSONString(request));
        ModelMap modelMap = new ModelMap();
        modelMap.put(DATA, syncService.syncApplication(request));
        return modelMap;
    }

    /**
     * 根据globalAppId查询自己和继承它的所有测点组列表
     *
     * @param appId 系统appId
     * @return 测点组DTO列表
     */
    @ApiOperation(value = "Query list of SensorGroup", notes = "Query list of SensorGroup")
    @RequestMapping(value = "/DaConfigGroups", method = {RequestMethod.GET})
    @ResponseBody
    public ModelMap getSensorGroupByGlobalAppId(@RequestParam String appId) {
        ModelMap modelMap = new ModelMap();
        modelMap.put(DATA, syncService.getSensorGroupByGlobalAppId(appId));
        return modelMap;
    }

    /**
     * 根据groupId查询测点列表
     *
     * @param groupId 测点组id
     * @return 测点DTO列表
     */
    @ApiOperation(value = "Query list of SensorGroup", notes = "sync app")
    @RequestMapping(value = "/DaConfigSensors", method = {RequestMethod.GET})
    @ResponseBody
    public ModelMap getSensorByGroupId(@RequestParam Long groupId) {
        ModelMap modelMap = new ModelMap();
        modelMap.put(DATA, syncService.getSensorByGroupId(groupId));
        return modelMap;
    }
}
