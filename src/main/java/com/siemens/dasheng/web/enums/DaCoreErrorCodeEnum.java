package com.siemens.dasheng.web.enums;

import com.siemens.ofmcommon.enums.CodeEnum;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2019/5/30.
 */
public enum DaCoreErrorCodeEnum implements CodeEnum {

    /**
     * 特殊定义，用于返回正确结果
     */
    SUCCESS(111000, "SUCCESS"),
    /**
     * 测点不存在
     */
    SENSOR_NOT_EXIST(111002, "SENSOR_NOT_EXIST"),
    /**
     * 测点编码已存在
     */
    SENSOR_SIECODE_EXIST(111002, "SENSOR_SIECODE_EXIST"),
    /**
     * 回写测点不能修改连接信息和tag
     */
    WRITEBACK_SENSOR_CANT_MODIFY_CONNECTOR_INFO_AND_TAG(111002, "WRITEBACK_SENSOR_CANT_MODIFY_CONNECTOR_INFO_AND_TAG"),
    /**
     * 测点已被使用不能修改
     */
    SENSOR_IMPORTED_APP_AND_CANT_MODIFY(111002, "SENSOR_IMPORTED_APP_AND_CANT_MODIFY"),
    /**
     * tag不存在
     */
    DA_CONFIG_TAG_NOT_EXIST(111002, "DA_CONFIG_TAG_NOT_EXIST"),
    /**
     * tag已导入sensor,不可重复导入
     */
    TAG_IMPORTED_SENSOR(111002, "TAG_IMPORTED_SENSOR"),
    /**
     * 操作失败
     */
    OPERATE_FAIL(111002, "OPERATE_FAIL");

    private Integer code;

    private String msg;

    DaCoreErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
