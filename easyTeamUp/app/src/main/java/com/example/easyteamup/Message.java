package com.example.easyteamup;

public class Message {

    private String sender;
    private String recipient;
    private String body;

    public Message(String sender, String recipient, String body) {
        this.sender = sender;
        this.recipient = recipient;
        this.body = body;
    }
}
