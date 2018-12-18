package pl.edu.wat.gadugadu.client.controllers;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import pl.edu.wat.gadugadu.client.Client;
import pl.edu.wat.gadugadu.common.UserStatus;

public class UserInfoController {
    public Circle userImage;
    public Label userName;
    public Label status;
    public HBox userInfoBox;
    private ContextMenu contextMenu;
    private Client client;

    public void initialize() {
        contextMenu = new ContextMenu();
        MenuItem status1 = new MenuItem("Available");
        MenuItem status2 = new MenuItem("Busy");
        MenuItem status3 = new MenuItem("Do not disturb");
        MenuItem status4 = new MenuItem("Away");
        MenuItem status5 = new MenuItem("Be right back");


        contextMenu.getItems().addAll(status1, status2, status3, status4, status5);

        userInfoBox.setOnContextMenuRequested(event -> {
            contextMenu.show(userInfoBox, event.getScreenX(), event.getScreenY());
        });

        status1.setOnAction(event -> {
            client.changeStatus(UserStatus.AVAILABLE);
            status.setText("Available");
        });
        status2.setOnAction(event -> {
            client.changeStatus(UserStatus.BUSY);
            status.setText("Busy");
        });
        status3.setOnAction(event -> {
            client.changeStatus(UserStatus.DO_NOT_DISTURB);
            status.setText("Do not disturb");
        });
        status4.setOnAction(event -> {
            client.changeStatus(UserStatus.AWAY);
            status.setText("Away");
        });
        status5.setOnAction(event -> {
            client.changeStatus(UserStatus.BE_RIGHT_BACK);
            status.setText("Be right back");
        });

    }

    public void setClient(Client client) {
        this.client = client;
    }


}
