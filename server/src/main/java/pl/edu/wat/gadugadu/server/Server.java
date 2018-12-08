package pl.edu.wat.gadugadu.server;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import pl.edu.wat.gadugadu.common.Payload;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {
    private int port;
    private String host;
    private String topic;
    private MainController controller;
    private MqttServer mqttServer;
    private List<MqttEndpoint> connectedClients =new ArrayList();
    private Gson gson;
    private DateFormat dateFormat;

    public Server(int port, String host, String topic, MainController controller) {
        this.port = port;
        this.host = host;
        this.topic = topic;
        this.controller=controller;
        gson = new Gson();
        dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        mqttServer = MqttServer.create(Vertx.vertx());

        mqttServer
                .endpointHandler(endpoint -> {

                    // shows main connect info
                    System.out.println("MQTT client [" + endpoint.clientIdentifier() + "] request to connect, clean session = " + endpoint.isCleanSession());

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


                        connectedClients.add(endpoint);

                        controller.updateConnectedClientsCount(connectedClients.size());

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
                        connectedClients.remove(endpoint);
                        controller.updateConnectedClientsCount(connectedClients.size());

                    });

                    // handling closing connection
                    endpoint.closeHandler(v -> {
                        System.out.println("Connection closed");
                        connectedClients.remove(endpoint);
                        controller.updateConnectedClientsCount(connectedClients.size());

                    });

                    // handling incoming published messages
                    endpoint.publishHandler(message -> {

                        System.out.println("Just received message on [" + message.topicName() + "] payload [" + message.payload() + "] with QoS [" + message.qosLevel() + "]");

                        publishMessage(message.payload().toString());

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

    public int getConnectedClientsrCount(){
        return connectedClients.size();
    }

    public void publishMessage(String message){
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
                    Buffer.buffer(gson.toJson(new Payload(1, 0, dateFormat.format(new Date()), message, null), Payload.class)),
                    MqttQoS.AT_MOST_ONCE,
                    false,
                    false);
        }
    }
}
