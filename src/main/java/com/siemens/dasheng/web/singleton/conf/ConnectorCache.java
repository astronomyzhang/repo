package com.siemens.dasheng.web.singleton.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xuxin
 * ConnectorCache
 * created by xuxin on 15/12/2018
 */
public class ConnectorCache {

    private final Map<Long,Boolean> map = new ConcurrentHashMap<Long,Boolean>();

    private ConnectorCache(){

    }

    private static volatile ConnectorCache single = null;

    /**
     * 双重检查
     * @return
     */
    public static ConnectorCache getInstance() {
        if (single == null) {
            synchronized (ConnectorCache.class) {
                if (single == null) {
                    single = new ConnectorCache();
                }
            }
        }
        return single;
    }

    public void put(Long key, Boolean value) {
        map.put(key,value);
    }

    public Boolean get(Long key) {
        return map.get(key);
    }

    public Map<Long,Boolean> getMap(){
        Map<Long,Boolean> tmap = new HashMap<>(map);
        return tmap;
    }

    public void remove(Long key){
        map.remove(key);
    }
}
