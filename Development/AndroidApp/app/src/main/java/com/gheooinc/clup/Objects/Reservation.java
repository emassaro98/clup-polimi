package com.gheooinc.clup.Objects;

public class Reservation {
    //Vars
    private String shopName, attemptTime, shopAddress, date;
    private boolean booking;
    private int id;

    //Getter and setter methods
    public String getAttemptTime() {
        return attemptTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAttemptTime(String attemptTime) {
        this.attemptTime = attemptTime;
    }

    public String getShopName() {
        return shopName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public boolean isBooking() {
        return booking;
    }

    public void setBooking(boolean booking) {
        this.booking = booking;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

}
