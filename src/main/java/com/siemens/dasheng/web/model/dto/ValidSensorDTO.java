package com.siemens.dasheng.web.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yaming.chen@siemens.com
 * Created by chenyaming on 2018/1/2.
 */
@ApiModel(value = "验证测点服务输入对象" )
public class ValidSensorDTO {

    /**
     * 输入待验证的测点数据
     */
    @ApiModelProperty(value = "输入待验证的测点数据")
    private List<ValidSensorDataList> inputSensorDataList;

    /**
     * 波动-时间区间
     */
    @ApiModelProperty(value = "波动-时间区间")
    private Long erratictime;

    /**
     * 波动-最大正常波动
     */
    @ApiModelProperty(value = "波动-最大正常波动")
    private BigDecimal erraticdbwidth;

    /**
     * 波动-最大正常波动次数
     */
    @ApiModelProperty(value = "波动-最大正常波动次数")
    private Long erraticrdbbreaks;

    /**
     * 波动-是否激活
     */
    @ApiModelProperty(value = "波动-是否激活")
    private Short erraticactive;

    // ----

    /**
     * 阈值上限
     */
    @ApiModelProperty(value = "阈值上限")
    private BigDecimal failureupperlimit;

    /**
     * 阈值上限是否激活
     */
    @ApiModelProperty(value = "阈值上限是否激活")
    private Short failureupperlimitactive;

    /**
     * 阈值下限
     */
    @ApiModelProperty(value = "阈值下限")
    private BigDecimal failurelowerlimit;

    /**
     * 阈值下限是否激活
     */
    @ApiModelProperty(value = "阈值下限是否激活")
    private Short failurelowerlimitactive;

    // ----

    /**
     * 绝对梯度
     */
    @ApiModelProperty(value = "绝对梯度")
    private BigDecimal failureabsgradthresh;

    /**
     * 相对梯度
     */
    @ApiModelProperty(value = "相对梯度")
    private BigDecimal failurerelgradthresh;

    /**
     * 梯度是否激活
     */
    @ApiModelProperty(value = "梯度是否激活")
    private Short gradactive;

    // ----

    /**
     * 死值-时间区间
     */
    @ApiModelProperty(value = "死值-时间区间")
    private Long locktime;

    /**
     * 死值-最小正常波动
     */
    @ApiModelProperty(value = "死值-最小正常波动")
    private BigDecimal lockeddbwidth;

    /**
     * 死值-是否激活
     */
    @ApiModelProperty(value = "死值-是否激活")
    private Short lockedactive;

    public List<ValidSensorDataList> getInputSensorDataList() {
        return inputSensorDataList;
    }

    public void setInputSensorDataList(List<ValidSensorDataList> inputSensorDataList) {
        this.inputSensorDataList = inputSensorDataList;
    }

    public Long getErratictime() {
        return erratictime;
    }

    public void setErratictime(Long erratictime) {
        this.erratictime = erratictime;
    }

    public BigDecimal getErraticdbwidth() {
        return erraticdbwidth;
    }

    public void setErraticdbwidth(BigDecimal erraticdbwidth) {
        this.erraticdbwidth = erraticdbwidth;
    }

    public Long getErraticrdbbreaks() {
        return erraticrdbbreaks;
    }

    public void setErraticrdbbreaks(Long erraticrdbbreaks) {
        this.erraticrdbbreaks = erraticrdbbreaks;
    }

    public Short getErraticactive() {
        return erraticactive;
    }

    public void setErraticactive(Short erraticactive) {
        this.erraticactive = erraticactive;
    }

    public BigDecimal getFailureupperlimit() {
        return failureupperlimit;
    }

    public void setFailureupperlimit(BigDecimal failureupperlimit) {
        this.failureupperlimit = failureupperlimit;
    }

    public Short getFailureupperlimitactive() {
        return failureupperlimitactive;
    }

    public void setFailureupperlimitactive(Short failureupperlimitactive) {
        this.failureupperlimitactive = failureupperlimitactive;
    }

    public BigDecimal getFailurelowerlimit() {
        return failurelowerlimit;
    }

    public void setFailurelowerlimit(BigDecimal failurelowerlimit) {
        this.failurelowerlimit = failurelowerlimit;
    }

    public Short getFailurelowerlimitactive() {
        return failurelowerlimitactive;
    }

    public void setFailurelowerlimitactive(Short failurelowerlimitactive) {
        this.failurelowerlimitactive = failurelowerlimitactive;
    }

    public BigDecimal getFailureabsgradthresh() {
        return failureabsgradthresh;
    }

    public void setFailureabsgradthresh(BigDecimal failureabsgradthresh) {
        this.failureabsgradthresh = failureabsgradthresh;
    }

    public BigDecimal getFailurerelgradthresh() {
        return failurerelgradthresh;
    }

    public void setFailurerelgradthresh(BigDecimal failurerelgradthresh) {
        this.failurerelgradthresh = failurerelgradthresh;
    }

    public Short getGradactive() {
        return gradactive;
    }

    public void setGradactive(Short gradactive) {
        this.gradactive = gradactive;
    }

    public Long getLocktime() {
        return locktime;
    }

    public void setLocktime(Long locktime) {
        this.locktime = locktime;
    }

    public BigDecimal getLockeddbwidth() {
        return lockeddbwidth;
    }

    public void setLockeddbwidth(BigDecimal lockeddbwidth) {
        this.lockeddbwidth = lockeddbwidth;
    }

    public Short getLockedactive() {
        return lockedactive;
    }

    public void setLockedactive(Short lockedactive) {
        this.lockedactive = lockedactive;
    }

    @Override
    public String toString() {
        return "InputValidSensorDTO{" +
                "inputSensorDataList=" + inputSensorDataList +
                ", erratictime=" + erratictime +
                ", erraticdbwidth=" + erraticdbwidth +
                ", erraticrdbbreaks=" + erraticrdbbreaks +
                ", erraticactive=" + erraticactive +
                ", failureupperlimit=" + failureupperlimit +
                ", failureupperlimitactive=" + failureupperlimitactive +
                ", failurelowerlimit=" + failurelowerlimit +
                ", failurelowerlimitactive=" + failurelowerlimitactive +
                ", failureabsgradthresh=" + failureabsgradthresh +
                ", failurerelgradthresh=" + failurerelgradthresh +
                ", gradactive=" + gradactive +
                ", locktime=" + locktime +
                ", lockeddbwidth=" + lockeddbwidth +
                ", lockedactive=" + lockedactive +
                '}';
    }
}
