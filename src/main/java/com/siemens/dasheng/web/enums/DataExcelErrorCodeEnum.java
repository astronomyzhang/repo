package com.siemens.dasheng.web.enums;

import com.siemens.ofmcommon.enums.CodeEnum;

/**
 * @author ly
 * @date 2019/07/19
 */
public enum DataExcelErrorCodeEnum implements CodeEnum {

    /**
     * excel 导出超出最大数据限制错误
     */
    DATA_TOO_BIG_ERROR(100400, "data export can not be bigger than 300000");

    private final Integer code;
    private final String msg;

    DataExcelErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
