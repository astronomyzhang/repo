package com.siemens.dasheng.web.page;

import java.util.List;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/3/7
 */
public class PageRespone<T> {

    /**
     * data
     */
    private Object data;

    /**
     * data number
     */
    private Integer total;

    /**
     * current page no
     */
    private Integer current;

    /**
     * page size
     */
    private Integer pageSize;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
