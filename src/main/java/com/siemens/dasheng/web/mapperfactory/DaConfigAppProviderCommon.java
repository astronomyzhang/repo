package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigAppProviderMapper;
import com.siemens.dasheng.web.model.DaConfigAppProvider;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author allan
 * Created by ofm on 2019/4/8.
 */
@Component
public class DaConfigAppProviderCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigAppProviderMapper daConfigAppProviderMapper;

    /**
     * 获取ID
     *
     * @return
     */
    public Long selectId() {

        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigAppProviderMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigAppProviderMapper.selectIdOR();
        }
        return id;
    }

    /**
     * 保存
     *
     * @param daConfigAppProviders
     * @return
     */
    public int saveList(List<DaConfigAppProvider> daConfigAppProviders) {
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            return daConfigAppProviderMapper.saveList(daConfigAppProviders);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            return daConfigAppProviderMapper.saveListOracle(daConfigAppProviders);
        } else {
            return 0;
        }
    }
}
