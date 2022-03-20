package com.example.easyteamup;

import java.util.List;

public class Profile {
    String username;
    List<Event> pastEvents;
    List<Event> futureEvents;
    List<Event> currentlyHosting;
    List<Message> messages;

    public Profile(String username, List<Event> pastEvents, List<Event> futureEvents, List<Event> currentlyHosting, List<Message> messages) {
        this.username = username;
        this.pastEvents = pastEvents;
        this.futureEvents = futureEvents;
        this.currentlyHosting = currentlyHosting;
        this.messages = messages;
    }

}
