package com.rafi.hostellize.model;

public class Transactions {
    String id;
    String date;
    double amount;
    String type;
    String notes;

    public Transactions(String id, String date, double amount, String type, String notes) {
        this.id = id;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }
    public String getNotes() {
        return notes;
    }
}
