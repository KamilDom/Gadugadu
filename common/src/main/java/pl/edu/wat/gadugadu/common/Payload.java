package pl.edu.wat.gadugadu.common;

import java.nio.ByteBuffer;
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
    private Registration registration;
    private byte[] fileContent;
    private ImageStatus imageStatus;

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
    public Payload(PayloadType contentType, UserStatus status, Authentication authentication) {
        this.contentType = contentType;
        this.status = status;
        this.authentication = authentication;
    }

    //login response
    public Payload(PayloadType contentType, Authentication authentication) {
        this.contentType = contentType;
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

    //registration
    public Payload(PayloadType contentType, Registration registration){
        this.contentType = contentType;
        this.registration = registration;
    }

    //image
    public Payload(PayloadType contentType, Integer clientId, ImageStatus imageStatus, byte[] fileContent){
        this.contentType = contentType;
        this.clientId = clientId;
        this.fileContent = fileContent;
        this.imageStatus = imageStatus;
    }

    //image
    public Payload(PayloadType contentType, int clientId, ImageStatus imageStatus) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.imageStatus = imageStatus;
    }

    //ready status
    public Payload(PayloadType contentType) {
        this.contentType = contentType;
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

    public Registration getRegistration() {
        return registration;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public ImageStatus getImageStatus() {
        return imageStatus;
    }
}
