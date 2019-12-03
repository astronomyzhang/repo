package com.siemens.dasheng.web.model;

/**
 * Demo class
 *
 * @author xuxin
 * @date 2019/5/28
 */
public class DaConfigGroupPlus extends DaConfigGroup {
    private Integer usage;

    private Integer usedNum;

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }

    public Integer getUsage() {
        return usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }
}
