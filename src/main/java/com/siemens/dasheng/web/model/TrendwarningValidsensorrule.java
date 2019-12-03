package com.siemens.dasheng.web.model;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author amon
 */
public class TrendwarningValidsensorrule {

    @Id
    private String id;

    private String sensorid;

    private String sensorname;

    private String name;

    private String creater;

    private Date createtime;

    private String modifier;

    private Date modifietime;

    private Short refer;

    private Long locktime;

    private BigDecimal lockeddbwidth;

    private Short lockedactive;

    private Long erratictime;

    private BigDecimal erraticdbwidth;

    private Long erraticrdbbreaks;

    private Short erraticactive;

    private BigDecimal failureupperlimit;

    private Short failureupperlimitactive;

    private BigDecimal failurelowerlimit;

    private Short failurelowerlimitactive;

    private BigDecimal failureabsgradthresh;

    private BigDecimal failurerelgradthresh;

    private Short gradactive;

    private Long redundantgroupno;

    private BigDecimal redundantdbwidth;

    private Short redundantactive = 0;

    private String globalappid;

    public TrendwarningValidsensorrule() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid == null ? null : sensorid.trim();
    }

    public String getSensorname() {
        return sensorname;
    }

    public void setSensorname(String sensorname) {
        this.sensorname = sensorname == null ? null : sensorname.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater == null ? null : creater.trim();
    }

    public Date getCreatetime() {
        if(createtime == null) {
            return null;
        }
        return (Date) createtime.clone();
    }

    public void setCreatetime(Date createtime) {
        if(createtime == null) {
            this.createtime = null;
        }else {
            this.createtime = (Date) createtime.clone();
        }
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    public Date getModifietime() {
        if(modifietime == null) {
            return null;
        }
        return (Date) modifietime.clone();
    }

    public void setModifietime(Date modifietime) {
        if(modifietime == null) {
            this.modifietime = null;
        }else {
            this.modifietime = (Date) modifietime.clone();
        }
    }

    public Short getRefer() {
        return refer;
    }

    public void setRefer(Short refer) {
        this.refer = refer;
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

    public Long getRedundantgroupno() {
        return redundantgroupno;
    }

    public void setRedundantgroupno(Long redundantgroupno) {
        this.redundantgroupno = redundantgroupno;
    }

    public BigDecimal getRedundantdbwidth() {
        return redundantdbwidth;
    }

    public void setRedundantdbwidth(BigDecimal redundantdbwidth) {
        this.redundantdbwidth = redundantdbwidth;
    }

    public Short getRedundantactive() {
        return redundantactive;
    }

    public void setRedundantactive(Short redundantactive) {
        this.redundantactive = redundantactive;
    }

    public String getGlobalappid() {
        return globalappid;
    }

    public void setGlobalappid(String globalappid) {
        this.globalappid = globalappid;
    }
}