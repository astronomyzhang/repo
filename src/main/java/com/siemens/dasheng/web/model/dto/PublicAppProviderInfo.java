package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author allan
 * Created by ofm on 2019/4/8.
 */
public class PublicAppProviderInfo {
    @ApiModelProperty("app name")
    private String name;

    @ApiModelProperty("description")
    private String description;

    @ApiModelProperty("应用提供者数量")
    private Integer providerNum;






}
