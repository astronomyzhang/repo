package com.siemens.dasheng.web.page;

import com.siemens.dasheng.web.singleton.constant.CommonConstant;

import javax.validation.constraints.Min;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/7
 */
public class PageRequest {

    /**
     * page
     */
    @Min(1)
    private Integer page;

    /**
     * pagesize
     */
    private Integer pageSize;

    public Integer getPage() {
        return page = page == null ? CommonConstant.PAGE : page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize = pageSize == null ? CommonConstant.PAGESIZE : pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
