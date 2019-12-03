package com.siemens.dasheng.web.service;

import com.alibaba.fastjson.JSON;
import com.magus.net.OPConnect;
import com.magus.net.OPDB;
import com.magus.net.OPNode;
import com.magus.net.OPStaticInfo;
import com.magus.net.exception.io.UsersException;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;


/**
 * OpenPlantLinuxTestConServiceImpl
 *
 * @author xuxin
 * @date 2019/1/3
 */
@Service
public class OpenPlantLinuxTestConServiceImpl implements ITestConnectorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String IP = "10.192.30.173";
    public static final int PORT = 8200;
    public static final String USER_NAME = "sis";
    public static final String PASSWORD = "openplant";
    public static String className = "com.magus.jdbc.Driver";
    public static String url = "jdbc:openplant://" + IP + ":" + PORT + "/RTDB";
    public static String user = USER_NAME;
    public static String password = PASSWORD;
    public static final String UNIT_HUIXIE = "UNIT_HUIXIE";
    /**
     * 示例连接的数据库名
     */
    public static final String DB_NAME = "W1";

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private SqlDasLinuxTestConServiceImpl sqlDasLinuxTestConServiceImpl;

    @Autowired
    private DataSensorService dataSensorService;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {

        ModelMap modelMap = new ModelMap();
        if (StringUtils.isBlank(daConfigConnectorRequest.getPort()) || StringUtils.isBlank(daConfigConnectorRequest.getServerHost())) {
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_SERVER_HOST_OR_PORT_IS_BLANK);
        }
        if (StringUtils.isBlank(daConfigConnectorRequest.getUserName()) || StringUtils.isBlank(daConfigConnectorRequest.getPassword())) {
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_USERNAME_OR_PASSWORD_IS_BLANK);
        }

        logger.error("## Openplant linux connect info:{}", JSON.toJSONString(daConfigConnectorRequest));

        OPConnect conn = null;
        try {
            conn = new OPConnect(daConfigConnectorRequest.getServerHost(), Integer.parseInt(daConfigConnectorRequest.getPort()), 20000, daConfigConnectorRequest.getUserName(), daConfigConnectorRequest.getPassword());
            if(!StringUtils.isBlank(daConfigConnectorRequest.getDatabase())){
                OPNode opnode = conn.getNodeByNodeName(daConfigConnectorRequest.getDatabase());
                if (null == opnode) {
                    return getErrorConnectMap(modelMap, Boolean.TRUE, DA_CONFIG_DATABASE_NOT_EXIST);
                }
            }
        } catch (IOException e) {
            logger.error("## Openplant linux connect info:{} ## error msg:{}", JSON.toJSONString(daConfigConnectorRequest), e.getMessage(),e);
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECT_FAIL);
        } catch (UsersException e) {
            logger.error("## Openplant linux connect info:{} ## error msg:{}", JSON.toJSONString(daConfigConnectorRequest), e.getMessage(),e);
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_USERNAME_OR_PASSWORD_IS_WRONG);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    logger.error("close openplant conn error :" + e.getMessage(),e);
                }
            }
        }

        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, CONNECT_SUCCESS);
        modelMap.put(DATA, structureRetMap(true));
        return modelMap;
    }


    @Override
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {
        ModelMap modelMap = new ModelMap();

        long bgTime = System.currentTimeMillis();
        Map<String,String> tagSet = queryTagSet(daConfigConnectorRequest);
        long endTime = System.currentTimeMillis();
        logger.info("Select taglist use time:{}ms, DaConfigConnector infos:{}", endTime - bgTime, JSON.toJSONString(daConfigConnectorRequest));
        if (CollectionUtils.isEmpty(tagSet)) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        }

        //query exist tagList
        DaConfigConnectorSensor cs = new DaConfigConnectorSensor();
        cs.setConnectorId(daConfigConnectorRequest.getId());
        List<DaConfigConnectorSensor> existCsList = daConfigConnectorSensorMapper.select(cs);
        List<String> existTagList = struExistTagList(existCsList);
        if(!CollectionUtils.isEmpty(existTagList)){
            return sqlDasLinuxTestConServiceImpl.updateMockedConnectorSensorRelation(tagSet,existTagList,cs.getConnectorInfo(),modelMap,existCsList, daConfigConnectorRequest.getId(), daConfigConnectorRequest.getApplicationIdList(), sign);
        }

        bgTime = System.currentTimeMillis();
        int ret = sqlDasLinuxTestConServiceImpl.insertMockedConnectorSensorRelation(tagSet, cs.getConnectorInfo(), daConfigConnectorRequest.getId(), sign);
        endTime = System.currentTimeMillis();
        logger.info("Insert snesorMapping use time:{}ms, DaConfigConnector infos:{}", endTime - bgTime, JSON.toJSONString(daConfigConnectorRequest));
        if (ret > 0) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        } else {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            return modelMap;
        }
    }

    private List<String> struExistTagList(List<DaConfigConnectorSensor> existCsList) {
        List<String> tagList = new ArrayList<>();
        for ( DaConfigConnectorSensor cs : existCsList){
            tagList.add(cs.getTag());
        }
        return tagList;
    }


    private Map<String,String> queryTagSet(DaConfigConnectorRequest daConfigConnectorRequest) {
        Map<String,String> retMap = new HashMap<>(16);
        Set<String> tagSet = new HashSet<>();
        OPConnect conn = null;
        try {
            long bgTime1 = System.currentTimeMillis();
            conn = new OPConnect(daConfigConnectorRequest.getServerHost(), Integer.parseInt(daConfigConnectorRequest.getPort()), 6000, daConfigConnectorRequest.getUserName(), daConfigConnectorRequest.getPassword());
            //查询所有的database
            OPDB[] dbs = conn.getDBs();
            //s所用database；
            OPDB db = null;
            if(StringUtils.isBlank(daConfigConnectorRequest.getDatabase())){
                db = dbs[0];
            }else{
                for(OPDB tdb : dbs){
                    if(tdb.getName().equals(daConfigConnectorRequest.getDatabase())){
                        db = tdb;
                        break;
                    }
                }
            }
            if(null == db){
                db = dbs[0];
            }

            Map<String, Integer> pointName2IdDbMap = conn.getPointIdsMap(db.getName());

            if (null != pointName2IdDbMap && pointName2IdDbMap.size() > 0) {
                for (String unitName : pointName2IdDbMap.keySet()) {
                    String key = db.getName() + "." + unitName;
                    Map<String, Integer> pointName2IdUnitMap = conn.getPointIdsMap(key);
                    if (null != pointName2IdUnitMap) {
                        for (Map.Entry<String, Integer> entry : pointName2IdUnitMap.entrySet()) {
                            tagSet.add(db.getName() + "." + unitName + "." + entry.getKey());
                        }
                    }
                }
            }
            long endTime1 = System.currentTimeMillis();
            logger.info("Load PointName2IdMap Use Time:" + (endTime1 - bgTime1) + "ms");

            if(!CollectionUtils.isEmpty(tagSet)){
                for(String tag : tagSet){
                    retMap.put(tag,"");
                }
            }
        } catch (IOException e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
        } catch (Exception e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    logger.error("close openplant conn error : " + e.getMessage(),e);
                }
            }
        }
        return retMap;
    }


    @Override
    public ModelMap deleteConnectorSensorRelation(Long connectorId) throws Exception {
        ModelMap modelMap = new ModelMap();
        daConfigConnectorSensorMapper.deleteByConnectorId(connectorId);
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        return modelMap;
    }

    @Override
    public String structureConnectorInfo(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append(daConfigConnectorRequest.getConnectorType()).append("#").append(daConfigConnectorRequest.getConnectorClass()).append("#")
                .append(daConfigConnectorRequest.getArchivedDatabase()).append("#").append(daConfigConnectorRequest.getConnectApproach()).append("#")
                .append(null == daConfigConnectorRequest.getServerHost() ? "" :daConfigConnectorRequest.getServerHost()).append("#")
                .append(null == daConfigConnectorRequest.getPort() ? "" : daConfigConnectorRequest.getPort()).append("#")
                .append(null == daConfigConnectorRequest.getDatabase() ? "" : daConfigConnectorRequest.getDatabase()).append("#");
        return str.toString();
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        return queryTagSet(daConfigConnectorRequest);
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, String tag) throws Exception {
        Map<String, String> retMap = new HashMap<>(16);
        OPConnect conn = null;
        try {
            conn = new OPConnect(daConfigConnectorRequest.getServerHost(), Integer.parseInt(daConfigConnectorRequest.getPort()), 6000, daConfigConnectorRequest.getUserName(), daConfigConnectorRequest.getPassword());
            OPStaticInfo info = conn.getPointStaticInfo(tag);
            if(null != info){
                retMap.put(tag,"");
            }
        }catch (IOException e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
        } catch (Exception e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    logger.error("close openplant conn error : " + e.getMessage(),e);
                }
            }
        }
        return retMap;
    }

    public static void main(String[] args){
        Map<String, List<String>> retMap = new HashMap<>(16);
        OPConnect conn = null;
        try {

            List<String> tagList = new ArrayList<>();
            /*for(int i =0 ; i<15000 ;i ++){
                tagList.add(i+"");
            }*/
            tagList.add("11131093xxx");
            conn = new OPConnect(IP, PORT, 6000, USER_NAME, PASSWORD);
            //conn.deletePointStaticInfo("W1."+UNIT_HUIXIE+".11131093XXX");

            OPDB[] dbs = conn.getDBs();
            //s所用database；
            OPDB db = null;

            for(OPDB tdb : dbs){
                if(tdb.getName().equals("W1")){
                    db = tdb;
                    break;
                }
            }

            if(null == db){
                db = dbs[0];
            }

            OPNode jiedian;
            try{
                jiedian = conn.getNodeByNodeName(db.getName()+"."+UNIT_HUIXIE);
            }catch(NullPointerException e){
                // 创建节点
                OPNode node = conn.createNode(db.getId(), UNIT_HUIXIE);
                // 将节点插入数据库
                jiedian = conn.insertNode(node);
            }
            // 创建测点

            OPStaticInfo[] infos = new OPStaticInfo[tagList.size()];

            for(int i =0 ; i < tagList.size() ; i++){
                infos[i] = conn.createStaticInfo(jiedian.getID(), tagList.get(i), "R8");
                infos[i].putValue("KZ",2);
            }
            // 将生成的测点数据提交数据库
            OPStaticInfo[] aa = conn.insertPointStaticInfos(infos);

            conn.deletePointStaticInfo("W1."+UNIT_HUIXIE+".11131093XXX");

            String[] tagStr = new String[tagList.size()];
            tagList.toArray(tagStr);
            Map<String,OPStaticInfo> infoss = conn.getPointStaticInfos(tagStr);
            List<String> retList = new ArrayList<>();
            for(Map.Entry<String,OPStaticInfo> entry : infoss.entrySet()){
                if(null != entry.getValue()){
                    retList.add(entry.getKey());
                }
            }


            Map<String, Integer> pointName2IdDbMap = conn.getPointIdsMap(db.getName());
            Set<String> tagSet = new HashSet<>();
            if (null != pointName2IdDbMap && pointName2IdDbMap.size() > 0) {
                for (String unitName : pointName2IdDbMap.keySet()) {
                    String key = db.getName() + "." + unitName;
                    Map<String, Integer> pointName2IdUnitMap = conn.getPointIdsMap(key);
                    if (null != pointName2IdUnitMap) {
                        for (Map.Entry<String, Integer> entry : pointName2IdUnitMap.entrySet()) {
                            tagSet.add(db.getName() + "." + unitName + "." + entry.getKey());
                        }
                    }
                }
            }
            //retMap.put(TAG_LIST,tagSet);
        }catch (IOException e) {
        } catch (Exception e) {
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, List<String> tagList) throws Exception {
        Map<String, List<String>> retMap = new HashMap<>(16);
        OPConnect conn = null;
        try {
            conn = new OPConnect(daConfigConnectorRequest.getServerHost(), Integer.parseInt(daConfigConnectorRequest.getPort()), 6000, daConfigConnectorRequest.getUserName(), daConfigConnectorRequest.getPassword());
            String[] tagStr = new String[tagList.size()];
            tagList.toArray(tagStr);
            Map<String,OPStaticInfo> infos = conn.getPointStaticInfos(tagStr);
            List<String> retList = new ArrayList<>();
            for(Map.Entry<String,OPStaticInfo> entry : infos.entrySet()){
                if(null != entry.getValue()){
                    retList.add(entry.getKey());
                }
            }
            retMap.put(TAG_LIST,retList);
            return retMap;
        }catch (IOException e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
            throw new Exception(e);
        } catch (Exception e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
            throw new Exception(e);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    logger.error("close openplant conn error : " + e.getMessage(),e);
                    throw new Exception(e);
                }
            }
        }
    }

    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorRequest, HashSet<String> tagSet) throws Exception {
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        OPConnect conn = null;
        try {
            conn = new OPConnect(daConfigConnectorRequest.getServerHost(), Integer.parseInt(daConfigConnectorRequest.getPort()), 6000, daConfigConnectorRequest.getUserName(), daConfigConnectorRequest.getPassword());
            if(!StringUtils.isBlank(daConfigConnectorRequest.getDatabase())){
                OPNode opnode = conn.getNodeByNodeName(daConfigConnectorRequest.getDatabase());
                if (null == opnode) {
                    modelMap.put(IS_SUCCESS, Boolean.FALSE);
                    return modelMap;
                }
            }
            //查询所有的database
            OPDB[] dbs = conn.getDBs();
            OPNode jiedian;
            //s所用database；
            OPDB db = null;
            if(StringUtils.isBlank(daConfigConnectorRequest.getDatabase())){
                db = dbs[0];
            }else{
                for(OPDB tdb : dbs){
                    if(tdb.getName().equals(daConfigConnectorRequest.getDatabase())){
                        db = tdb;
                        break;
                    }
                }
            }
            try{
                jiedian = conn.getNodeByNodeName(db.getName()+"."+UNIT_HUIXIE);
            }catch(NullPointerException e){
                // 创建节点
                OPNode node = conn.createNode(db.getId(), UNIT_HUIXIE);
                // 将节点插入数据库
                jiedian = conn.insertNode(node);
            }


            // 创建测点
            List<String> tagList = new ArrayList<>(tagSet);

            OPStaticInfo[] infos = new OPStaticInfo[tagList.size()];

            for(int i =0 ; i < tagList.size() ; i++){
                infos[i] = conn.createStaticInfo(jiedian.getID(), tagList.get(i), "R8");
                infos[i].putValue("KZ",2);
            }
            // 将生成的测点数据提交数据库
            conn.insertPointStaticInfos(infos);
        }catch (IOException e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG,e.getMessage());
        } catch (Exception e) {
            logger.error("openplant queryTagSet error : " + e.getMessage(),e);
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            modelMap.put(MSG,e.getMessage());
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (IOException e) {
                    logger.error("close openplant conn error : " + e.getMessage(),e);
                }
            }
        }

        return modelMap;
    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        insertTagData(daConfigConnectorReques,tagSet);
        String prefix = dataSensorService.struOpTag(daConfigConnectorReques.getId());
        List<String> openplanTagList = new ArrayList<>();
        tagSet.forEach(a->openplanTagList.add(prefix + a.toUpperCase()));
        Map<String, List<String>> existTagMap = batchSelectTagListExact(daConfigConnectorReques,openplanTagList);
        ModelMap modelMap = new ModelMap();
        modelMap.put(SUCCESS,true);
        modelMap.put(DATA,existTagMap.get(TAG_LIST));
        return modelMap;
    }


    private ModelMap getErrorConnectMap(ModelMap modelMap, Boolean b, String msg) {
        modelMap.put(IS_SUCCESS, b);
        modelMap.put(MSG, msg);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }

    private Map<String, Object> structureRetMap(boolean flag) {
        Map<String, Object> retMap = new HashMap<String, Object>(4);
        retMap.put("status", flag);
        return retMap;
    }
}
