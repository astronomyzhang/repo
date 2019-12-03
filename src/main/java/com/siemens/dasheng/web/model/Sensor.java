package com.siemens.dasheng.web.model;

import javax.persistence.Id;

/**
 * @author chenyaming
 */
public class Sensor {

    @Id
    private String id;

    private String equipmentid;

    private String figurenumber;

    private String sensorkks;

    private String description;

    private String title;

    private String location;

    private String type;

    private String mediumtype;

    private String iotype;

    private String signaltype;

    private String connectedsystem;

    private String powersupplier;

    private String isolation;

    private String rangeunit;

    private String rangeup;

    private String rangedown;

    private String tendency;

    private String io1;

    private String io2;

    private String connectiontype;

    private String signaleffectiveway;

    private String reserve01;

    private String reserve02;

    private String reserve03;

    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", equipmentid='" + equipmentid + '\'' +
                ", figurenumber='" + figurenumber + '\'' +
                ", sensorkks='" + sensorkks + '\'' +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", mediumtype='" + mediumtype + '\'' +
                ", iotype='" + iotype + '\'' +
                ", signaltype='" + signaltype + '\'' +
                ", connectedsystem='" + connectedsystem + '\'' +
                ", powersupplier='" + powersupplier + '\'' +
                ", isolation='" + isolation + '\'' +
                ", rangeunit='" + rangeunit + '\'' +
                ", rangeup='" + rangeup + '\'' +
                ", rangedown='" + rangedown + '\'' +
                ", tendency='" + tendency + '\'' +
                ", io1='" + io1 + '\'' +
                ", io2='" + io2 + '\'' +
                ", connectiontype='" + connectiontype + '\'' +
                ", signaleffectiveway='" + signaleffectiveway + '\'' +
                ", reserve01='" + reserve01 + '\'' +
                ", reserve02='" + reserve02 + '\'' +
                ", reserve03='" + reserve03 + '\'' +
                '}';
    }

    public String getEquipmentid() {
        return equipmentid;
    }

    public void setEquipmentid(String equipmentid) {
        this.equipmentid = equipmentid;
    }

    public Sensor() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getFigurenumber() {
        return figurenumber;
    }

    public void setFigurenumber(String figurenumber) {
        this.figurenumber = figurenumber == null ? null : figurenumber.trim();
    }

    public String getSensorkks() {
        return sensorkks;
    }

    public void setSensorkks(String sensorkks) {
        this.sensorkks = sensorkks == null ? null : sensorkks.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getMediumtype() {
        return mediumtype;
    }

    public void setMediumtype(String mediumtype) {
        this.mediumtype = mediumtype == null ? null : mediumtype.trim();
    }

    public String getIotype() {
        return iotype;
    }

    public void setIotype(String iotype) {
        this.iotype = iotype == null ? null : iotype.trim();
    }

    public String getSignaltype() {
        return signaltype;
    }

    public void setSignaltype(String signaltype) {
        this.signaltype = signaltype == null ? null : signaltype.trim();
    }

    public String getConnectedsystem() {
        return connectedsystem;
    }

    public void setConnectedsystem(String connectedsystem) {
        this.connectedsystem = connectedsystem == null ? null : connectedsystem.trim();
    }

    public String getPowersupplier() {
        return powersupplier;
    }

    public void setPowersupplier(String powersupplier) {
        this.powersupplier = powersupplier == null ? null : powersupplier.trim();
    }

    public String getIsolation() {
        return isolation;
    }

    public void setIsolation(String isolation) {
        this.isolation = isolation == null ? null : isolation.trim();
    }

    public String getRangeunit() {
        return rangeunit;
    }

    public void setRangeunit(String rangeunit) {
        this.rangeunit = rangeunit == null ? null : rangeunit.trim();
    }

    public String getRangeup() {
        return rangeup;
    }

    public void setRangeup(String rangeup) {
        this.rangeup = rangeup == null ? null : rangeup.trim();
    }

    public String getRangedown() {
        return rangedown;
    }

    public void setRangedown(String rangedown) {
        this.rangedown = rangedown == null ? null : rangedown.trim();
    }

    public String getTendency() {
        return tendency;
    }

    public void setTendency(String tendency) {
        this.tendency = tendency == null ? null : tendency.trim();
    }

    public String getIo1() {
        return io1;
    }

    public void setIo1(String io1) {
        this.io1 = io1 == null ? null : io1.trim();
    }

    public String getIo2() {
        return io2;
    }

    public void setIo2(String io2) {
        this.io2 = io2 == null ? null : io2.trim();
    }

    public String getConnectiontype() {
        return connectiontype;
    }

    public void setConnectiontype(String connectiontype) {
        this.connectiontype = connectiontype == null ? null : connectiontype.trim();
    }

    public String getSignaleffectiveway() {
        return signaleffectiveway;
    }

    public void setSignaleffectiveway(String signaleffectiveway) {
        this.signaleffectiveway = signaleffectiveway == null ? null : signaleffectiveway.trim();
    }

    public String getReserve01() {
        return reserve01;
    }

    public void setReserve01(String reserve01) {
        this.reserve01 = reserve01 == null ? null : reserve01.trim();
    }

    public String getReserve02() {
        return reserve02;
    }

    public void setReserve02(String reserve02) {
        this.reserve02 = reserve02 == null ? null : reserve02.trim();
    }

    public String getReserve03() {
        return reserve03;
    }

    public void setReserve03(String reserve03) {
        this.reserve03 = reserve03 == null ? null : reserve03.trim();
    }
}