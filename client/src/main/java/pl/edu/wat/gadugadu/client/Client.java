package pl.edu.wat.gadugadu.client;

import com.google.gson.Gson;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import pl.edu.wat.gadugadu.common.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private int connectionTimeout = 500; //5s
    public int clientId;
    Path tempDir;
    Path tempFile;
    private FileOutputStream fop;

    public Client(int port, String host, String topic){
        this.port = port;
        this.host = host;
        this.topic = topic;
        gson = new Gson();
        dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

        try {
            Path basePath = Paths.get(System.getProperty("java.io.tmpdir")+"/Gagugadu");
            basePath.toFile().mkdir();
            basePath.toFile().deleteOnExit();
            tempDir= Files.createTempDirectory(basePath,"gg-files-");
            tempDir.toFile().deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        status = UserStatus.AVAILABLE;

        options = new MqttClientOptions().setKeepAliveTimeSeconds(30);

        client = MqttClient.create(Vertx.vertx(), options);

        client.exceptionHandler(event -> System.out.println("test"));

        client.publishHandler(publish -> {
            System.out.println("Just received message on [" + publish.topicName() + "] payload [" + publish.payload().toString(Charset.defaultCharset()) + "] with QoS [" + publish.qosLevel() + "]");
            Payload payload = gson.fromJson(publish.payload().toString(), Payload.class);

            switch (payload.getContentType()) {
                case REGISTRATION:
                    if(payload.getRegistration().getRegistrationStatus()==RegistrationStatus.REGISTRATION_SUCCESSFUL) {
                        Main.registerController.showSuccesfulDialog(payload.getRegistration().getNewId());
                        clientId = payload.getRegistration().getNewId();
                        try {
                            Main.registerController.sendImage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case AUTHENTICATION:
                    switch (payload.getAuthentication().getAuthenticationStatus()) {
                        case SUCCESSFUL:
                            Main.loginController.loadMainStage();
                            Main.loginController.closeLoginStage();
                            synchronized (this) {
                                try {
                                    wait();
                                } catch (InterruptedException ie) {
                                }
                            }
                            Main.mainController.loadClientInfo(payload.getAuthentication().getName());
                            synchronized (this) {
                                try {
                                    wait();
                                } catch (InterruptedException ie) {
                                }
                            }
                            break;
                        case ERROR:
                            Main.loginController.showLoginError();
                            break;
                        default:
                            //
                    }
                    break;
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
                    if (payload.getClientId() != clientId){
                        Main.mainController.playMessageSound();
                    }
                    Main.mainController.addToMessagesList(payload);
                    Main.mainController.showMessage(payload);
                    break;
                case IMAGE:
                    switch(payload.getImageStatus()) {
                        case START:
                            try {
                                tempFile = Files.createTempFile(tempDir,"userImage-", ".png");
                                tempFile.toFile().deleteOnExit();
                                fop = new FileOutputStream(tempFile.toFile());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case SENDING:
                            try {
                                fop.write(payload.getFileContent());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;

                        case STOP:
                            try {
                                fop.flush();
                                fop.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if(payload.getClientId()==clientId){
                                    Main.mainController.updateUserAvatar(tempFile.toString());
                                } else {
                                    Main.mainController.updateContactAvatarURI(payload, tempFile.toString());
                                }
                            }

                            break;
                    }
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
        connectionTimeout = 500;
        client.connect(port, host, ch -> {
            if(ch.failed()){
                Main.loginController.showErrorDialog();
                connectionTimeout=0;
            }
           });

        while (connectionTimeout > 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connectionTimeout--;
            if (Main.client.isConnected()) {
                System.out.println("Connected to a server");
                break;
            }
        }

        if (connectionTimeout == 0) {
            System.err.println("Failed to connect to a server");
        }


    }

    public void disconnect() {
        client.disconnect();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void login(Integer id, String password) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.AUTHENTICATION, status, new Authentication(id, password)), Payload.class)),
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
        this.status = status;
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.STATUS_UPDATE, clientId, status), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void register(String name, String password) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.REGISTRATION, new Registration(name, password)), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void sendImage(byte[] fileContent, ImageStatus imageStatus) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.IMAGE, clientId, imageStatus, fileContent), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }

    public void sendImage(ImageStatus imageStatus) {
        client.publish(
                topic,
                Buffer.buffer(gson.toJson(new Payload(PayloadType.IMAGE, clientId, imageStatus), Payload.class)),
                MqttQoS.AT_MOST_ONCE,
                false,
                false);
    }
}
