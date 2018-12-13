package pl.edu.wat.gadugadu.client;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import pl.edu.wat.gadugadu.client.controllers.MainController;
import pl.edu.wat.gadugadu.common.PayloadType;
import pl.edu.wat.gadugadu.common.UserStatus;
import pl.edu.wat.gadugadu.common.Payload;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private int port;
    private String host;
    private String topic;
    private MqttClientOptions options;
    private MqttClient client;
    private MainController controller;
    private Gson gson;
    private DateFormat dateFormat;
    private UserStatus status;


    // tymczasowe rozwiazania
    public int clientId=2;

    public Client(int port, String host, String topic, MainController controller) {
        this.port = port;
        this.host = host;
        this.topic = topic;
        this.controller = controller;
        gson = new Gson();
        dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        options = new MqttClientOptions().setKeepAliveTimeSeconds(30);

        client = MqttClient.create(Vertx.vertx(), options);

        client.publishHandler(publish -> {
            System.out.println("Just received message on [" + publish.topicName() + "] payload [" + publish.payload().toString(Charset.defaultCharset()) + "] with QoS [" + publish.qosLevel() + "]");
            controller.showMessage(publish.payload().toString());
            controller.updateClientsList(publish.payload().toString());
        });

        client.closeHandler(event -> {
            //TODO trzeba tu bedzie obsłużyć padnięcie serwera
            System.out.println("Server down");
        });

        }

    public void connect(){
            //TODO dodanie obługi wyjątku w przypadku niepowodzenia połączenia
       client.connect(port, host, ch -> {
            if (ch.succeeded()) {
                System.out.println("Connected to a server");
                client.subscribe(topic, 0);
                status= UserStatus.AVAILABLE;
            } else {
                System.out.println("Failed to connect to a server");
                //System.out.println(ch.cause());
            }
        });
    }

    public void disconnect(){
        client.disconnect();
    }

    public boolean isConnected(){
        return client.isConnected();
    }

    public void publishMessage(String message) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.MESSAGE.value(), clientId,
                        dateFormat.format(new Date()), message, null, null, null), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void changeStatus(UserStatus status) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.STATUS_UPDATE.value(), clientId,
                        dateFormat.format(new Date()), null, status, null, null), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
