package com.siemens.dasheng.web.mapperfactory;

import com.siemens.dasheng.web.enums.DataBaseType;
import com.siemens.dasheng.web.mapper.DaConfigAppExtensionMapper;
import com.siemens.dasheng.web.model.DaConfigAppExtension;
import com.siemens.dasheng.web.singleton.conf.DataBaseConf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/** @author allan
 * Created by ofm on 2019/4/8.
 */
@Component
public class DaConfigAppExtensionCommon {
    @Autowired
    private DataBaseConf dataBaseConf;

    @Autowired
    private DaConfigAppExtensionMapper daConfigAppExtensionMapper;

    /**
     * 获取ID
     * @return
     */
    public Long selectId() {

        Long id = null;
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            id = daConfigAppExtensionMapper.selectIdPG();
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            id = daConfigAppExtensionMapper.selectIdOR();
        }
        return id;
    }

    /**
     * 保存
     * @param daConfigAppExtensions
     * @return
     */
    public int saveList(List<DaConfigAppExtension> daConfigAppExtensions) {
        if (DataBaseType.POSTGRESQL.getType().equals(dataBaseConf.getType())) {
            return daConfigAppExtensionMapper.saveList(daConfigAppExtensions);
        } else if (DataBaseType.ORACLE.getType().equals(dataBaseConf.getType())) {
            return daConfigAppExtensionMapper.saveListOracle(daConfigAppExtensions);
        } else {
            return 0;
        }
    }
}
