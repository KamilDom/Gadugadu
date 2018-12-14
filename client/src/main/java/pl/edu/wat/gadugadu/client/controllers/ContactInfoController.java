package pl.edu.wat.gadugadu.client.controllers;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import pl.edu.wat.gadugadu.client.Client;
import pl.edu.wat.gadugadu.client.Main;
import pl.edu.wat.gadugadu.common.UserInfo;
import pl.edu.wat.gadugadu.common.UserStatus;

public class ContactInfoController {
    public Circle userImage;
    public Label userName;
    public Label status;
    public VBox clientInfoVBox;
    public HBox contactBox;
    // private Client client;

    private int id;

    public void initialize() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void onContactClick(MouseEvent mouseEvent) {
        System.out.println(id);
    }

    public void setCircleStroke(UserStatus userStatus){
        userImage.setStrokeWidth(3);
        switch(userStatus){
            case AVAILABLE:
                userImage.setStroke(Color.web("#00DC00"));
                break;
            case BUSY:
                userImage.setStroke(Color.web("#ffff00"));
                break;
            case DO_NOT_DISTURB:
                userImage.setStroke(Color.web("#ff7b00"));
                break;
            case AWAY:
                userImage.setStroke(Color.web("#00a0ff"));
                break;
            case BE_RIGHT_BACK:
                userImage.setStroke(Color.web("#0000ffff"));
                break;

            default:
                //
        }
    }

    /*  public void setClient(Client client) {
        this.client = client;
    }*/


}
