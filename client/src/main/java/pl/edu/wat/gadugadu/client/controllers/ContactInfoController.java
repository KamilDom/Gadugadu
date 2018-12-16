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
import pl.edu.wat.gadugadu.client.Contact;
import pl.edu.wat.gadugadu.client.Main;
import pl.edu.wat.gadugadu.common.UserInfo;
import pl.edu.wat.gadugadu.common.UserStatus;

public class ContactInfoController {
    public Circle userImage;
    public Label userName;
    public Label status;
    public VBox clientInfoVBox;
    public HBox contactBox;
    public Label lastMessage;
    private MainController mainController;
    private Contact contact;

    private int id;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void initialize() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void onContactClick(MouseEvent mouseEvent) {
        mainController.changeDestinationId(id);
        mainController.showConversationWithClient(id);
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

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void onMouseEntered(MouseEvent mouseEvent) {
        contact.setMouseEntered(true);
        contactBox.getStyleClass().add("highlighted-background");

    }

    public void OnMouseExited(MouseEvent mouseEvent) {
        contact.setMouseEntered(false);
        contactBox.getStyleClass().remove("highlighted-background");
    }

    public void addBrightestBackround(){
        contactBox.getStyleClass().add("brighter-highlighted-background");

    }
    public void removeBrightestBackround(){
        contactBox.getStyleClass().remove("brighter-highlighted-background");

    }




}
