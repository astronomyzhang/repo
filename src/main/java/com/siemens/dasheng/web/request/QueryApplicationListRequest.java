package com.siemens.dasheng.web.request;

import com.siemens.dasheng.web.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.DA_CONFIG_NOTBLANK_APPLICATION_ID;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/7
 */
public class QueryApplicationListRequest {

    private Long connectorStatus;

    private Long status;

    private String searchContent;

    @NotBlank(message = DA_CONFIG_NOTBLANK_APPLICATION_ID)
    private String applicationId;

    @ApiModelProperty("分页情况")
    @Valid
    PageRequest pageRequest;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getConnectorStatus() {
        return connectorStatus;
    }

    public void setConnectorStatus(Long connectorStatus) {
        this.connectorStatus = connectorStatus;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public PageRequest getPageRequest() {
        return pageRequest;
    }

    public void setPageRequest(PageRequest pageRequest) {
        this.pageRequest = pageRequest;
    }
}
