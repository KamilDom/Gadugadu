package pl.edu.wat.gadugadu.server;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import pl.edu.wat.gadugadu.common.PayloadType;
import pl.edu.wat.gadugadu.common.UserInfo;
import pl.edu.wat.gadugadu.common.UserStatus;
import pl.edu.wat.gadugadu.common.Payload;

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

                    // handling ping from client
                   /*   endpoint.pingHandler(v -> {

                        System.out.println("Ping received from client");
                    });*/

                    // handling disconnect message
                    endpoint.disconnectHandler(v -> {
                        System.out.println("Received disconnect from client");
                        connectedClients--;
                        controller.updateConnectedClientsCount(connectedClients);
                        onlineUsers.stream()
                                .filter(userInfo -> userInfo.getClientIdentifier().equals(endpoint.clientIdentifier()))
                                .findAny()
                                .ifPresentOrElse(userInfo -> onlineUsers.remove(userInfo), () -> System.out.println("User not found"));
                        publishClientsList();
                    });

                    // handling closing connection
                    endpoint.closeHandler(v -> {
                        System.out.println("Connection closed");
                        connectedClients--;
                        controller.updateConnectedClientsCount(connectedClients);
                        onlineUsers.stream()
                                .filter(userInfo -> userInfo.getClientIdentifier().equals(endpoint.clientIdentifier()))
                                .findAny()
                                .ifPresentOrElse(userInfo -> onlineUsers.remove(userInfo), () -> System.out.println("User not found"));
                        publishClientsList();
                    });

                    // handling incoming published messages
                    endpoint.publishHandler(message -> {

                        System.out.println("Just received message on [" + message.topicName() + "] payload [" + message.payload() + "] with QoS [" + message.qosLevel() + "]");

                        Payload payload = gson.fromJson(message.payload().toString(), Payload.class);

                        switch(payload.getContentType()){
                            case AUTHENTICATION: {
                               // endpoint.accept(true);
                                System.out.println("tess");
                                onlineUsers.add(new UserInfo(endpoint.clientIdentifier(), payload.getClientId(), "Edek", "", payload.getStatus(), endpoint));
                                publishClientsList();
                            }
                                break;
                            case STATUS_UPDATE:
                                onlineUsers.stream()
                                        .filter(userInfo -> userInfo.getClientId()==payload.getClientId())
                                        .findAny()
                                        .ifPresentOrElse(userInfo -> userInfo.setUserStatus(payload.getStatus()), () -> System.out.println("User not found"));

                                publishClientsList();
                                break;
                            case ONLINE_USERS_LIST:
                                publishClientsList();
                                break;
                            case MESSAGE:

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


  /*  public void publishMessage(String message){
        for (MqttEndpoint endpoint : connectedClients) {
            endpoint.publish(topic,
                    Buffer.buffer(message),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }
    }


    public void publishTestMessage(String message) {
        for (MqttEndpoint endpoint : connectedClients) {
            endpoint.publish(topic,
                    Buffer.buffer(gson.toJson(new Payload(PayloadType.MESSAGE.value(), 0, dateFormat.format(new Date()), message, null, null, null), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }
    }
*/
    public void publishClientsList(){
        System.out.println(onlineUsers.size());
       // for (Map.Entry<Integer, UserInfo> userInfo: onlineUsers.entrySet()) {
        for(UserInfo userInfo: onlineUsers){
                userInfo.getEndpoint().publish(
                        topic,
                        Buffer.buffer(gson.toJson(new Payload(PayloadType.ONLINE_USERS_LIST, null, null, null,
                                null, null, onlineUsers), Payload.class)),
                        MqttQoS.AT_MOST_ONCE,
                        false,
                        false);

        }
    }

    //TODO add db methods
}
