package com.example.easyteamup;
import java.util.List;

import java.util.List;

public class PrivateEvent extends Event {
    private List<String> guestList;
    public String type = "Private";

    public PrivateEvent(int id, String name, String host, double latitude, double longitude, Long deadline, Long finalTime) {
        super(id, name, host, latitude, longitude, deadline, finalTime, "Private");
    }
    public List<String> getGuestList() {
        return this.guestList;
    }
    public List<String> setGuestList(List<String> gl) {
        this.guestList = gl;
        return this.guestList;
    }
}
