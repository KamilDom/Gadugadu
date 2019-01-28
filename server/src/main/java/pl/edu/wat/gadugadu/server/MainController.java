package pl.edu.wat.gadugadu.server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainController {

    public Label serviceStatus;
    public Label connectedClients;
    public Server server;


    public void initialize() {
        Properties properties = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream("/server.properties");
            properties.load(in);
            server = new Server(Integer.parseInt(properties.getProperty("Port")), properties.getProperty("IP"),"gadugadu", this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onStartServer(ActionEvent actionEvent) {
            server.starServer();
    }

    public void onStopServer(ActionEvent actionEvent) {
        server.stopServer();
    }

    public void onSendMessage(ActionEvent actionEvent) {
        //server.publishTestMessage("Test message");
//        server.publishClientsList();
}

    public void updateStatus(String status){
        Platform.runLater(() -> {
            serviceStatus.setText("Server status: "+status);
        });
    }

    public void updateConnectedClientsCount(int connectedClientsCount) {
        Platform.runLater(() -> {
            connectedClients.setText("Connected clients: " + connectedClientsCount);
        });
    }
}
