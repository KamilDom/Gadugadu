package pl.edu.wat.gadugadu.common;


import io.vertx.mqtt.MqttEndpoint;

public class UserInfo {
    private int clientId;
    private String defaultNick;
    private String avatarURI;
    private UserStatus userStatus;
    private MqttEndpoint endpoint;

    public UserInfo(int clientId, String defaultNick, String avatarURI, UserStatus userStatus, MqttEndpoint endpoint) {
        this.clientId = clientId;
        this.defaultNick = defaultNick;
        this.avatarURI = avatarURI;
        this.userStatus = userStatus;
        this.endpoint = endpoint;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getDefaultNick() {
        return defaultNick;
    }

    public void setDefaultNick(String defaultNick) {
        this.defaultNick = defaultNick;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public MqttEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(MqttEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}