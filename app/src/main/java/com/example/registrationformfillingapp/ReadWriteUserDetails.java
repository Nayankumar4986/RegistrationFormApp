package com.example.registrationformfillingapp;

public class ReadWriteUserDetails {

    public String fullname,dob,gender,mobile;

    public ReadWriteUserDetails(){};

    public ReadWriteUserDetails(String fullname,String dob, String gender, String mobile) {
        this.fullname = fullname;
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
    }
}
//package com.example.registrationformfillingapp;

//public class ReadWriteUserDetails {
//
//    private String fullname;
//    private String dob;
//    private String gender;
//    private String mobile;
//
//    public ReadWriteUserDetails() {
//        // Default constructor required for Firebase
//    }
//
//    public ReadWriteUserDetails(String fullname, String dob, String gender, String mobile) {
//        this.fullname = fullname;
//        this.dob = dob;
//        this.gender = gender;
//        this.mobile = mobile;
//    }
//
//    public String getFullname() {
//        return fullname;
//    }
//
//    public void setFullname(String fullname) {
//        this.fullname = fullname;
//    }
//
//    public String getDob() {
//        return dob;
//    }
//
//    public void setDob(String dob) {
//        this.dob = dob;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }
//}
