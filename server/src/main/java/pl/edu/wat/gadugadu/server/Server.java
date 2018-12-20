package pl.edu.wat.gadugadu.server;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import javafx.collections.FXCollections;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import pl.edu.wat.gadugadu.common.*;
import pl.edu.wat.gadugadu.server.database.User;

import javax.persistence.NoResultException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Server {
    private int port;
    private int connectedClients;
    private String host;
    private String topic;
    private MainController controller;
    private MqttServer mqttServer;
    private Gson gson;
    private List<UserInfo> onlineUsers;

    public Server(int port, String host, String topic, MainController controller) {
        this.port = port;
        this.host = host;
        this.topic = topic;
        this.controller = controller;
        gson = new Gson();
        onlineUsers = new ArrayList<>();
        connectedClients=0;
        mqttServer = MqttServer.create(Vertx.vertx());


        mqttServer
                .endpointHandler(endpoint -> {

                    // shows main connect info
                    System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());
                        connectedClients++;
                        controller.updateConnectedClientsCount(connectedClients);

                    if (endpoint.auth() != null) {
                        System.out.println("[username = " + endpoint.auth().userName() + ", password = " + endpoint.auth().password() + "]");
                    }
                    if (endpoint.will() != null) {
                        System.out.println("[will flag = " + endpoint.will().isWillFlag() + " topic = " + endpoint.will().willTopic() + " msg = " + endpoint.will().willMessage() +
                                " QoS = " + endpoint.will().willQos() + " isRetain = " + endpoint.will().isWillRetain() + "]");
                    }

                    System.out.println("[keep alive timeout = " + endpoint.keepAliveTimeSeconds() + "]");

                    // accept connection from the remote client
                    endpoint.accept(false);

                    // handling requests for subscriptions
                    endpoint.subscribeHandler(subscribe -> {

                        List<MqttQoS> grantedQosLevels = new ArrayList<>();
                        for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
                            System.out.println("Subscription for " + s.topicName() + " with QoS " + s.qualityOfService());
                            grantedQosLevels.add(s.qualityOfService());
                        }
                        // ack the subscriptions request
                        endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);



                        // specifing handlers for handling QoS 1 and 2
                        endpoint.publishAcknowledgeHandler(messageId -> {

                            System.out.println("Received ack for message = " + messageId);

                        }).publishReceivedHandler(messageId -> {

                            endpoint.publishRelease(messageId);

                        }).publishCompletionHandler(messageId -> {

                            System.out.println("Received ack for message = " + messageId);
                        });
                    });

                    // handling requests for unsubscriptions
                    endpoint.unsubscribeHandler(unsubscribe -> {

                        for (String t : unsubscribe.topics()) {
                            System.out.println("Unsubscription for " + t);
                        }
                        // ack the subscriptions request
                        endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
                    });


                    // handling disconnect message
                    endpoint.disconnectHandler(v -> {
                        System.out.println("Received disconnect from client");
                        connectedClients--;
                        controller.updateConnectedClientsCount(connectedClients);
                        onlineUsers.stream()
                                .filter(userInfo -> userInfo.getClientIdentifier().equals(endpoint.clientIdentifier()))
                                .findAny()
                                .ifPresentOrElse(userInfo -> {
                                    userInfo.setUserStatus(UserStatus.OFFLINE);
                                    onlineUsers.remove(userInfo);
                                    publishStatusUpdate(userInfo);
                                }, () -> System.out.println("User not found"));
                    });

                    // handling closing connection
                    endpoint.closeHandler(v -> {
                        System.out.println("Connection closed");
                        connectedClients--;
                        controller.updateConnectedClientsCount(connectedClients);
                        onlineUsers.stream()
                                .filter(userInfo -> userInfo.getClientIdentifier().equals(endpoint.clientIdentifier()))
                                .findAny()
                                .ifPresentOrElse(userInfo -> {
                                    userInfo.setUserStatus(UserStatus.OFFLINE);
                                    onlineUsers.remove(userInfo);
                                    publishStatusUpdate(userInfo);
                                }, () -> System.out.println("User not found"));
                    });

                    // handling incoming published messages
                    endpoint.publishHandler(message -> {

                        System.out.println("Just received message on [" + message.topicName() + "] payload [" + message.payload() + "] with QoS [" + message.qosLevel() + "]");

                        Payload payload = gson.fromJson(message.payload().toString(), Payload.class);

                        switch(payload.getContentType()){
                            case REGISTRATION:
                                User newUser = new User(payload.getRegistration().getName(), payload.getRegistration().getPassword());
                                    try(Session session = Main.getSession()){
                                        session.beginTransaction();

                                        session.save(newUser);

                                        session.getTransaction().commit();
                                        session.close();
                                    }
                                    publishRegistrationStatus(endpoint, newUser.getId());
                                break;
                            case AUTHENTICATION:
                                try(Session session = Main.getSession()) {
                                    session.beginTransaction();
                                    String query = "from User where id="+payload.getAuthentication().getId();
                                    User user = session.createQuery(query, User.class).getSingleResult();
                                    session.getTransaction().commit();
                                    session.close();

                                    if(user.getPassword().equals(payload.getAuthentication().getPassword())){
                                        publishLoginStatus(endpoint,user.getName());
                                        UserInfo u = new UserInfo(endpoint.clientIdentifier(), payload.getAuthentication().getId(), user.getName(), "", payload.getStatus(), endpoint);
                                        publishNewConnectedClientInfo(u);
                                        publishClientsList(endpoint);
                                        onlineUsers.add(u);
                                    } else {
                                        publishLoginStatus(endpoint,null);
                                    }
                                } catch (NoResultException e){
                                    publishLoginStatus(endpoint,null);
                                }

                                break;
                            case NEW_CLIENT_CONNECTED:

                                break;
                            case STATUS_UPDATE:
                                onlineUsers.stream()
                                        .filter(userInfo -> userInfo.getClientId()==payload.getClientId())
                                        .findAny()
                                        .ifPresentOrElse(userInfo -> {userInfo.setUserStatus(payload.getStatus());
                                                publishStatusUpdate(userInfo);
                                                },
                                                () -> System.out.println("User not found"));

                                break;
                            case ONLINE_USERS_LIST:

                                break;
                            case MESSAGE:
                                onlineUsers.stream()
                                        .filter(userInfo -> userInfo.getClientId()==payload.getDestinationId())
                                        .findAny()
                                        .ifPresentOrElse(userInfo -> userInfo.getEndpoint().publish(
                                                topic,
                                                Buffer.buffer(gson.toJson(new Payload(PayloadType.MESSAGE, payload.getClientId(),  payload.getDestinationId(), payload.getDate(),
                                                        payload.getContent()), Payload.class)),
                                                MqttQoS.AT_MOST_ONCE,
                                                false,
                                                false), () -> System.out.println("User not found"));
                                onlineUsers.stream()
                                        .filter(userInfo -> userInfo.getClientId()==payload.getClientId())
                                        .findAny()
                                        .ifPresentOrElse(userInfo -> userInfo.getEndpoint().publish(
                                                topic,
                                                Buffer.buffer(gson.toJson(new Payload(PayloadType.MESSAGE, payload.getClientId(),  payload.getDestinationId(), payload.getDate(),
                                                        payload.getContent()), Payload.class)),
                                                MqttQoS.AT_MOST_ONCE,
                                                false,
                                                false), () -> System.out.println("User not found"));
                                break;
                            case IMAGE:

                                break;

                            default:
                               //
                        }


                        if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                            endpoint.publishAcknowledge(message.messageId());
                        } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                            endpoint.publishReceived(message.messageId());
                        }

                    }).publishReleaseHandler(messageId -> {
                        endpoint.publishComplete(messageId);
                    });
                });
    }

    public void starServer(){
        mqttServer.listen(port, host, ar -> {
            if (ar.succeeded()) {
                System.out.println("MQTT server is listening on port " + mqttServer.actualPort());
                controller.updateStatus("Server is running...");
            } else {
                System.err.println("Error on starting the server" + ar.cause().getMessage());
                controller.updateStatus("Error on starting the server");
            }
        });
    }

    public void stopServer(){
        mqttServer.close(v -> {
            System.out.println("MQTT server closed");
            controller.updateStatus("Server is stopped");
        });
    }


    public void publishClientsList(MqttEndpoint endpoint){
        endpoint.publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.ONLINE_USERS_LIST, onlineUsers), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
    }

    public void publishNewConnectedClientInfo(UserInfo newConnectedClientInfo){

        for(UserInfo userInfo: onlineUsers){
            userInfo.getEndpoint().publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.NEW_CLIENT_CONNECTED, newConnectedClientInfo), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }
    }

    public void publishStatusUpdate(UserInfo updatedUser){
        for(UserInfo userInfo: onlineUsers){
            userInfo.getEndpoint().publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.STATUS_UPDATE, updatedUser.getClientId(),updatedUser.getUserStatus()), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }

    }

    public void publishRegistrationStatus(MqttEndpoint endpoint, Integer id){
        if(id != null){
            endpoint.publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.REGISTRATION, new Registration(id, RegistrationStatus.REGISTRATION_SUCCESSFUL)), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        } else {
            endpoint.publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.REGISTRATION, new Registration(id, RegistrationStatus.REGISTRATION_ERROR)), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }

    }


    public void publishLoginStatus(MqttEndpoint endpoint, String name) {
        if (name!=null) {
            endpoint.publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.AUTHENTICATION, new Authentication(name, AuthenticationStatus.SUCCESSFUL)), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        } else {
            endpoint.publish(
                    topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.AUTHENTICATION, new Authentication(AuthenticationStatus.ERROR)), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }
    }

        //TODO add db methods
}
