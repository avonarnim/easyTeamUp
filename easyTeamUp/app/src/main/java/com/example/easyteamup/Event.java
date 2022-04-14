package com.example.easyteamup;

import java.util.Date;

public class Event {

    private int id;
    private String name;
    private String host;
    private double latitude;
    private double longitude;
    private Long deadline;
    private Long finalTime;
    private String type;

    public Event(int id, String name, String host, double latitude, double longitude, Long deadline, Long finalTime) {
        this.id = id;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public Long getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(Long finalTime) {
        this.finalTime = finalTime;
    }

    public void makePublic() {
        this.type = "Public";
    }
    public void makePrivate() {
        this.type = "Private";
    }

    public String getType() {
        return this.type;
    }
}
