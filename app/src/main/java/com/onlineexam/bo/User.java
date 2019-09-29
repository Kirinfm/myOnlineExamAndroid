package com.onlineexam.bo;

import java.io.Serializable;

/**
 * Created by 方明 on 2017/3/12.
 */

public class User implements Serializable{
    private int id;
    private int schoolid;
    private String name;
    private String password;
    private String TelNo;
    private String photo;
    private int isteacher;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(int schoolid) {
        this.schoolid = schoolid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(int isteacher) {
        this.isteacher = isteacher;
    }

    public String getTelNo() {
        return TelNo;
    }

    public void setTelNo(String telNo) {
        TelNo = telNo;
    }
}
