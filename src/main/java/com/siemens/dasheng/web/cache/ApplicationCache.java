package com.siemens.dasheng.web.cache;

import com.siemens.dasheng.web.model.OrigApplication;
import com.siemens.dasheng.web.singleton.conf.ConnectorCache;

import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationCache
 *
 * @author xuxin
 * @date 2019/3/5
 */
public class ApplicationCache {
    private ApplicationCache(){

    }

    private static volatile ApplicationCache single = null;

    /**
     * 双重检查
     * @return
     */
    public static ApplicationCache getInstance() {
        if (single == null) {
            synchronized (ApplicationCache.class) {
                if (single == null) {
                    single = new ApplicationCache();
                }
            }
        }
        return single;
    }

    public List<OrigApplication> getOrigApplicationList(){
        List<OrigApplication> appList = new ArrayList<>();
        OrigApplication app1= new OrigApplication();
        app1.setId(1L);
        app1.setName("app1");
        OrigApplication app2= new OrigApplication();
        app2.setId(2L);
        app2.setName("app2");
        appList.add(app1);
        appList.add(app2);
        return appList;
    }
}
