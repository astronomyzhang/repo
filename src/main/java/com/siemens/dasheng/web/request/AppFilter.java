package com.siemens.dasheng.web.request;

/**
 * @author  allan
 * Created by ofm on 2019/4/3.
 */
public class AppFilter {

    /**
     * page
     */
    private String page;

    /**
     * pagesize
     */
    private String pageSize;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }
}
