package com.example.demo.model;

public class Budget {
    private int id;
    private int userId;
    private String period; // "Monthly" or "Weekly"
    private double limitAmount;

    public Budget() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public double getLimitAmount() { return limitAmount; }
    public void setLimitAmount(double limitAmount) { this.limitAmount = limitAmount; }
}