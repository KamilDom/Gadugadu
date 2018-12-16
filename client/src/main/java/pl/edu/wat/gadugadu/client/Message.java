package pl.edu.wat.gadugadu.client;

import java.time.LocalDate;

public class Message {
    private int clientId;
    private String messageContent;
    private String messageDate;

    public Message(int clientId, String messageContent, String messageDate) {
        this.clientId = clientId;
        this.messageContent = messageContent;
        this.messageDate = messageDate;
    }

    public int getClientId() {
        return clientId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getMessageDate() {
        return messageDate;
    }
}
