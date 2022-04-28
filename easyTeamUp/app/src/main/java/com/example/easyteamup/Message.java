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
    //maybe make a method with available time slots and radio button

    public String getBody() {
        return body;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
