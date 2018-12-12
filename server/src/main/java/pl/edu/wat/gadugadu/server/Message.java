package pl.edu.wat.gadugadu.server;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


//TODO add relations between tables
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @SequenceGenerator(name = "seq", sequenceName = "message_seq", allocationSize = 1)
    @GeneratedValue(generator = "seq")
    private Long id;
    private Long senderId;
    private Long recipientId;
    private String messageContent;
    private LocalDate messageDate;

    public Message(Long senderId, Long recipientId, String messageContent, String messageDate) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageContent = messageContent;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss");
        this.messageDate = LocalDate.parse(messageDate, dtf);
    }

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public LocalDate getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(LocalDate messageDate) {
        this.messageDate = messageDate;
    }
}
