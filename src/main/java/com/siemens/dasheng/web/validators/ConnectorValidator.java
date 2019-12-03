package com.siemens.dasheng.web.validators;


import com.siemens.dasheng.web.enums.CategoryType;
import com.siemens.dasheng.web.enums.ConnectorDatabaseType;
import com.siemens.dasheng.web.model.DaConnectorConfig;
import org.apache.commons.lang3.StringUtils;

/**
 * ly
 * 2018.12.27
 * 连接器验证器类
 *
 * @author ly
 */
public final class ConnectorValidator {

    public static final int DEFAULT = -1;
    public static final int PI_SQLDAS = 0;
    public static final int OPENPLANT_SDK = 2;
    public static final int PI_SDK = 1;
    public static final int PI_SQLDAS_LINUX = 3;
    public static final int PI_SQLDAS_WINDOWS = 4;
    public static final String OSIPI = ConnectorDatabaseType.OSIPI.getType();
    public static final String OPENPLANT = ConnectorDatabaseType.OPENPLANT.getType();
    public static final String OPC = ConnectorDatabaseType.OPC.getType();

    public static final String SQLDASV2016_LINUX = CategoryType.SQLDASV2016_LINUX.getType();
    public static final String SQLDASV2012_LINUX = CategoryType.SQLDASV2012_LINUX.getType();
    public static final String SQLDASV2016_WINDOW = CategoryType.SQLDASV2016_WINDOWS.getType();
    public static final String SQLDASV2012_WINDOW = CategoryType.SQLDASV2012_WINDOWS.getType();
    public static final String SDKV2016 = CategoryType.SDKV2016_WINDOWS.getType();
    public static final String OPENPLANTSDK = CategoryType.OPENPLANTSDK_LINUX.getType();

    private ConnectorValidator() {

    }



    /**
     * 获取验证器策略
     *
     * @param connector
     * @return
     */
    public static int getValidateStrategy(DaConnectorConfig connector) {
        if (connector == null) {
            return DEFAULT;
        }


        String dataBaseType = connector.getArchivedDatabase();
        String categoryType = connector.getConnectApproach();

        int flag = DEFAULT;
        if (OSIPI.equals(dataBaseType)) {
            if (SQLDASV2016_LINUX.equals(categoryType) ||
                    SQLDASV2012_LINUX.equals(categoryType)
                    ) {
                flag = PI_SQLDAS_LINUX;
            } else if (SQLDASV2016_WINDOW.equals(categoryType) ||
                    SQLDASV2012_WINDOW.equals(categoryType)){
                flag = PI_SQLDAS_WINDOWS;
            } else if (SDKV2016.equals(categoryType)) {
                flag = PI_SDK;
            }
        } else if (OPENPLANT.equals(dataBaseType)) {
            if (OPENPLANTSDK.equals(categoryType)) {
                flag = OPENPLANT_SDK;
            }
        } else if (OPC.equals(dataBaseType)) {
            flag = DEFAULT;
        }
        return flag;
    }



}
