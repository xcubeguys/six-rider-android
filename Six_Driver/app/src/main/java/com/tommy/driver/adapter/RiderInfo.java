package com.tommy.driver.adapter;

/**
 * Created by navneet on 2/6/16.
 */
public class RiderInfo {

    //Variables that are in our json
 /*   private int StudentId;
    private String StudentName;
    private int StudentMarks;*/
    private String firstname;
    private String lastname;
    private String email;
    private String profile_pic;
    private String mobile;
    private String rider_id;
    private String country_code;
    private String category;
    private String refrel_code;
    private String nick_name;

    private String vehicle_make;
    private String vehicle_model;
    private String vehicle_year;
    private String vehicle_mileage;
    private String number_plate;

    private String status;
    private String message;



    //Getters and setters


    public String getRider_id() {
        return rider_id;
    }

    public void setRider_id(String rider_id) {
        this.rider_id = rider_id;
    }
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String name) {
        this.firstname = name;
    }
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String name) {
        this.lastname = name;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String name) {
        this.email = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRefrel_code() {
        return refrel_code;
    }

    public void setRefrel_code(String refrel_code) {
        this.refrel_code = refrel_code;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getVehicle_make() {
        return vehicle_make;
    }

    public void setVehicle_make(String vehicle_make) {
        this.vehicle_make = vehicle_make;
    }

    public String getVehicle_model() {
        return vehicle_model;
    }

    public void setVehicle_model(String vehicle_model) {
        this.vehicle_model = vehicle_model;
    }

    public String getVehicle_year() {
        return vehicle_year;
    }

    public void setVehicle_year(String vehicle_year) {
        this.vehicle_year = vehicle_year;
    }

    public String getVehicle_mileage() {
        return vehicle_mileage;
    }

    public void setVehicle_mileage(String vehicle_mileage) {
        this.vehicle_mileage = vehicle_mileage;
    }

    public String getNumber_plate() {
        return number_plate;
    }

    public void setNumber_plate(String number_plate) {
        this.number_plate = number_plate;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
