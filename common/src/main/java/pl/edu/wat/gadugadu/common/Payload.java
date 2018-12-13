package pl.edu.wat.gadugadu.common;

import java.util.List;

public class Payload {
    private PayloadType contentType;
    private int clientId;
    private String date;
    private String content;
    private UserStatus status;
    private Authentication authentication;
    private List<UserInfo> usersInfo;

    public Payload(PayloadType contentType, int clientId, String date, String content, UserStatus status, Authentication authentication, List<UserInfo> usersInfo) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.date = date;
        this.content = content;
        this.status = status;
        this.authentication = authentication;
        this.usersInfo = usersInfo;
    }

    public PayloadType getContentType() {
        return contentType;
    }

    public int getClientId() {
        return clientId;
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
}
