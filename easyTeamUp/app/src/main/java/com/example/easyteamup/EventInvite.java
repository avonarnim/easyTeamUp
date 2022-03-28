package com.example.easyteamup;

import java.util.List;

public class EventInvite extends Message {
    private List<String> guestList;
    public EventInvite(String sender, String recipient, String body) {
        super(sender, recipient, body);
    }

    public void addGuest(String user) {
        this.guestList.add(user);
    }

    public List<String> getGuestList() {
        return this.guestList;
    }
    public List<String> setGuestList(List<String> gl) {
        this.guestList = gl;
        return this.guestList;
    }
}
