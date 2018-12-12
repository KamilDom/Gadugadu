package pl.edu.wat.gadugadu.client.controllers;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class ClientInfoController {
    public Circle userImage;
    public Label userName;
    public Label status;
    public VBox clientInfoVBox;
    ContextMenu contextMenu;

    public void initialize() {
        contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Menu Item 1");
        contextMenu.getItems().addAll(item1);
        clientInfoVBox.setOnContextMenuRequested(event -> {
            contextMenu.show(clientInfoVBox, event.getScreenX(), event.getScreenY());
        });
    }


}
