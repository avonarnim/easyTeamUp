package com.example.easyteamup;

import java.util.List;

public class Profile {
    String username;
    byte[] picture;
    List<Event> pastEvents;
    List<Event> futureEvents;
    List<Event> currentlyHosting;
    List<Message> messages;

    public Profile(String username, byte[] picture, List<Event> pastEvents, List<Event> futureEvents, List<Event> currentlyHosting, List<Message> messages) {
        this.username = username;
        this.picture = picture;
        this.pastEvents = pastEvents;
        this.futureEvents = futureEvents;
        this.currentlyHosting = currentlyHosting;
        this.messages = messages;
    }

    public String getUsername() {
        return username;
    }

    public List<Event> getPastEvents() {
        return pastEvents;
    }

    public List<Event> getCurrentlyHosting() {
        return currentlyHosting;
    }

    public List<Event> getFutureEvents() {
        return futureEvents;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
