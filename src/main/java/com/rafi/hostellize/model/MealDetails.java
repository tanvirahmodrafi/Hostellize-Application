package com.rafi.hostellize.model;

public class MealDetails {
    String mealName;
    String price;
    String date;
    String numberOfMael;
    String imageUrl;

    public MealDetails(String date, String mealName, String numberOfMael, String price, String imageUrl) {
        this.date = date;
        this.mealName = mealName;
        this.numberOfMael = numberOfMael;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public MealDetails(String date, String mealName, String numberOfMael, String price) {
        this.date = date;
        this.mealName = mealName;
        this.numberOfMael = numberOfMael;
        this.price = price;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNumberOfMael() {
        return numberOfMael;
    }

    public void setNumberOfMael(String numberOfMael) {
        this.numberOfMael = numberOfMael;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
