package com.siemens.dasheng.web.model.batchregist;

import java.util.List;

/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class RegistDataValidInfo {

    /**
     * 数据点导入成功
     */
    private Integer successNo;

    /**
     * 数据点导入失败(正常)
     */
    private Integer errorNo;

    /**
     * //多少个标签已在存档数据库中创建
     */
    private Integer createNo;

    /**
     * 导入异常失败的tag以及原因
     */
    private List<ImportFalse> errorLists;

    public Integer getSuccessNo() {
        return successNo;
    }

    public void setSuccessNo(Integer successNo) {
        this.successNo = successNo;
    }

    public Integer getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(Integer errorNo) {
        this.errorNo = errorNo;
    }

    public Integer getCreateNo() {
        return createNo;
    }

    public void setCreateNo(Integer createNo) {
        this.createNo = createNo;
    }

    public List<ImportFalse> getErrorLists() {
        return errorLists;
    }

    public void setErrorLists(List<ImportFalse> errorLists) {
        this.errorLists = errorLists;
    }
}
