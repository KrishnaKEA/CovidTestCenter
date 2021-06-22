package com.example.covid19.Model;




public class User {

    private int userID;
    private String name;
    private String userName;
    private String password;
    private String cpr;
    private String phone;
    private int tcID;
    private int vcID;
    private int tsID;
    private int vsID;
    private int vacNameID;



    public User() {
    }

    public User(int userID, String name, String userName, String password, String cpr, String phone, int tcID, int vcID, int tsID, int vsID,int vacNameID) {
        this.userID = userID;
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.cpr = cpr;
        this.phone = phone;
        this.tcID = tcID;
        this.vcID = vcID;
        this.tsID = tsID;
        this.vsID = vsID;
        this.vacNameID=vacNameID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTcID() {
        return tcID;
    }

    public void setTcID(int tcID) {
        this.tcID = tcID;
    }

    public int getVcID() {
        return vcID;
    }

    public void setVcID(int vcID) {
        this.vcID = vcID;
    }

    public int getTsID() {
        return tsID;
    }

    public void setTsID(int tsID) {
        this.tsID = tsID;
    }

    public int getVsID() {
        return vsID;
    }

    public void setVsID(int vsID) {
        this.vsID = vsID;
    }
    public int getVacNameID() {
        return vacNameID;
    }

    public void setVacNameID(int vacNameID) {
        this.vacNameID = vacNameID;
    }
}
