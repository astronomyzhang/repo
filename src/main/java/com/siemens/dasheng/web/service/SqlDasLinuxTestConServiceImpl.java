package com.siemens.dasheng.web.service;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.siemens.dasheng.web.enums.ConnDatabaseType;
import com.siemens.dasheng.web.enums.DelStatus;
import com.siemens.dasheng.web.mapper.DaConfigConnectorSensorMapper;
import com.siemens.dasheng.web.mapper.DaConfigSensorMapper;
import com.siemens.dasheng.web.mapperfactory.DaConfigConnectorSensorMapperCommon;
import com.siemens.dasheng.web.model.DaConfigConnectorSensor;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.singleton.SieExecutorsPool;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;
import static com.siemens.dasheng.web.singleton.constant.ModelConstant.*;

/**
 * SqlDasLinuxTestConServiceImpl
 *
 * @author xuxin
 * @date 2019/1/3
 */
@Service
@SuppressWarnings({"all"})
public class SqlDasLinuxTestConServiceImpl implements ITestConnectorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PI_SDK_MSG = "[PI SDK] The requested server was not found in the known servers table";

    private static final int BATCH_SIZE = 3000;

    private static final int BATCH_SENSOR_SIZE = 3000;

    private static final String MAXIMUM_LICENSED_POINT = "Maximum";
    private static final String PI_CLIENT_NO_AUTH = "Access";
    private static final String PI_CLIENT_TIMEOUT = "Timeout";

    @Value("${pi.protocol}")
    private String protocol;

    @Value("${pi.url.suffix}")
    private String urlSuffix;

    @Autowired
    private DaConfigConnectorSensorMapper daConfigConnectorSensorMapper;

    @Autowired
    private DaConfigConnectorSensorMapperCommon daConfigConnectorSensorMapperCommon;

    @Autowired
    private DaConfigSensorMapper daConfigSensorMapper;

    /**
     * 执行测试连接的任务数目
     */
    private AtomicInteger taskCount;

    private void reduceTaskCount() {
        taskCount.getAndDecrement();
    }

    private AtomicInteger getTaskCount() {
        return taskCount;
    }

    @Override
    public ModelMap testConnector(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        ModelMap modelMap = new ModelMap();

        if (StringUtils.isBlank(daConfigConnectorRequest.getSqldas()) || StringUtils.isBlank(daConfigConnectorRequest.getServerHost())) {
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_SERVER_HOST_OR_SQLDAS_IS_BLANK);
        }

        // do slqdas telnet 5461 or 5462 test
        TelnetClient telnetClient = new TelnetClient("vt200");
        telnetClient.setDefaultTimeout(3000);
        telnetClient.setConnectTimeout(3000);
        try {
            telnetClient.connect(daConfigConnectorRequest.getSqldas(), 5461);
        } catch (IOException e) {
            logger.error("Telnet SqlDas Server Error,{}", JSON.toJSONString(daConfigConnectorRequest),e);
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_SQLDAS_IS_WRONG_OR_PI_SERVER_CLOSE);
        } finally {
            if (telnetClient.isConnected()) {
                telnetClient.disconnect();
            }
        }

        if (StringUtils.isBlank(daConfigConnectorRequest.getUserName()) || StringUtils.isBlank(daConfigConnectorRequest.getPassword())) {
            return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_USERNAME_OR_PASSWORD_IS_BLANK);
        }

        String url = protocol + daConfigConnectorRequest.getSqldas() + "/Data Source=" + daConfigConnectorRequest.getServerHost() + urlSuffix;
        Class.forName("com.osisoft.jdbc.Driver");
        DriverManager.setLoginTimeout(3);

        synchronized (SqlDasLinuxTestConServiceImpl.class) {
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(url, daConfigConnectorRequest.getUserName(), daConfigConnectorRequest.getPassword());
                if (null != connection) {
                    modelMap.put(IS_SUCCESS, true);
                    modelMap.put(MSG, CONNECT_SUCCESS);
                    modelMap.put(DATA, structureRetMap(true));
                    return modelMap;
                }
            } catch (SQLException e) {
                logger.error("## Sql das linux connect info:{} ## error msg:{}", JSON.toJSONString(daConfigConnectorRequest), e.getMessage(), e);
                if (e.getMessage().contains("Maximum number of open connections was reached")) {
                    return getErrorConnectMap(modelMap, Boolean.TRUE, MAX_NUM_OF_CON_REACHED);
                } else if (!e.getMessage().contains(PI_SDK_MSG)) {
                    return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_USERNAME_OR_PASSWORD_IS_WRONG);
                } else {
                    return getErrorConnectMap(modelMap, Boolean.TRUE, CONNECTOR_SERVER_HOST_IS_WRONG);
                }
            } finally {
                try {
                    if (null != connection && !connection.isClosed()) {
                        logger.info("Close PI Jdbc Connect!");
                        connection.close();
                    }
                } catch (SQLException e) {
                    logger.error("Close PI Jdbc Connect error:" + e.getMessage(), e);
                }
            }
        }
        return getErrorConnectMap(modelMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ModelMap addOrUpdateConnectorSensorRelation(DaConfigConnectorRequest daConfigConnectorRequest, int sign) throws Exception {

        ModelMap modelMap = new ModelMap();

        long bgTime = System.currentTimeMillis();
        Map<String, String> tagMap = selectPiTagListByPrefix("", daConfigConnectorRequest, true);
        long endTime = System.currentTimeMillis();
        logger.info("Select taglist use time:{}ms, DaConfigConnector infos:{}", endTime - bgTime, JSON.toJSONString(daConfigConnectorRequest));
        if (CollectionUtils.isEmpty(tagMap)) {
            modelMap.put(IS_SUCCESS, Boolean.TRUE);
            return modelMap;
        }
        //query exist tagList
        DaConfigConnectorSensor cs = new DaConfigConnectorSensor();
        cs.setConnectorId(daConfigConnectorRequest.getId());
        List<DaConfigConnectorSensor> existCsList = daConfigConnectorSensorMapper.select(cs);
        List<String> existTagList = struExistTagList(existCsList);
        if (!CollectionUtils.isEmpty(existTagList)) {
            return updateMockedConnectorSensorRelation(tagMap, existTagList, cs.getConnectorInfo(), modelMap, existCsList, daConfigConnectorRequest.getId(), daConfigConnectorRequest.getApplicationIdList(), sign);
        }

        bgTime = System.currentTimeMillis();
        int ret = insertMockedConnectorSensorRelation(tagMap, cs.getConnectorInfo(), daConfigConnectorRequest.getId(), sign);
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

    public ModelMap updateMockedConnectorSensorRelation(Map<String, String> tagMap, List<String> existTagList, String connectorInfo, ModelMap modelMap, List<DaConfigConnectorSensor> existCsList, Long connectorId, List<Long> applicationIds, int sign) {
        long startTime = System.currentTimeMillis();
        Map<String, String> insertTagMap = new HashMap<>(16);
        Map<String, String> existMap = new HashMap<>(16);
        for (String tag : existTagList) {
            existMap.put(tag, tag);
        }

        //List<String> tempTagList = new ArrayList<>();
        if (null != tagMap && tagMap.size() > 0) {
            for (Map.Entry<String, String> tagmap : tagMap.entrySet()) {
                if (!existMap.containsKey(tagmap.getKey())) {
                    insertTagMap.put(tagmap.getKey(), tagmap.getValue());
                }

            }
        }

        //更新已存在的状态
        List<String> tempTagList = new ArrayList<>();
        for (DaConfigConnectorSensor cs : existCsList) {
            if (tagMap.containsKey(cs.getTag()) && cs.getStatus().equals(DelStatus.DELETE.getType())) {
                tempTagList.add(cs.getTag());
            }
        }
        if (!CollectionUtils.isEmpty(tempTagList)) {
            updateStatusToExistByCondition(tempTagList, connectorId);
            //更新sensor为已导入
            daConfigSensorMapper.updateStatusToImportedByCondition(tempTagList, connectorId);
        }

        //插入新加的
        if (null != insertTagMap && insertTagMap.size() > 0) {
            int retInsert = insertMockedConnectorSensorRelation(insertTagMap, connectorInfo, connectorId, sign);
            if (retInsert < 1) {
                throw new RuntimeException("insert Tag List Exception");
            }
        }


        //删除不存在的
        Set<String> delTagList = new HashSet<>();
        for (DaConfigConnectorSensor cs : existCsList) {
            if (!tagMap.containsKey(cs.getTag())) {
                delTagList.add(cs.getTag());
            }
        }
        if (!CollectionUtils.isEmpty(delTagList)) {

            //删除未导入的taglist
            deleteUnImportTagByTagList(connectorId, delTagList);
            //update con_sensor status
            updateStatusToDelByCondition(delTagList, connectorId);

            //update sensor Status
            updateStatusToDelByCondition(delTagList, applicationIds, connectorId);

        }
        long endTime = System.currentTimeMillis();
        logger.info("updateMockedConnectorSensorRelation use time" + (endTime - startTime) + "ms");
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        return modelMap;
    }

    private int updateStatusToDelByCondition(Set<String> delTagList, List<Long> applicationIds, Long connectorId) {
        Set<String> tempList = new HashSet<>();
        int updateSize = BATCH_SENSOR_SIZE;
        int updateNum = 0;
        int currentIndex = 0;

        for (String tag : delTagList) {
            if (currentIndex == updateSize) {
                if (tempList.size() > 0) {
                    logger.info("-----------------" + JSONUtils.toJSONString(tempList));
                    updateNum += daConfigSensorMapper.updateStatusToDelByCondition(tempList, applicationIds, connectorId);
                    currentIndex = 0;
                    tempList = new HashSet<>();
                }
            }
            tempList.add(tag);
            currentIndex++;
        }

        if (tempList.size() > 0) {
            updateNum += daConfigSensorMapper.updateStatusToDelByCondition(tempList, applicationIds, connectorId);
        }
        return updateNum;

    }

    private int updateStatusToDelByCondition(Set<String> delTagList, Long connectorId) {
        Set<String> tempList = new HashSet<>();
        int updateSize = BATCH_SIZE;
        int updateNum = 0;
        int currentIndex = 0;

        for (String tag : delTagList) {
            if (currentIndex == updateSize) {
                if (tempList.size() > 0) {
                    updateNum += daConfigConnectorSensorMapper.updateStatusToDelByCondition(tempList, connectorId);
                    currentIndex = 0;
                    tempList = new HashSet<>();
                }
            }
            tempList.add(tag);
            currentIndex++;
        }

        if (tempList.size() > 0) {
            updateNum += daConfigConnectorSensorMapper.updateStatusToDelByCondition(tempList, connectorId);
        }
        return updateNum;

    }

    private int deleteUnImportTagByTagList(Long connectorId, Set<String> delTagList) {
        Set<String> tempList = new HashSet<>();
        int updateSize = BATCH_SIZE;
        int updateNum = 0;
        int currentIndex = 0;

        for (String tag : delTagList) {
            if (currentIndex == updateSize) {
                if (tempList.size() > 0) {
                    updateNum += daConfigConnectorSensorMapper.deleteUnImportTagByTagList(connectorId, tempList);
                    currentIndex = 0;
                    tempList = new HashSet<>();
                }
            }
            tempList.add(tag);
            currentIndex++;
        }

        if (tempList.size() > 0) {
            updateNum += daConfigConnectorSensorMapper.deleteUnImportTagByTagList(connectorId, tempList);
        }
        return updateNum;

    }

    private int updateStatusToExistByCondition(List<String> tempTagList, Long connectorId) {
        List<String> tempList = new ArrayList<>();
        int updateSize = BATCH_SIZE;
        int updateNum = 0;
        int currentIndex = 0;

        for (String tag : tempTagList) {
            if (currentIndex == updateSize) {
                if (tempList.size() > 0) {
                    updateNum += daConfigConnectorSensorMapper.updateStatusToExistByCondition(tempList, connectorId);
                    currentIndex = 0;
                    tempList = new ArrayList<>();
                }
            }
            tempList.add(tag);
            currentIndex++;
        }

        if (tempList.size() > 0) {
            updateNum += daConfigConnectorSensorMapper.updateStatusToExistByCondition(tempList, connectorId);
        }
        return updateNum;

    }


    private List<String> struExistTagList(List<DaConfigConnectorSensor> existCsList) {
        List<String> tagList = new ArrayList<>();
        for (DaConfigConnectorSensor cs : existCsList) {
            tagList.add(cs.getTag());
        }
        return tagList;
    }

    /**
     * Insert connector sensor relation
     *
     * @param tagMap
     * @param connectorInfo
     * @param connectorId
     * @param sign
     * @return
     */
    public int insertMockedConnectorSensorRelation(Map<String, String> tagMap, String connectorInfo, Long connectorId, int sign) {

        if (null != tagMap && tagMap.size() > 0) {

            List<DaConfigConnectorSensor> retList = new ArrayList<>();

            // set batch insert size 4000 (8000*4<32767)
            int batchSize = BATCH_SIZE;
            int insertNum = 1;
            int currentIndex = 0;

            taskCount = new AtomicInteger(tagMap.size() / BATCH_SIZE);

            for (Map.Entry<String, String> tagmap : tagMap.entrySet()) {
                if (StringUtils.isNotEmpty(tagmap.getKey())) {
                    if (currentIndex == batchSize) {
                        if (retList.size() > 0) {
                            /*SomeThread oneThread = new SomeThread(retList);
                            oneThread.start();*/
                            SieExecutorsPool.executor(new Task(retList));
                            currentIndex = 0;
                            retList = new ArrayList<>();
                        }
                    }
                    DaConfigConnectorSensor conSensor = new DaConfigConnectorSensor();
                    conSensor.setConnectorInfo(connectorInfo);
                    conSensor.setTag(tagmap.getKey());
                    conSensor.setStatus(DelStatus.UNIMPORT.getType());
                    conSensor.setUnit(tagmap.getValue());
                    conSensor.setConnectorId(connectorId);
                    if (sign == ConnDatabaseType.OPCUA.getType()) {
                        String[] vals = tagmap.getValue().split("#,");
                        conSensor.setDaPrefix(vals[0]);
                        conSensor.setHdaPrefix(vals[1]);
                        conSensor.setUnit((StringUtils.isEmpty(vals[2]) || vals[2].equals("null")) ? null : vals[2]);

                    }
                    retList.add(conSensor);

                    currentIndex++;

                }
            }

            // insert last records
            if (retList.size() > 0) {
                insertNum += daConfigConnectorSensorMapperCommon.insertList(retList);
            }
            while (true) {
                if (getTaskCount().intValue() == 0) {
                    break;
                }
            }

            return insertNum;

        }

        return 0;

    }


    /*public class SomeThread extends Thread   {
        List<DaConfigConnectorSensor> retList;
        SomeThread(List<DaConfigConnectorSensor> retList){
            this.retList = retList;
        }
        public void run()   {
            daConfigConnectorSensorMapperCommon.insertList(retList);
        }
    }*/

    public class Task implements Runnable {
        List<DaConfigConnectorSensor> tagList;

        public Task(List<DaConfigConnectorSensor> tagList) {
            this.tagList = tagList;
        }

        @Override
        public void run() {
            daConfigConnectorSensorMapperCommon.insertList(tagList);
            reduceTaskCount();
        }
    }


    @Override
    public ModelMap deleteConnectorSensorRelation(Long connectorId) throws Exception {
        ModelMap modelMap = new ModelMap();
        deleteConnectorSensorByConnectorId(connectorId);
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        return modelMap;
    }

    @Override
    public String structureConnectorInfo(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append(daConfigConnectorRequest.getConnectorType()).append("#").append(daConfigConnectorRequest.getConnectorClass()).append("#")
                .append(daConfigConnectorRequest.getArchivedDatabase()).append("#").append(daConfigConnectorRequest.getConnectApproach()).append("#")
                .append(null == daConfigConnectorRequest.getServerHost() ? "" : daConfigConnectorRequest.getServerHost()).append("#")
                .append(null == daConfigConnectorRequest.getSqldas() ? "" : daConfigConnectorRequest.getSqldas()).append("#");
        return str.toString();
    }

    @Override
    public Map<String, String> selectTagList(DaConfigConnectorRequest daConfigConnectorRequest) throws Exception {
        return selectPiTagListByPrefix("", daConfigConnectorRequest, true);
    }

    @Override
    public Map<String, String> selectTagListExact(DaConfigConnectorRequest daConfigConnectorRequest, String tag) throws Exception {
        return selectPiTagListByPrefix(tag, daConfigConnectorRequest, false);
    }

    public List<String> queryExistSqldasSensor(List<String> sensorNameSet, Connection conn) throws Exception {

        List<String> retTagList = new ArrayList<>();
        if (null != sensorNameSet && sensorNameSet.size() > 0) {

            int batchSize = 3000;
            int insertNum = 1;
            int currentIndex = 0;
            List<String> sensorList = new ArrayList<>();

            PreparedStatement statement = null;


            for (String sensor : sensorNameSet) {
                if (currentIndex == batchSize) {

                    statement = conn.prepareStatement("SELECT tag,engunits FROM pipoint..pipoint WHERE tag in  ('" + sensorList.stream().collect(Collectors.joining("','")) + "')");
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        String tag = resultSet.getString("tag");
                        retTagList.add(tag);
                    }
                    currentIndex = 0;
                    statement = null;
                    sensorList = new ArrayList<>();
                }
                sensorList.add(sensor);
                currentIndex++;

            }

            if(!CollectionUtils.isEmpty(sensorList)){
                statement = conn.prepareStatement("SELECT tag,engunits FROM pipoint..pipoint WHERE tag in  ('" + sensorList.stream().collect(Collectors.joining("','")) + "')");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String tag = resultSet.getString("tag");
                    retTagList.add(tag);
                }
            }

            return retTagList;

        }

        return retTagList;

    }

    @Override
    public Map<String, List<String>> batchSelectTagListExact(DaConfigConnectorRequest daConfigConnectorReques, List<String> tagList) throws Exception {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Map<String, String> tagUnitMap = new HashMap<>(8);

            conn = this.makeConnection(daConfigConnectorReques);
            if (null == conn) {
                return null;
            }

            List<String> retTagList = queryExistSqldasSensor(tagList,conn);

            Map<String, List<String>> retMap = new HashMap<>();
            retMap.put(TAG_LIST, retTagList);
            return retMap;
        } finally {
            closeDbResources(conn, statement, resultSet);
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            DaConfigConnectorRequest daConfigConnectorReques = new DaConfigConnectorRequest();
            daConfigConnectorReques.setServerHost("10.192.30.135");
            daConfigConnectorReques.setSqldas("10.192.30.135");
            daConfigConnectorReques.setUserName("siemens-pdt");
            daConfigConnectorReques.setPassword("12345678");
            try {

                // do slqdas telnet 5461 or 5462 test
                TelnetClient telnetClient = new TelnetClient("vt200");
                telnetClient.setDefaultTimeout(3000);
                telnetClient.setConnectTimeout(3000);
                try {
                    telnetClient.connect(daConfigConnectorReques.getSqldas(), 5461);
                } catch (IOException e) {
                }

                String url = "jdbc:pisql://" + daConfigConnectorReques.getSqldas() + "/Data Source=" + daConfigConnectorReques.getServerHost() + ";Integrated Security=SSPI";
                Class.forName("com.osisoft.jdbc.Driver");
                DriverManager.setLoginTimeout(3);

                conn = DriverManager.getConnection(url, daConfigConnectorReques.getUserName(), daConfigConnectorReques.getPassword());
            } catch (RuntimeException e) {
            } catch (Exception e) {
            }
            List<String> sensorNameSet = new ArrayList<>();
            sensorNameSet.add("12314214124214");
            /*sensorNameSet.add("12314214124215");
            sensorNameSet.add("12314214124216");*/


            statement = conn.prepareStatement("SELECT tag,engunits FROM pipoint..pipoint WHERE tag in  ('" + sensorNameSet.stream().collect(Collectors.joining("','")) + "')");

            resultSet = statement.executeQuery();
            List<String> tagList = new ArrayList<>();
            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                tagList.add(tag);
            }


            //insertSqldasSensor(new HashSet<>(sensorNameSet),conn);
            //logger.info("Insert pi tags success: Tag List: {}", JSON.toJSONString(sensorNameSet));

        } catch (Exception e) {
            System.out.println(e);

        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public ModelMap insertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> sensorNameSet) throws Exception {
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);

        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            conn = this.makeConnection(daConfigConnectorReques);
            insertSqldasSensor(sensorNameSet, conn);
            logger.info("Insert pi tags success: Tag List: {}", JSON.toJSONString(sensorNameSet));

        } catch (Exception e) {
            modelMap.put(IS_SUCCESS, Boolean.FALSE);
            if (e.getMessage().contains(MAXIMUM_LICENSED_POINT)) {
                modelMap.put(MSG, PI_MAX_LICENCE_LIMIT);
            } else if (e.getMessage().contains(PI_CLIENT_NO_AUTH)) {
                modelMap.put(MSG, PI_CLIENT_NO_AUTH_MSG);
            } else if (e.getMessage().contains(PI_CLIENT_TIMEOUT)) {
                modelMap.put(MSG, PI_CONNECT_TIMEOUT_MSG);
            } else {
                modelMap.put(MSG, PI_UNKNOWN_MSG_ERROR);
            }
            logger.error("Insert pi tags fail: Tag List: {},Error Message: {}", JSON.toJSONString(sensorNameSet), e.getMessage(), e);
        } finally {
            this.closeDbResources(conn, statement, resultSet);
        }
        return modelMap;
    }


    public ModelMap insertTagDataForBatch(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> sensorNameSet) throws Exception {
        ModelMap modelMap = new ModelMap();
        modelMap.put(IS_SUCCESS, Boolean.TRUE);
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        conn = this.makeConnection(daConfigConnectorReques);
        insertSqldasSensor(sensorNameSet, conn);
        logger.info("Insert pi tags success: Tag List: {}", JSON.toJSONString(sensorNameSet));
        this.closeDbResources(conn, statement, resultSet);
        return modelMap;
    }

    public int insertSqldasSensor(HashSet<String> sensorNameSet, Connection conn) throws Exception {

        if (null != sensorNameSet && sensorNameSet.size() > 0) {

            List<DaConfigConnectorSensor> retList = new ArrayList<>();

            int batchSize = 5000;
            int insertNum = 1;
            int currentIndex = 0;

            PreparedStatement statement = null;
            statement = conn.prepareStatement("INSERT pipoint..classic (tag,pointtypex,compressing,excdev,excdevpercent,excmax) " +
                    "VALUES (?,?,?,?,?,?)");

            for (String sensor : sensorNameSet) {
                if (currentIndex == batchSize) {
                    int[] insertResult = statement.executeBatch();
                    conn.commit();
                    currentIndex = 0;
                    statement = null;
                    statement = conn.prepareStatement("INSERT pipoint..classic (tag,pointtypex,compressing,excdev,excdevpercent,excmax) " +
                            "VALUES (?,?,?,?,?,?)");
                }
                statement.setString(1, sensor);
                statement.setString(2, "float32");
                statement.setInt(3, 0);
                statement.setFloat(4, 0L);
                statement.setFloat(5, 0L);
                statement.setInt(6, 0);
                statement.addBatch();
                currentIndex++;

            }

            // insert last records
            int[] insertResult = statement.executeBatch();
            conn.commit();

            return sensorNameSet.size();

        }

        return 0;

    }

    @Override
    public ModelMap batchInsertTagData(DaConfigConnectorRequest daConfigConnectorReques, HashSet<String> tagSet) throws Exception {
        ModelMap modelMap = new ModelMap();
        insertTagDataForBatch(daConfigConnectorReques, tagSet);

        List<String> tagList = new ArrayList<>(tagSet);
        Map<String, List<String>> existTagMap = batchSelectTagListExact(daConfigConnectorReques,tagList);
        modelMap.put(SUCCESS,true);
        modelMap.put(DATA,existTagMap.get(TAG_LIST));
        return modelMap;

    }

    private void deleteConnectorSensorByConnectorId(Long connectorId) {
        daConfigConnectorSensorMapper.deleteByConnectorId(connectorId);
    }

    private Map<String, String> selectPiTagListByPrefix(String prefix, DaConfigConnectorRequest daConfigConnector, Boolean fuzzy) throws SQLException {
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            Map<String, String> tagUnitMap = new HashMap<>(8);

            conn = this.makeConnection(daConfigConnector);
            if (null == conn) {
                return null;
            }
            if (fuzzy) {
                statement = conn.prepareStatement("SELECT tag,engunits FROM pipoint..pipoint WHERE tag like ? ");
                statement.setString(1, prefix + "%");
            } else {
                statement = conn.prepareStatement("SELECT tag,engunits FROM pipoint..pipoint WHERE tag = ? ");
                statement.setString(1, prefix);
            }
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String tag = resultSet.getString("tag");
                String unit = resultSet.getString("engunits");
                tagUnitMap.put(tag, unit);
            }
            return tagUnitMap;
        } finally {
            closeDbResources(conn, statement, resultSet);
        }
    }

    private void closeDbResources(Connection conn, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.error("Close ResultSet Error", e);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                logger.error("Close PreparedStatement Error", e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        logger.error("Close Connection Error", e);
                    }
                }
            }
        }
    }

    private Connection makeConnection(DaConfigConnectorRequest daConfigConnector) {
        Connection conn = null;
        try {

            if (StringUtils.isBlank(daConfigConnector.getSqldas()) || StringUtils.isBlank(daConfigConnector.getServerHost())) {
                return null;
            }

            // do slqdas telnet 5461 or 5462 test
            TelnetClient telnetClient = new TelnetClient("vt200");
            telnetClient.setDefaultTimeout(3000);
            telnetClient.setConnectTimeout(3000);
            try {
                logger.info("Do Telnet SqlDas Server Test,HostName:{}", daConfigConnector.getSqldas());
                telnetClient.connect(daConfigConnector.getSqldas(), 5461);
            } catch (IOException e) {
                logger.error("Telnet SqlDas Server Error,{}", JSON.toJSONString(daConfigConnector));
                return null;
            }

            if (StringUtils.isBlank(daConfigConnector.getUserName()) || StringUtils.isBlank(daConfigConnector.getPassword())) {
                return null;
            }

            String url = protocol + daConfigConnector.getSqldas() + "/Data Source=" + daConfigConnector.getServerHost() + urlSuffix;
            Class.forName("com.osisoft.jdbc.Driver");
            DriverManager.setLoginTimeout(3);

            logger.info(JSON.toJSONString(daConfigConnector));
            conn = DriverManager.getConnection(url, daConfigConnector.getUserName(), daConfigConnector.getPassword());
        } catch (RuntimeException e) {
            logger.error("makeConnection error");
        } catch (Exception e) {
            logger.error("makeConnection error");
        }
        return conn;

    }

    private ModelMap getErrorConnectMap(ModelMap modelMap, Boolean b, String msg) {
        modelMap.put(IS_SUCCESS, b);
        modelMap.put(MSG, msg);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }

    private Map<String, Object> structureRetMap(boolean flag) {
        Map<String, Object> retMap = new HashMap<String, Object>(4);
        retMap.put(STATUS, flag);
        return retMap;
    }

    private ModelMap getErrorConnectMap(ModelMap modelMap) {
        modelMap.put(IS_SUCCESS, true);
        modelMap.put(MSG, CONNECT_FAIL);
        modelMap.put(DATA, structureRetMap(false));
        return modelMap;
    }
}
