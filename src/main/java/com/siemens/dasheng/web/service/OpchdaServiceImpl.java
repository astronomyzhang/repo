package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.CONNECT_FAIL;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.DATA;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.IS_SUCCESS;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.MSG;

/**
 * OpchdaServiceImpl
 *
 * @author xuxin
 * @date 2019/4/3
 */
@Service
public class OpchdaServiceImpl implements ITestConnectorService {
    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
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
        StringBuilder str = new StringBuilder();
        str.append(daConfigConnectorRequest.getConnectorType()).append("#").append(daConfigConnectorRequest.getConnectorClass()).append("#")
                .append(daConfigConnectorRequest.getArchivedDatabase()).append("#").append(daConfigConnectorRequest.getConnectApproach()).append("#")
                .append(null == daConfigConnectorRequest.getServerHost() ? "" :daConfigConnectorRequest.getServerHost()).append("#")
                .append(null == daConfigConnectorRequest.getPort() ? "" : daConfigConnectorRequest.getPort()).append("#");
        return str.toString();
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
