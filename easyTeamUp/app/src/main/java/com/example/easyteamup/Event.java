package com.example.easyteamup;

import java.util.Date;
import java.util.List;

public class Event {

    private int id;
    private String name;
    private String host;
    private float latitude;
    private float longitude;
    private String deadline;
    private String finalTime;

    public Event(int id, String name, String host, float latitude, float longitude, String deadline, String finalTime) {
        this.name = name;
        this.host = host;
        this.latitude = latitude;
        this.longitude= longitude;
        this.deadline = deadline;
        this.finalTime = finalTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String n) {
        this.name = n;
    }

    public String getHost() {
        return this.host;
    }
    public void setHost(String h) {
        this.host = h;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(String finalTime) {
        this.finalTime = finalTime;
    }
}
