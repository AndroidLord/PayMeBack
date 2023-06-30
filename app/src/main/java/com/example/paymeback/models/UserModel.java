package com.example.paymeback.models;

import java.util.HashSet;

public class UserModel {

    private String userId;
    private String Name;
    private String PhoneNo;
    private String Mail;
    private HashSet<String> borrow,lent;

    public UserModel(String userId, String name, String phoneNo, String email, HashSet<String> borrow, HashSet<String> lent) {
        this.userId = userId;
        this.Name = name;
        this.PhoneNo = phoneNo;
        this.Mail = email;
        this.borrow = borrow;
        this.lent = lent;
    }

    public UserModel(String name, String phoneNo, String email) {
        this.Name = name;
        this.PhoneNo = phoneNo;
        this.Mail = email;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.PhoneNo = phoneNo;
    }

    public String getEmail() {
        return Mail;
    }

    public void setEmail(String email) {
        this.Mail = email;
    }

    public HashSet<String> getBorrow() {
        return borrow;
    }

    public void setBorrow(HashSet<String> borrow) {
        this.borrow = borrow;
    }

    public HashSet<String> getLent() {
        return lent;
    }

    public void setLent(HashSet<String> lent) {
        this.lent = lent;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "Name='" + Name + '\'' +
                ", PhoneNo='" + PhoneNo + '\'' +
                ", Mail='" + Mail + '\'' +
                '}';
    }
}

