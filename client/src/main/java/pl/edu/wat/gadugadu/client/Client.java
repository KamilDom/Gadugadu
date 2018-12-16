package pl.edu.wat.gadugadu.client;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import pl.edu.wat.gadugadu.common.*;

import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Client {
    private int port;
    private String host;
    private String topic;
    private MqttClientOptions options;
    private MqttClient client;
    private Gson gson;
    private DateFormat dateFormat;
    private UserStatus status;

    // tymczasowe rozwiazania
    Random r = new Random();
    public int clientId=r.nextInt(99)+1;
    //public int clientId=66;

    public Client(int port, String host, String topic) {
        this.port = port;
        this.host = host;
        this.topic = topic;
        gson = new Gson();
        dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        status = UserStatus.AVAILABLE;

        options = new MqttClientOptions().setKeepAliveTimeSeconds(30);

        client = MqttClient.create(Vertx.vertx(), options);

        client.publishHandler(publish -> {
            System.out.println("Just received message on [" + publish.topicName() + "] payload [" + publish.payload().toString(Charset.defaultCharset()) + "] with QoS [" + publish.qosLevel() + "]");
            Payload payload = gson.fromJson(publish.payload().toString(), Payload.class);

            switch(payload.getContentType()){
                case NEW_CLIENT_CONNECTED:
                    Main.mainController.addToContactList(payload.getUserInfo());
                    break;
                case STATUS_UPDATE:
                    Main.mainController.updateContactStatus(payload);
                    break;
                case ONLINE_USERS_LIST:
                    Main.mainController.showContactsList(payload.getUsersInfo());
                    break;
                case MESSAGE:
                    Main.mainController.addToMessagesList(payload);
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

    public UserStatus getStatus() {
        return status;
    }

    public void login(String login, String password) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.AUTHENTICATION, clientId, UserStatus.AVAILABLE, new Authentication(login,password)), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
        client.subscribe(topic, 0);
        client.subscribe(String.valueOf(clientId), 0);
    }

    public void publishMessage(String message, Integer destinationId) {
        client.publish(
                String.valueOf(destinationId),
                Buffer.buffer(gson.toJson(new Payload(PayloadType.MESSAGE, clientId, destinationId, dateFormat.format(new Date()), message), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void changeStatus(UserStatus status) {
        this.status=status;
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.STATUS_UPDATE, clientId, status), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }


}
