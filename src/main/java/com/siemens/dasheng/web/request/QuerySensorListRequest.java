package com.siemens.dasheng.web.request;

import com.siemens.dasheng.web.page.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.siemens.dasheng.web.singleton.constant.LogConstant.*;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/4/8
 */
public class QuerySensorListRequest {

    @ApiModelProperty("分页情况")
    @Valid
    PageRequest pageRequest;

    private Long status;

    private List<Long> extendAppIds;

    private String searchContent;

    @NotNull(message = DA_CONFIG_NOTNULL_SCOPE)
    private Integer scope;

    @NotNull(message = DA_CONFIG_NOTNULL_APPLICATION_ID)
    private Long appId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public List<Long> getExtendAppIds() {
        return extendAppIds;
    }

    public void setExtendAppIds(List<Long> extendAppIds) {
        this.extendAppIds = extendAppIds;
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
