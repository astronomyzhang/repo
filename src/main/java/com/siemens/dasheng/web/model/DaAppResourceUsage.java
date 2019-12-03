package com.siemens.dasheng.web.model;

/**
 * @author allan
 * Created by z0041dpv on 5/5/2019.
 */
public class DaAppResourceUsage {
    private Long id;

    private String objectid;

    private String objecttype;

    private String appid;

    private Long createdate;

    private Long releasedate;

    private String release;

    private String consumerobject;

    private String consumerdescription;

    private String consumerobjectId;

    /**
     * 资源所属app
     */
    private Long ownappid;

    public DaAppResourceUsage() {
    }

    public DaAppResourceUsage(Long id, String objectid, String objecttype, String appid, Long createdate,
                              Long releasedate, String release, String consumerobject, String consumerdescription,
                              String consumerobjectId, Long ownappid) {
        this.id = id;
        this.objectid = objectid;
        this.objecttype = objecttype;
        this.appid = appid;
        this.createdate = createdate;
        this.releasedate = releasedate;
        this.release = release;
        this.consumerobject = consumerobject;
        this.consumerdescription = consumerdescription;
        this.consumerobjectId = consumerobjectId;
        this.ownappid = ownappid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjectid() {
        return objectid;
    }

    public void setObjectid(String objectid) {
        this.objectid = objectid;
    }

    public String getObjecttype() {
        return objecttype;
    }

    public void setObjecttype(String objecttype) {
        this.objecttype = objecttype;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Long getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Long createdate) {
        this.createdate = createdate;
    }

    public Long getReleasedate() {
        return releasedate;
    }

    public void setReleasedate(Long releasedate) {
        this.releasedate = releasedate;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getConsumerobject() {
        return consumerobject;
    }

    public void setConsumerobject(String consumerobject) {
        this.consumerobject = consumerobject;
    }

    public String getConsumerdescription() {
        return consumerdescription;
    }

    public void setConsumerdescription(String consumerdescription) {
        this.consumerdescription = consumerdescription;
    }

    public String getConsumerobjectId() {
        return consumerobjectId;
    }

    public void setConsumerobjectId(String consumerobjectId) {
        this.consumerobjectId = consumerobjectId;
    }

    public Long getOwnappid() {
        return ownappid;
    }

    public void setOwnappid(Long ownappid) {
        this.ownappid = ownappid;
    }
}