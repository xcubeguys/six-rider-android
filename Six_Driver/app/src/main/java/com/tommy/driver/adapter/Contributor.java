package com.tommy.driver.adapter;

/**
 * Created by test on 28/3/17.
 */
public class Contributor {
    private String status,percentage,company_name,company_fee;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status= status;
    }

    public String getCompanyName() {
        return company_name;
    }

    public void setCompanyName(String company_name) {
        this.company_name= company_name;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage= percentage;
    }

    public String getCompanyFee() {
        return company_fee;
    }

    public void company_fee(String company_fee) {
        this.company_fee= company_fee;
    }
}
