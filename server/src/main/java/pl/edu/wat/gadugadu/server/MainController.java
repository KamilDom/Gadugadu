package pl.edu.wat.gadugadu.server;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

public class MainController {

    public Label serviceStatus;
    public Label connectedClients;
    public Server server;

    public void initialize() {
        server = new Server(1883,"127.0.0.20","gadugadu", this);
    }

    public void onStartServer(ActionEvent actionEvent) {
            server.starServer();
    }

    public void onStopServer(ActionEvent actionEvent) {
        server.stopServer();
    }

    public void onSendMessage(ActionEvent actionEvent) {
        //server.publishTestMessage("Test message");
       // server.publishClientsList();
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
