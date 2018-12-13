package pl.edu.wat.gadugadu.client.controllers;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import pl.edu.wat.gadugadu.client.Client;
import pl.edu.wat.gadugadu.client.controllers.messageViewControllers.MessageViewController;
import pl.edu.wat.gadugadu.common.UserStatus;
import pl.edu.wat.gadugadu.common.Payload;

import java.io.IOException;
import java.util.Arrays;

public class MainController {


    public VBox messageScrollBox;
    public HBox userInfoBox;
    public ScrollPane contactsListScroll;
    public VBox contactsListScrollBox;
    private Client client;

    public HBox windowBox;
    public VBox conversationBox;
    public VBox contactsBox;
    public ScrollPane messageScroll;
    public JFXTextArea messageField;

    private Gson gson;

    public void initialize() {
        gson = new Gson();

        contactsBox.setMinWidth(200);
        conversationBox.prefWidthProperty().bind(windowBox.widthProperty());
        messageScrollBox.heightProperty().addListener(observable -> messageScroll.setVvalue(1D));
        messageScrollBox.prefWidthProperty().bind(messageScroll.widthProperty());
        messageScrollBox.prefHeightProperty().bind(messageScroll.heightProperty());
        messageField.setMinHeight(100);
        messageScroll.prefHeightProperty().bind(windowBox.heightProperty());
        messageScroll.prefWidthProperty().bind(windowBox.widthProperty());
        messageScroll.setFitToWidth(true);
        messageScroll.setFitToHeight(true);
        contactsListScroll.prefHeightProperty().bind(windowBox.heightProperty());
        contactsListScrollBox.prefWidthProperty().bind(contactsListScroll.widthProperty());
        contactsListScrollBox.prefHeightProperty().bind(contactsListScroll.heightProperty());
        contactsListScroll.setFitToWidth(true);
        contactsListScroll.setFitToHeight(true);
        client = new Client(1883, "127.0.0.20", "gadugadu", this);
        client.connect();
        loadClientInfo();

        messageScrollBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            Platform.runLater(() -> {
                Stage stage = (Stage) newScene.getWindow();
                stage.setOnCloseRequest(e -> {
                    Platform.exit();
                    System.exit(0);
                });
            });
        });

    }

    public void onConnectToServer(ActionEvent actionEvent) {
        client.connect();
    }

    public void onEnter(KeyEvent keyEvent) {
        //TODO dorobic mozwliwosc wprowadzania znaku nowej linii przez kombinacje SHIFT + ENTER

        if (keyEvent.getCode().toString().equals("ENTER") & !messageField.getText().isBlank()) {
            //sprawdzanie czy wprowadzono komende zmiany statusu
            Arrays.stream(UserStatus.statusFromInputBox)
                    .filter(removeLastChar(messageField.getText())::equals)
                    .findAny()
                    .ifPresentOrElse(s -> client.changeStatus(
                            UserStatus.valueOf(Arrays.asList(UserStatus.statusFromInputBox).indexOf(s))),
                            () -> client.publishMessage(removeLastChar(messageField.getText()))
                    );

            messageField.clear();
        }

    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public void showMessage(String payload) {
        Platform.runLater(() -> {
            VBox vBox = new VBox();
            Payload p = gson.fromJson(payload, Payload.class);

            FXMLLoader loader;
            if (p.getClientId()==client.clientId){
                loader = new FXMLLoader(getClass().getResource("/messageView/outgoingMessage.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/messageView/incomingMessage.fxml"));
            }

            Parent parent;
            try {
                parent = loader.load();
                MessageViewController messageViewController = loader.getController();
                messageViewController.userName.setText("xDD");
                messageViewController.messageContent.setText(p.getContent());

                messageViewController.messageContent.setMaxWidth(messageScrollBox.getWidth() - 150);
                messageScrollBox.widthProperty().addListener(observable -> {
                    messageViewController.messageContent.setMaxWidth(messageScrollBox.getWidth() - 150);
                });

                Image img = new Image("/blank-profile-picture.png");
                messageViewController.userImage.setFill(new ImagePattern(img));

                vBox.getChildren().addAll(parent);
                messageScrollBox.getChildren().add(vBox);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }
    private void loadClientInfo(){
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/userInfo.fxml"));
        Parent parent;
        try {
            parent = loader.load();
            UserInfoController userInfoController = loader.getController();
            userInfoController.setClient(client);
            userInfoController.userName.setText("xDD");
            userInfoController.status.setText("4");

            Image img = new Image("/blank-profile-picture.png");
            userInfoController.userImage.setFill(new ImagePattern(img));

            vBox.getChildren().addAll(parent);
            userInfoBox.getChildren().add(vBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateClientsList(String payload) {
    }
}
