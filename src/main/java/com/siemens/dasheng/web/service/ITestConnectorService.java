package com.siemens.dasheng.web.service;

import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author xuxin
 * DataProviderService
 * created by xuxin on 3/1/2018
 */
@Service
public interface ITestConnectorService {
    /**
     * testConnector
     * @param daConfigConnectorRequest
     * @return
     * @throws Exception
     */
    ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception ;

    /**
     * addOrUpdateConnectorSensorRelation
     * @param daConfigConnectorRequest
     * @param sign
     * @return
     * @throws Exception
     */
    ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest,int sign) throws Exception;

    /**
     * deleteConnectorSensorRelation
     * @param connectorId
     * @return
     * @throws Exception
     */
    ModelMap deleteConnectorSensorRelation(Long connectorId) throws Exception;

    /**
     * structureConnectorInfo
     * @param daConfigConnectorRequest
     * @return
     * @throws Exception
     */
    String structureConnectorInfo(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception;

    /**
     * selectTagList
     * @param daConfigConnectorReques
     * @return key:tag; value:unit;
     * @throws Exception
     */
    Map<String,String> selectTagList(DaConfigConnectorRequest daConfigConnectorReques) throws Exception;


    /**
     * selectTagListExact
     * @param daConfigConnectorReques
     * @param tag
     * @return key:tag; value:unit;
     * @throws Exception
     */
    Map<String,String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, String tag) throws Exception;

    /**
     * batchSelectTagListExact
     * @param daConfigConnectorReques
     * @param tag
     * @return
     * @throws Exception
     */
    Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, List<String> tag) throws Exception;


    /**
     * nsert tag data to time series db
     * @param daConfigConnectorReques
     * @param tagSet
     * @return
     * @throws Exception
     */
    ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception;

    /**
     * batchInsertTagData
     * @param daConfigConnectorReques
     * @param tagSet
     * @return
     * @throws Exception
     */
    ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception;

}
