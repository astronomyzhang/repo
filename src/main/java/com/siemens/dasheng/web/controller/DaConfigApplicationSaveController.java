package com.siemens.dasheng.web.controller;

import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.exception.BusinessException;
import com.siemens.dasheng.web.model.Users;
import com.siemens.dasheng.web.request.AppFilter;
import com.siemens.dasheng.web.request.DaConfigApplicationRequest;
import com.siemens.dasheng.web.request.InheritAppFilter;
import com.siemens.dasheng.web.request.PublicAppFilter;
import com.siemens.dasheng.web.service.DaConfigApplicationSaveService;
import com.siemens.dasheng.web.singleton.constant.CommonErrorConstant;
import com.siemens.dasheng.web.util.NumberUtil;
import com.siemens.ofmcommon.log.anotation.LogEventName;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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

import static com.siemens.dasheng.web.singleton.constant.ControllerConstant.USER_SESSION;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * @author allan
 * Created by ofm on 2019/4/3.
 */
@Api(value = "config app save", description = "config app save")
@Controller
@RequestMapping("/config/application")
public class DaConfigApplicationSaveController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DaConfigApplicationSaveService daConfigApplicationSaveService;

    @ApiOperation(value = "select app list", notes = "select app list")
    @RequestMapping(value = "/selectAppList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType="selectAppList",businessDescription="select app list", webUrl = "/config/application/selectAppList")
    public ModelMap selectAppList(@RequestBody AppFilter appFilter, HttpServletRequest httpServletRequest) {
        logger.info("selectAppList,"+ JSON.toJSONString(appFilter));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            if (appFilter == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap =  filter(modelMap, appFilter);
            if (!(Boolean)modelMap.get(IS_SUCCESS)) {
                return modelMap;
            }

            modelMap.put(DATA,  daConfigApplicationSaveService.selectAppList(appFilter, true));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
            return modelMap;
        } catch (BusinessException be) {
            logger.error("selectAppList errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        } catch (Exception e) {
            logger.error("selectAppList errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "select public app list", notes = "select public app list")
    @RequestMapping(value = "/selectPublicAppList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType="selectPublicAppList",businessDescription="select public app list", webUrl = "/config/application/selectPublicAppList")
    public ModelMap selectPublicAppList(@RequestBody PublicAppFilter publicAppFilter,  HttpServletRequest httpServletRequest) {
        logger.info("selectPublicAppList,"+ JSON.toJSONString(publicAppFilter));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            if (publicAppFilter == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap.put(DATA,  daConfigApplicationSaveService.selectPublicAppList(publicAppFilter.getId()));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
            return modelMap;
        }  catch (BusinessException be) {
            logger.error("selectPublicAppList errors:{}", be.getMessage(),be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        } catch (Exception e) {
            logger.error("selectPublicAppList errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    @ApiOperation(value = "registerApp", notes = "registerApp")
    @RequestMapping(value = "/registerApp", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType="registerApp",businessDescription="registerApp", webUrl = "/config/application/registerApp")
    public ModelMap registerApp(@RequestBody DaConfigApplicationRequest daConfigApplicationRequest, HttpServletRequest httpServletRequest) {
        logger.info("registerApp,"+ JSON.toJSONString(daConfigApplicationRequest));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            Users loginUser = (Users) httpServletRequest.getSession().getAttribute(USER_SESSION);
            if (null == loginUser) {
                modelMap.put(MSG, INVALID_USER_SESSION);
                modelMap.put(IS_SUCCESS, false);
                return modelMap;
            }
            if (daConfigApplicationRequest == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }
            modelMap.put(MSG, OPERATION_SUCCESS);
            modelMap =  daConfigApplicationSaveService.registerApp(daConfigApplicationRequest, modelMap, loginUser.getUsername());
            return modelMap;
        }  catch (BusinessException be) {
            logger.error("registerApp errors:{}", be.getMessage(), be);
            be.printStackTrace();
            modelMap.put(MSG, be.getCode());
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }  catch (Exception e) {
            logger.error("registerApp errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    /**
     * 校验参数
     * @param modelMap
     * @return
     */
    private ModelMap filter(ModelMap modelMap, AppFilter appFilter) {
        if (appFilter != null) {
            String pageStr = appFilter.getPage();
            String pageSizeStr= appFilter.getPageSize();
            //判断是否是数字
            if (!StringUtils.isEmpty(pageStr) && !NumberUtil.isInteger(pageStr)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
            if (!StringUtils.isEmpty(pageSizeStr) && !NumberUtil.isInteger(pageSizeStr)) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                modelMap.put(MSG, CommonErrorConstant.PARAMETER_IS_ILLEGAL);
                return modelMap;
            }
        }
        return modelMap;
    }


    @ApiOperation(value = "inherit app list", notes = "inherit app list")
    @RequestMapping(value = "/inheritAppList", method = {RequestMethod.POST})
    @ResponseBody
    @LogEventName(operateType="inheritAppList",businessDescription="inherit app list", webUrl = "/config/application/inheritAppList")
    public ModelMap inheritAppList(@RequestBody InheritAppFilter inheritAppFilter, HttpServletRequest httpServletRequest) {
        logger.info("inheritAppList,"+ JSON.toJSONString(inheritAppFilter));
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        try {
            if (inheritAppFilter == null) {
                modelMap.put(IS_SUCCESS, Boolean.FALSE);
                return modelMap;
            }

            modelMap.put(DATA,  daConfigApplicationSaveService.inheritAppList(inheritAppFilter.getAppId()));
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            modelMap.put(MSG, OPERATION_SUCCESS);
            return modelMap;
        } catch (Exception e) {
            logger.error("inheritAppList errors:{}", e.getMessage(),e);
            e.printStackTrace();
            modelMap.put(MSG, OPERATE_FAIL);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

}
