package pl.edu.wat.gadugadu.client.controllers;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import pl.edu.wat.gadugadu.client.Client;
import pl.edu.wat.gadugadu.client.controllers.messageViewControllers.OutgoingMessageViewController;
import pl.edu.wat.gadugadu.common.ClientStatus;

import java.io.IOException;
import java.util.Arrays;

public class MainController {


    public VBox messageScrollBox;
    private Client client;

    public HBox windowBox;
    public VBox conversationBox;
    public VBox contactsBox;
    public ScrollPane messageScroll;
    public JFXTextArea messageField;


    public void initialize() {
        contactsBox.setMinWidth(200);
        conversationBox.prefWidthProperty().bind(windowBox.widthProperty());
        messageScrollBox.heightProperty().addListener(observable -> messageScroll.setVvalue(1D));
        messageScrollBox.prefWidthProperty().bind(messageScroll.widthProperty());
        messageScrollBox.prefHeightProperty().bind(messageScroll.heightProperty());
        messageField.setMinHeight(100);
        messageScroll.prefHeightProperty().bind(windowBox.heightProperty());
        messageScroll.setFitToWidth(true);
        client = new Client(1883,"0.0.0.0","gadugadu",this);
        client.connect();
    }
    
    public void onConnectToServer(ActionEvent actionEvent) {
        client.connect();
    }

    public void onEnter(KeyEvent keyEvent) {
        //TODO dorobic mozwliwosc wprowadzania znaku nowej linii przez kombinacje SHIFT + ENTER

        if(keyEvent.getCode().toString().equals("ENTER") & !messageField.getText().isBlank()){
                //sprawdzanie czy wprowadzono komende zmiany statusu
                Arrays.stream(ClientStatus.statusFromInputBox)
                    .filter(removeLastChar(messageField.getText())::equals)
                    .findAny()
                    .ifPresentOrElse(s -> client.changeStatus(
                        ClientStatus.valueOf(Arrays.asList(ClientStatus.statusFromInputBox).indexOf(s))),
                        () ->client.publishMessage(removeLastChar(messageField.getText()))
                    );

            messageField.clear();
        }

    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public void showMessage(String message) {
        Platform.runLater(() -> {
            VBox vBox = new VBox();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/messageView/outgoingMessage.fxml"));
            Parent parent;
            try {
                parent = loader.load();
                OutgoingMessageViewController messageViewController = loader.getController();
                messageViewController.userName.setText("xDD");
                messageViewController.messageContent.setText(message);

                Image img = new Image("/blank-profile-picture.png");
                messageViewController.userImage.setFill(new ImagePattern(img));

                vBox.getChildren().addAll(parent);
                messageScrollBox.getChildren().add(vBox);

            } catch (IOException e) {
                e.printStackTrace();
            }

            });

    }
}
