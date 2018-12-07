package pl.edu.wat.gadugadu.client;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import pl.edu.wat.gadugadu.common.ClientStatus;
import pl.edu.wat.gadugadu.common.JsonParser;

import java.net.ConnectException;
import java.nio.charset.Charset;

public class Client {
    private int port;
    private String host;
    private String topic;
    private MqttClientOptions options;
    private MqttClient client;
    private MainController controller;
    private JsonParser jsonParser;

    public Client(int port, String host, String topic, MainController controller) {
        this.port = port;
        this.host = host;
        this.topic = topic;
        this.controller = controller;
        jsonParser = new JsonParser();

        MqttClientOptions options = new MqttClientOptions().setKeepAliveTimeSeconds(30);

        client = MqttClient.create(Vertx.vertx(), options);

        client.publishHandler(publish -> {
            System.out.println("Just received message on [" + publish.topicName() + "] payload [" + publish.payload().toString(Charset.defaultCharset()) + "] with QoS [" + publish.qosLevel() + "]");
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
                Buffer.buffer(jsonParser.makePayload(1,"aa",message)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void changeStatus(ClientStatus status) {
        client.publish(
                topic,
                Buffer.buffer(jsonParser.makePayload(0,"aa",String.valueOf(status.value()))),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }


}
