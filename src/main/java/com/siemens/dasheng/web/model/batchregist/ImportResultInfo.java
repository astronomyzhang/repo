package com.siemens.dasheng.web.model.batchregist;


import java.util.List;

/**
 * @author zhangliming
 * @Date: 2019/10/12 17:10
 */
public class ImportResultInfo {


    /**
     *  //导入的成功数
     */
    private  Integer succeedNum;

    /**
     *  //导入的失败数
     */
    private Integer failedNum;

    /**
     * 失败清单
     */
    private List<ImportFalse> failedList;

    public Integer getSucceedNum() {
        return succeedNum;
    }

    public void setSucceedNum(Integer succeedNum) {
        this.succeedNum = succeedNum;
    }

    public Integer getFailedNum() {
        return failedNum;
    }

    public void setFailedNum(Integer failedNum) {
        this.failedNum = failedNum;
    }

    public List<ImportFalse> getFailedList() {
        return failedList;
    }

    public void setFailedList(List<ImportFalse> failedList) {
        this.failedList = failedList;
    }
}
