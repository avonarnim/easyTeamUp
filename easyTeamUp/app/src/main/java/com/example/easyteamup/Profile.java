package com.example.easyteamup;

import java.util.List;

public class Profile {
    Integer userId;
    String username;
    List<Integer> pastEvents;
    List<Integer> futureEvents;
    List<Integer> currentlyHosting;
    List<Message> messages;

    public Profile(Integer id, String username, List<Integer> pastEvents, List<Integer> futureEvents, List<Integer> currentlyHosting, List<Message> messages) {
        this.userId = id;
        this.username = username;
        this.pastEvents = pastEvents;
        this.futureEvents = futureEvents;
        this.currentlyHosting = currentlyHosting;
        this.messages = messages;
    }

}
