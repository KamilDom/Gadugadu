package pl.edu.wat.gadugadu.common;


import io.vertx.mqtt.MqttEndpoint;

public class UserInfo {
    private transient String clientIdentifier;
    private int clientId;
    private String defaultNick;
    private String avatarURI;
    private UserStatus userStatus;
    private transient MqttEndpoint endpoint;

    public UserInfo(String clientIdentifier, int clientId, String defaultNick, String avatarURI, UserStatus userStatus, MqttEndpoint endpoint) {
        this.clientIdentifier = clientIdentifier;
        this.clientId = clientId;
        this.defaultNick = defaultNick;
        this.avatarURI = avatarURI;
        this.userStatus = userStatus;
        this.endpoint = endpoint;
    }

    public UserInfo(int clientId, String defaultNick, UserStatus userStatus) {
        this.clientId = clientId;
        this.defaultNick = defaultNick;
        this.userStatus = userStatus;
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
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