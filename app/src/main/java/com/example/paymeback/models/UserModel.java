package com.example.paymeback.models;

import java.util.HashMap;
import java.util.HashSet;

public class UserModel {

    private String userId;
    private String name;
    private int phoneNo;
    private String email;
    private HashSet<String> borrow,lent;

    public UserModel(String userId, String name, int phoneNo, String email, HashSet<String> borrow, HashSet<String> lent) {
        this.userId = userId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.borrow = borrow;
        this.lent = lent;
    }

    public UserModel(String name, int phoneNo, String email) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(int phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
