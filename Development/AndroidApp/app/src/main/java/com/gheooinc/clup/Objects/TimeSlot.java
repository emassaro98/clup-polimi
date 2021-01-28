package com.gheooinc.clup.Objects;

public class TimeSlot {
    private int idShop, idUp, idDown;
    private String time, duration;

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getIdShop() {
        return idShop;
    }

    public void setIdShop(int idShop) {
        this.idShop = idShop;
    }

    public int getIdUp() {
        return idUp;
    }

    public void setIdUp(int idUp) {
        this.idUp = idUp;
    }

    public int getIdDown() {
        return idDown;
    }

    public void setIdDown(int idDown) {
        this.idDown = idDown;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
