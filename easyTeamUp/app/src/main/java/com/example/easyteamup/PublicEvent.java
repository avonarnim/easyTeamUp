package com.example.easyteamup;

public class PublicEvent extends Event {
    public String type = "Public";

    public PublicEvent(int id, String name, String host, double latitude, double longitude, Long deadline, Long finalTime) {
        super(id, name, host, latitude, longitude, deadline, finalTime);
    }
}
