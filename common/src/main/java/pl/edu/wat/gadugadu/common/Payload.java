package pl.edu.wat.gadugadu.common;

import java.util.List;

public class Payload {
    private PayloadType contentType;
    private Integer clientId;
    private Integer destinationId;
    private String date;
    private String content;
    private UserStatus status;
    private Authentication authentication;
    private List<UserInfo> usersInfo;
    private UserInfo userInfo;

    public Payload(PayloadType contentType, Integer clientId, String date, String content, UserStatus status, Authentication authentication, List<UserInfo> usersInfo) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.date = date;
        this.content = content;
        this.status = status;
        this.authentication = authentication;
        this.usersInfo = usersInfo;
    }

    //status
    public Payload(PayloadType contentType, Integer clientId, UserStatus status) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.status = status;
    }

    //message
    public Payload(PayloadType contentType, Integer clientId, Integer destinationId, String date, String content) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.destinationId = destinationId;
        this.date = date;
        this.content = content;
    }

    //login
    public Payload(PayloadType contentType, Integer clientId, UserStatus status, Authentication authentication) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.status = status;
        this.authentication = authentication;
    }

    //online users list
    public Payload(PayloadType contentType, List<UserInfo> onlineUsers) {
        this.contentType = contentType;
        this.usersInfo = onlineUsers;
    }

    // new connected client
    public Payload(PayloadType contentType, UserInfo userInfo) {
        this.contentType = contentType;
        this.userInfo = userInfo;
    }


    public PayloadType getContentType() {
        return contentType;
    }

    public Integer getClientId() {
        return clientId;
    }

    public Integer getDestinationId() {
        return destinationId;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public List<UserInfo> getUsersInfo() {
        return usersInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
