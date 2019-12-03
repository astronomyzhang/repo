package com.siemens.dasheng.web.enums;

import com.siemens.ofmcommon.enums.CodeEnum;

/**
 * @author zhangliming
 * @Date: 7/18/2019 16:53
 */
public enum BatchRegistSensorCodeEnum implements CodeEnum {

    /**
     * 特殊定义，用于返回正确结果
     */
    SUCCESS(111000, "SUCCESS"),

    /**
     * 待上传文件数据错误
     */
    TO_UPDATE_REGIST_DATA_ERROR(111001, "TO_UPDATE_REGIST_DATA_ERROR"),

    /**
     * 测点不能为空
     */
    SENSOR_CODE_NOT_BLANK_ERROE(111002, "SENSOR_CODE_NOT_BLANK_ERROE"),

    /**
     * tag不能为空
     */
    TAG_NOT_BLANK_ERROE(111003, "TAG_NOT_BLANK_ERROE"),

    /**
     * tagname 不合规范
     */
    TAG_NAME_NOT_IMPROPER(111004, "TAG_NAME_NOT_IMPROPER"),

    /**
     * tag名最长长度
     */
    TAG_MAX_LENGTH_ERROR(111005, "TAG_MAX_LENGTH_ERROR"),

    /**
     * sensor名最大长度
     */
    SENSOR_MAX_LENGTH_ERROR(111006, "SENSOR_MAX_LENGTH_ERROR"),

    /**
     * desc最大长度
     */
    DESCRIPTION_MAX_LENGTH_ERROR(111007, "DESCRIPTION_MAX_LENGTH_ERROR"),

    /**
     * create tag unknow
     */
    CREATE_TAG_UNKWON_ERROE(111008, "CREATE_TAG_UNKWON_ERROE"),


    /**
     *从excel文件读取数据
     */
    READ_DATA_FROM_EXCEL_ERROR(111009, "READ_DATA_FROM_EXCEL_ERROR"),


    /**
     * excel中 siecode重复
     */
    SIECODE_REPEAT_IN_EXCEL_ERROR(111010, "SIECODE_REPEAT_IN_EXCEL_ERROR"),


    /**
     * excel中，tagname重复
     */
    TAG_REPEAT_IN_EXCEL_ERROR(111011, "TAG_REPEAT_IN_EXCEL_ERROR"),


    /**
     * sensor已经存在
     */
    SENSOR_HAS_EXIST(111012, "SENSOR_HAS_EXIST_DB"),


    /**
     * tag在数据源中已存在
     */
    TAG_HAS_EXIST_UPDER_CONNECTOR(111013, "TAG_HAS_EXIST_IN_CONNECTOR"),


    /**
     * tag在数据源中不存在
     */
    TAG_NOT_EXIST_UPDER_CONNECTOR(111014, "TAG_NOT_EXIST_IN_CONNECTOR"),


    /**
     * 上传文件校验失败
     */
    VALID_IMPORT_DATA_ERROR(111015, "VALID_IMPORT_DATA_ERROR"),

    /**
     * pi数据库满点
     */
    PI_MAX_LICENCE_LIMIT(111016, "PI_MAX_LICENCE_LIMIT"),


    /**
     * pi客户端机器无权限
     */
    PI_CLIENT_NO_AUTH_MSG(111017, "PI_CLIENT_NO_AUTH"),


    /**
     * pi server连接超时
     */
    PI_CONNECT_TIMEOUT_MSG(111019, "PI_CONNECT_TIMEOUT_MSG"),


    /**
     * 最大导入数量
     */
    EXCEL_MAX_SIZE_ERROE(111020, "EXCEL_MAX_SIZE_ERROE"),


    /**
     * opu create tag 必须为no
     */
    OPU_CREATE_TAG_NO_ERROR(111021, "OPU_CREATE_TAG_NO_ERROR"),

    /**
     *TAG中文字符错误
     */
    TAG_CHINESE_CHARACTER_ERROR (111022, "TAG_CHINESE_CHARACTER_ERROR"),

    /**
     * SIECODE中文字符错误
     */
    SIECODE_CHINESE_CHARACTER_ERROR (111023, "SIECODE_CHINESE_CHARACTER_ERROR"),

    /**
     *导入数据错误
     */
    BATCH_MPORT_DATA_ERROR (111024, "BATCH_MPORT_DATA_ERROR"),


    /**
     * openplant连接超时
     */
    OPENPLANT_TIMEOUT_MSG(111020, "OPENPLANT_TIMEOUT_MSG"),


    /**
     * 连接器已被移除
     */
    CONNECTOR_HAS_BEEN_REMOVED(111021, "CONNECTOR_HAS_BEEN_REMOVED"),


    /**
     * openplant tag 不合法
     */
    OPENPLANT_TAG_NAME_INVALID_ERROR(111021, "OPENPLANT_TAG_NAME_INVALID_ERROR"),

    /**
     * openplant tag 不合法
     */
    TAG_HAS_BEEN_MAPED(111022, "TAG_HAS_BEEN_MAPED"),


    /**
     * openplanttag名最长长度
     */
    OPENPLANT_TAG_MAX_LENGTH(111023, "OPENPLANT_TAG_MAX_LENGTH"),

    /**
     * 操作失败
     */
    OPERATE_FAIL(111999, "OPERATE_FAIL"),

    ;

    private Integer code;

    private String msg;

    BatchRegistSensorCodeEnum(Integer code, String msg) {
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
