package com.siemens.dasheng.web.singleton.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuxin
 * ConnectorCache
 * created by xuxin on 15/12/2018
 */
public class ConnectorInfoCache {

    private final Map<String,Boolean> map = new ConcurrentHashMap<String,Boolean>();

    private ConnectorInfoCache(){

    }

    private static volatile ConnectorInfoCache single = null;

    /**
     * 双重检查
     * @return
     */
    public static ConnectorInfoCache getInstance() {
        if (single == null) {
            synchronized (ConnectorInfoCache.class) {
                if (single == null) {
                    single = new ConnectorInfoCache();
                }
            }
        }
        return single;
    }

    public void put(String key, Boolean value) {
        map.put(key,value);
    }

    public Boolean get(String key) {
        return map.get(key);
    }

    public Map<String,Boolean> getMap(){
        Map<String,Boolean> tmap = new HashMap<>(map);
        return tmap;
    }

    public void clearMap(){
        map.clear();
    }

    public void remove(String key){
        map.remove(key);
    }
}
