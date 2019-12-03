package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.CONNECT_FAIL;
import static com.siemens.dasheng.web.singleton.constant.LogConstant.CONNECT_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.DATA;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.MSG;

/**
 * DefaultTestConServiceImpl
 *
 * @author xuxin
 * @date 2019/1/3
 */
@Service
public class DefaultTestConServiceImpl implements ITestConnectorService {
    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) {
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, CONNECT_FAIL);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }

    @Override
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {
        return null;
    }


    @Override
    public ModelMap deleteConnectorSensorRelation(Long connectorId) throws Exception {
        return null;
    }

    @Override
    public String structureConnectorInfo(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, String tag) throws Exception {
        return null;
    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, List<String> tag) throws Exception {
        return null;
    }

    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        return null;
    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        return null;
    }

    private Map<String,Object> structureRetMap(boolean flag) {
        Map<String,Object> retMap = new HashMap<String,Object>(4);
        retMap.put("status",flag);
        return retMap;
    }
}
