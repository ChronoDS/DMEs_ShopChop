package com.example.danie.dmes_shopchop;

/**
 * Created by Michael on 11/01/2018.
 */

public class User {

    private String phone;
    private String name;
    private boolean admin;
    private String email;

    public User() {}

    public User(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.admin = false;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin() {
        this.admin = true;
    }

}
