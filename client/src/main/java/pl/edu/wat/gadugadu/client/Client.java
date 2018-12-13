package pl.edu.wat.gadugadu.client;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import javafx.fxml.FXMLLoader;
import pl.edu.wat.gadugadu.common.*;

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
    private Gson gson;
    private DateFormat dateFormat;

    // tymczasowe rozwiazania
    public int clientId=75;

    public Client(int port, String host, String topic) {
        this.port = port;
        this.host = host;
        this.topic = topic;
        gson = new Gson();
        dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        options = new MqttClientOptions().setKeepAliveTimeSeconds(30);

        client = MqttClient.create(Vertx.vertx(), options);

        client.publishHandler(publish -> {
            System.out.println("Just received message on [" + publish.topicName() + "] payload [" + publish.payload().toString(Charset.defaultCharset()) + "] with QoS [" + publish.qosLevel() + "]");
            Payload payload = gson.fromJson(publish.payload().toString(), Payload.class);

            switch(payload.getContentType()){
                case STATUS_UPDATE:

                    break;
                case ONLINE_USERS_LIST:
                    Main.mainController.updateClientsList(payload.getUsersInfo());
                    break;
                case MESSAGE:
                    Main.mainController.showMessage(payload);
                    break;
                case IMAGE:

                    break;

                default:
                    //
            }




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

    public void login(String login, String password) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.AUTHENTICATION, clientId,
                        dateFormat.format(new Date()), null, UserStatus.AVAILABLE, new Authentication(login,password),null), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
        client.subscribe(topic, 0);
    }

    public void publishMessage(String message) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.MESSAGE, clientId,
                        dateFormat.format(new Date()), message, null, null, null), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void changeStatus(UserStatus status) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.STATUS_UPDATE, clientId,
                        dateFormat.format(new Date()), null, status, null, null), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }


}
