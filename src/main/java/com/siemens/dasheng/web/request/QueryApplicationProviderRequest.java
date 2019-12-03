package com.siemens.dasheng.web.request;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_APPLICATION_ID;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/8
 */
public class QueryApplicationProviderRequest {

    @NotNull(message = DA_CONFIG_NOTBLANK_APPLICATION_ID)
    private Long applicationId;

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
