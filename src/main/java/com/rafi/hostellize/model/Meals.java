package com.rafi.hostellize.model;

public class Meals {
    String Date;
    String ID;
    Double amount;

    public Meals(String date, String ID, Double amount) {
        Date = date;
        this.ID = ID;
        this.amount = amount;
    }

    public String getDate() {
        return Date;
    }

    public String getID() {
        return ID;
    }

    public Double getAmount() {
        return amount;
    }
}
