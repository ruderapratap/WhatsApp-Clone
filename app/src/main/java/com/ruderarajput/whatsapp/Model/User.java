package com.ruderarajput.whatsapp.Model;

public class User
{
    private String uid;
    private String name;
    private String about;
    private String profileImage;
    private String phoneNumber;

    public User(String name,String about, String phoneNumber) {
        this.name = name;
        this.about=about;
        this.phoneNumber = phoneNumber;
    }

    public User() {
    }

    public User(String uid, String name,String about, String profileImage, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.about=about;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
