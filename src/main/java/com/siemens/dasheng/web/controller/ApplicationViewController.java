package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.response.AppDetail;
import com.siemens.dasheng.web.service.ApplicationViewService;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.APP_IS_REMOVED;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.OPERATION_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;


/**
 * @author liming
 * @Date: 2019/4/8 10:35
 */
@Api(value = "config app save", description = "config app save")
@Controller
@RequestMapping("/applicationView")
public class ApplicationViewController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApplicationViewService applicationViewService;


    @ApiOperation(value = "select app list", notes = "select app list")
    @RequestMapping(value = "/selectAppList", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "APPLICATIONVIEW", businessDescription = "select app list", webUrl = "/applicationView/selectAppList")
    public ModelMap selectAppList() {
        logger.info("selectAppList");
        ModelMap modelMap = new ModelMap();
        modelMap.put(DATA, applicationViewService.selectAppList());
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, OPERATION_SUCCESS);
        return modelMap;
    }

    @ApiOperation(value = "select app detail", notes = "select app detail")
    @RequestMapping(value = "/selectAppDetail/{id}", method = {RequestMethod.GET})
    @ResponseBody
    @LogEventName(operateType = "APPLICATIONVIEW", businessDescription = "select app detail", webUrl = "/applicationView/selectAppDetail/{id}")
    public ModelMap selectAppDetail(@PathVariable Long id) {
        logger.info("selectAppDetail");
        ModelMap modelMap = new ModelMap();
        AppDetail appDetail = applicationViewService.selectAppDetail(id);
        if (null != appDetail) {
            modelMap.put(DATA, applicationViewService.selectAppDetail(id));
            modelMap.put(IS_SUCCESS, true);
            modelMap.put(MSG, OPERATION_SUCCESS);
        } else {
            modelMap.put(IS_SUCCESS, false);
            modelMap.put(MSG, APP_IS_REMOVED);
        }
        return modelMap;
    }

}
