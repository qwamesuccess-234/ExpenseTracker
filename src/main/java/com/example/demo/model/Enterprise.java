package com.example.demo.model;

public class Enterprise extends User{

    private String enterpriseId;
    private int numberOfBranches;

    public Enterprise() {
        super();
    }

    public Enterprise(int numberOfBranches, String enterpriseId) {
        this.numberOfBranches = numberOfBranches;
        this.enterpriseId = enterpriseId;
    }

    public int getNumberOfBranches() {
        return numberOfBranches;
    }

    public void setNumberOfBranches(int numberOfBranches) {
        this.numberOfBranches = numberOfBranches;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
