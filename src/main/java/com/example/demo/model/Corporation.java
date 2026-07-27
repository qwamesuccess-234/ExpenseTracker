package com.example.demo.model;

public class Corporation extends User{

    private String companyRegNo;
    private String industry;

    public Corporation() {
        super();
    }

    public Corporation(String industry, String companyRegNo) {
        this.industry = industry;
        this.companyRegNo = companyRegNo;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompanyRegNo() {
        return companyRegNo;
    }

    public void setCompanyRegNo(String companyRegNo) {
        this.companyRegNo = companyRegNo;
    }
}
