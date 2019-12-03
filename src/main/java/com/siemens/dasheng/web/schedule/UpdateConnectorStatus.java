package com.siemens.dasheng.web.schedule;


import com.alibaba.fastjson.JSON;
import com.siemens.da.util.UUIDUtil;
import com.siemens.dasheng.web.enums.ConnecteStatus;
import com.siemens.dasheng.web.mapper.DaConfigConnectorMapper;
import com.siemens.dasheng.web.model.DaConfigConnector;
import com.siemens.dasheng.web.model.Env;
import com.siemens.dasheng.web.request.DaConfigConnectorRequest;
import com.siemens.dasheng.web.service.DataConnectorService;
import com.siemens.dasheng.web.singleton.SieExecutors;
import com.siemens.dasheng.web.singleton.conf.ConnectorCache;
import com.siemens.dasheng.web.singleton.conf.ConnectorInfoCache;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.siemens.dasheng.web.singleton.constant.ServiceConstant.UNIT_TEST;


/**
 * @author xin.xu@siemens.com
 * @date 12/3/2018
 */

@Component
@EnableScheduling
@ConfigurationProperties
public class UpdateConnectorStatus {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static Boolean FALSE = false;

    private final static Boolean TRUE = true;

    private final static String DATA = "data";

    private final static String STATUS = "status";


    List<DaConfigConnector> connectList = null;

    @Autowired
    private DaConfigConnectorMapper daConfigConnectorMapper;

    @Autowired
    private DataConnectorService dataConnectorService;

    @Autowired
    private StringEncryptor stringEncryptor;

    @Value("${pi.protocol}")
    private String protocol;

    @Value("${pi.url.suffix}")
    private String urlSuffix;

    @Autowired
    private Env env;




    private final static String KEY = "DPP@DuNEfVnluGmyhfqevygZFMxKcNpkMeTanhRoMmJhQSiDnLaeBTjQszAQEakB";

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


    /**
     * 更新连接状态
     */
    @Scheduled(fixedDelay = 60000)
    @SchedulerLock(name = "demoLockTask", lockAtMostFor = 5*1000)
    protected void updateConnectorStatus() {

        if (!UNIT_TEST.equals(env.getName())) {
            try {
                connectList = daConfigConnectorMapper.selectAllConnectors();

                taskCount = new AtomicInteger(connectList.size());

                if (CollectionUtils.isEmpty(connectList)) {
                    return;
                }

                //清除已删除的
                List<Long> tempIds = strConnectIds(connectList);
                for (Long tid : ConnectorCache.getInstance().getMap().keySet()) {
                    if (!tempIds.contains(tid)) {
                        ConnectorCache.getInstance().remove(tid);
                    }
                }

                for (DaConfigConnector connect : connectList) {
                    SieExecutors.executor(new Task(connect));
                }

                while (true) {
                    if (getTaskCount().intValue()==0) {
                        break;
                    }
                }

                //修改数据库connect状态
                modifyConnectorStatus();
                //清除connectorInfoCache缓存
                ConnectorInfoCache.getInstance().clearMap();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

    }

    private void modifyConnectorStatus() {
        Map<Long, Boolean> tmap = ConnectorCache.getInstance().getMap();
        if (tmap.isEmpty()) {
            return;
        }
        List<DaConfigConnector> connectorList = new ArrayList<>();
        for (Map.Entry<Long, Boolean> map : tmap.entrySet()) {
            DaConfigConnector con = new DaConfigConnector();
            con.setId(map.getKey());
            con.setStatus(map.getValue() ? ConnecteStatus.CONNECTABLE.getType() : ConnecteStatus.UNCONNECTABLE.getType());
            connectorList.add(con);
        }
        daConfigConnectorMapper.batchUpdateStatusByPrimaryKey(connectorList);
    }

    private List<Long> strConnectIds(List<DaConfigConnector> connectList) {
        List<Long> ids = new ArrayList<>();
        for (DaConfigConnector con : connectList) {
            ids.add(con.getId());
        }
        return ids;
    }


    private class Task implements Runnable {

        private DaConfigConnector connect;

        Task(DaConfigConnector connect) {
            this.connect = connect;
        }

        @Override
        public void run() {
            MDC.clear();
            MDC.put("bizTraceId", UUIDUtil.uuidGenerate());
            logger.info("updateConnectorStatus,"+ JSON.toJSONString(connect));
            //对password解密
            connect.setPassword(StringUtils.isBlank(connect.getPassword()) ? "" : stringEncryptor.decrypt(connect.getPassword().trim()));
            DaConfigConnectorRequest daConfigConnectorRequest = DaConfigConnectorRequest.format(connect);
            //过滤重复
            String connectorInfo = strConnectorInfo(connect);
            try {
                if(null == ConnectorInfoCache.getInstance().get(connectorInfo)){
                    ModelMap modelMap = dataConnectorService.testConnector(daConfigConnectorRequest);
                    boolean variyTest = (boolean) ((Map<String, Object>) modelMap.get(DATA)).get(STATUS);
                    if (variyTest) {
                        ConnectorCache.getInstance().put(connect.getId(), TRUE);
                        ConnectorInfoCache.getInstance().put(connectorInfo,TRUE);
                    } else {
                        ConnectorCache.getInstance().put(connect.getId(), FALSE);
                        ConnectorInfoCache.getInstance().put(connectorInfo,FALSE);
                    }
                }else{
                    ConnectorCache.getInstance().put(connect.getId(), ConnectorInfoCache.getInstance().get(connectorInfo));
                }


            } catch (Exception e) {
                ConnectorCache.getInstance().put(connect.getId(), FALSE);
                ConnectorInfoCache.getInstance().put(connectorInfo,FALSE);
            } finally {
                reduceTaskCount();
            }

        }

        private String strConnectorInfo(DaConfigConnector connect) {
            StringBuilder str = new StringBuilder();
            str.append(connect.getConnectorType()).append("#").append(connect.getConnectorClass()).append("#")
                    .append(connect.getArchivedDatabase()).append("#").append(connect.getConnectApproach()).append("#")
                    .append(null == connect.getServerHost() ? "" :connect.getServerHost()).append("#")
                    .append(null == connect.getSqldas() ? "" : connect.getSqldas()).append("#")
                    .append(null == connect.getPort() ? "" : connect.getPort()).append("#")
                    .append(null == connect.getDbName() ? "" : connect.getDbName()).append("#")
                    .append(null == connect.getUserName() ? "" : connect.getUserName()).append("#")
                    .append(null == connect.getPassword() ? "" : connect.getPassword()).append("#")
                    .append(null == connect.getUrl() ? "" : connect.getUrl()).append("#")
                    .append(null == connect.getDaServerName() ? "" : connect.getDaServerName()).append("#")
                    .append(null == connect.getHdaServerName() ? "" : connect.getHdaServerName());
            return str.toString();
        }

    }


}
