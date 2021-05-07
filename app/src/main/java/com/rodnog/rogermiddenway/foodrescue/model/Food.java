package com.rodnog.rogermiddenway.foodrescue.model;

import java.sql.Time;
import java.util.Date;

public class Food {
    private int food_id;
    private int user_id;
    private String image_path;
    private String title;
    private String description;
    private long date;
    private long time;
    private int quantity;
    private String location;

    public Food() {

    }

    public int getFood_id() {
        return food_id;
    }

    public void setFood_id(int food_id) {
        this.food_id = food_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getImage_path() { return image_path; }

    public void setImage_path(String image_path) { this.image_path = image_path; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public String getShortDescription() {
        int maxLength = 40;
        if(description.length() < maxLength) return description;
        else return description.substring(0, 60) + "...";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time.getTime();

    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
