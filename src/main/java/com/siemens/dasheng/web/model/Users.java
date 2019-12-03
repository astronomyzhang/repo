package com.siemens.dasheng.web.model;

import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Z003W5DZ
 * @Auther: xiaozhi.gu
 * @Date: 6/21/2018 14:21
 * @Description:
 */
public class Users implements Serializable{

    private static final long serialVersionUID = 7603784943374260125L;
    @Id
    private Integer id;

    private String employeenum;

    private String username;

    private String passwd;

    private String truename;

    private String post;

    private Integer sex;

    private String phone;

    private String email;

    private Integer userlevelid;

    private Integer parentid;

    private String reserve01;

    private String reserve02;

    private String reserve03;

    private String groupid;

    private String department;

    private Integer isdistributed;

    private String lv;

    private Integer ssoid;

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", employeenum='" + employeenum + '\'' +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                ", truename='" + truename + '\'' +
                ", post='" + post + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", userlevelid=" + userlevelid +
                ", parentid=" + parentid +
                ", reserve01='" + reserve01 + '\'' +
                ", reserve02='" + reserve02 + '\'' +
                ", reserve03='" + reserve03 + '\'' +
                '}';
    }

    public Users(Integer id, String employeenum, String username, String passwd, String department, String truename, String post, Integer sex, String phone, String email, Integer userlevelid, Integer parentid, String reserve01, String reserve02, String reserve03, String groupid, Integer isdistributed, String lv, Integer ssoid) {
        this.id = id;
        this.employeenum = employeenum;
        this.username = username;
        this.passwd = passwd;
        this.department = department;
        this.truename = truename;
        this.post = post;
        this.sex = sex;
        this.phone = phone;
        this.email = email;
        this.userlevelid = userlevelid;
        this.parentid = parentid;
        this.reserve01 = reserve01;
        this.reserve02 = reserve02;
        this.reserve03 = reserve03;
        this.groupid = groupid;
        this.isdistributed = isdistributed;
        this.lv = lv;
        this.ssoid = ssoid;
    }

    public Users() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmployeenum() {
        return employeenum;
    }

    public void setEmployeenum(String employeenum) {
        this.employeenum = employeenum == null ? null : employeenum.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename == null ? null : truename.trim();
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post == null ? null : post.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getUserlevelid() {
        return userlevelid;
    }

    public void setUserlevelid(Integer userlevelid) {
        this.userlevelid = userlevelid;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
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

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department == null ? null : department.trim();
    }

    public Integer getIsdistributed() {
        return isdistributed;
    }

    public void setIsdistributed(Integer isdistributed) {
        this.isdistributed = isdistributed;
    }

    public String getLv() {
        return lv;
    }

    public void setLv(String lv) {
        this.lv = lv;
    }

    public Integer getSsoid() {
        return ssoid;
    }

    public void setSsoid(Integer ssoid) {
        this.ssoid = ssoid;
    }
}