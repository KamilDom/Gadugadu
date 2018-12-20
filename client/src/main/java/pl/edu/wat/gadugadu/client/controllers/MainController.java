package pl.edu.wat.gadugadu.client.controllers;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import pl.edu.wat.gadugadu.client.Contact;
import pl.edu.wat.gadugadu.client.Main;
import pl.edu.wat.gadugadu.client.Message;
import pl.edu.wat.gadugadu.client.controllers.messageViewControllers.MessageViewController;
import pl.edu.wat.gadugadu.common.UserInfo;
import pl.edu.wat.gadugadu.common.UserStatus;
import pl.edu.wat.gadugadu.common.Payload;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class MainController {

    public VBox messageScrollBox;
    public HBox userInfoBox;
    public ScrollPane contactsListScroll;
    public VBox contactsListScrollBox;

    public HBox windowBox;
    public VBox conversationBox;
    public VBox contactsBox;
    public ScrollPane messageScroll;
    public JFXTextArea messageField;

    private DateFormat dateFormat;
    private DateFormat dateFormatTime;

    private String clientName;
    private String destinationClientName;
    private int destinationId;

    private Map<Integer, List<Payload>> messages;
    private List<Contact> contacts = new ArrayList<>();

    UserInfoController userInfoController;

    private AudioClip messageSound;

    public void initialize() {
        Main.mainController = this;
        messages  = new LinkedHashMap<>();
       synchronized(Main.client) {
           Main.client.notify();
       }
        dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        dateFormatTime = new SimpleDateFormat("HH:mm");

        contactsBox.setMinWidth(250);
        conversationBox.prefWidthProperty().bind(windowBox.widthProperty());
        messageScrollBox.heightProperty().addListener(observable -> messageScroll.setVvalue(1D));
        messageScrollBox.prefWidthProperty().bind(messageScroll.widthProperty());
        messageScrollBox.prefHeightProperty().bind(messageScroll.heightProperty());
        messageField.setPrefHeight(0);
        messageField.setMinHeight(0);

        messageScroll.prefHeightProperty().bind(windowBox.heightProperty());
        messageScroll.prefWidthProperty().bind(windowBox.widthProperty());
        messageScroll.setFitToWidth(true);
        messageScroll.setFitToHeight(true);
        contactsListScroll.prefHeightProperty().bind(windowBox.heightProperty());
        contactsListScrollBox.prefWidthProperty().bind(contactsListScroll.widthProperty());
        contactsListScrollBox.prefHeightProperty().bind(contactsListScroll.heightProperty());
        contactsListScroll.setFitToWidth(true);
        contactsListScroll.setFitToHeight(true);

        messageSound= new AudioClip(this.getClass().getResource("/message-sound.mp3").toString());
        messageSound.setVolume(0.3);
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


    public void onEnter(KeyEvent keyEvent) {
        //TODO dorobic mozwliwosc wprowadzania znaku nowej linii przez kombinacje SHIFT + ENTER

        if (keyEvent.getCode().toString().equals("ENTER") & !messageField.getText().isBlank()) {
            //sprawdzanie czy wprowadzono komende zmiany statusu
            Arrays.stream(UserStatus.statusFromInputBox)
                    .filter(removeLastChar(messageField.getText())::equals)
                    .findAny()
                    .ifPresentOrElse(s -> {
                        UserStatus newUserStatus = UserStatus.valueOf(Arrays.asList(UserStatus.statusFromInputBox).indexOf(s));
                        Main.client.changeStatus(newUserStatus);
                        userInfoController.setStatusLabel(UserStatus.statusNames[newUserStatus.value()]);
                    },
                            () -> Main.client.publishMessage(removeLastChar(messageField.getText()), destinationId)
                    );

            messageField.clear();
        }

    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    public void showMessage(Payload payload) {
        if(payload.getDestinationId()==destinationId & payload.getClientId()==Main.client.clientId
        | payload.getDestinationId()==Main.client.clientId & payload.getClientId()==destinationId)
            Platform.runLater(() -> {
                VBox vBox = new VBox();

                FXMLLoader loader;
                if (payload.getClientId() == Main.client.clientId) {
                    loader = new FXMLLoader(getClass().getResource("/messageView/outgoingMessage.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/messageView/incomingMessage.fxml"));
                }

                Parent parent;
                try {
                    parent = loader.load();
                    MessageViewController messageViewController = loader.getController();
                    if (payload.getClientId() == Main.client.clientId) {
                        messageViewController.userName.setText(dateFormatTime.format(dateFormat.parse(payload.getDate())) + ", " + clientName);
                    } else {
                        messageViewController.userName.setText( destinationClientName+", "+dateFormatTime.format(dateFormat.parse(payload.getDate())));
                    }
                    messageViewController.messageContent.setText(payload.getContent());

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
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            });
        else {
            contacts.stream()
                    .filter(contact -> contact.getId()==payload.getClientId())
                    .findAny()
                    .ifPresent(contact ->
                            Platform.runLater(() ->{
                                    contact.getContactInfoController().lastMessage.setText(payload.getContent());
                                    contact.animation.play();
                            }));
        }
    }

    public void loadClientInfo(String name) {
        VBox vBox = new VBox();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/userInfo.fxml"));
        Platform.runLater(() -> {
            Parent parent;
            try {
                parent = loader.load();
                userInfoController = loader.getController();
                userInfoController.setClient(Main.client);
                clientName=name;
                userInfoController.userName.setText(name + " (" + Main.client.clientId + ")");
                userInfoController.status.setText(UserStatus.statusNames[Main.client.getStatus().value()]);
                userInfoController.setCircleStroke(Main.client.getStatus());
                Image img = new Image("/blank-profile-picture.png");
                userInfoController.userImage.setFill(new ImagePattern(img));

                vBox.getChildren().addAll(parent);
                userInfoBox.getChildren().add(vBox);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void showContactsList(List<UserInfo> onlineUsers) {
        Platform.runLater(() -> {
            contactsListScrollBox.getChildren().clear();
            for (UserInfo userInfo : onlineUsers) {
                if (userInfo.getClientId() != Main.client.clientId) {
                    Contact contact = new Contact(userInfo.getClientId(), userInfo, this);
                    contacts.add(contact);
                    contactsListScrollBox.getChildren().add(contact.getvBox());
                }
            }

        });
    }

    public void updateContactStatus(Payload payload){
        contacts.stream()
                .filter(contact -> contact.getId()==payload.getClientId())
                .findAny()
                .ifPresent(contact -> {
                    if(payload.getStatus()==UserStatus.OFFLINE) {
                        Platform.runLater(() -> contactsListScrollBox.getChildren().remove(contact.getvBox()));
                        contacts.remove(contact);
                    } else {
                        Platform.runLater(() -> contact.setStatus(payload.getStatus()));
                    }
                });
    }

    public void addToContactList(UserInfo userInfo){
        Platform.runLater(() -> {
            Contact contact = new Contact(userInfo.getClientId(), userInfo, this);
            contacts.add(contact);
            contactsListScrollBox.getChildren().add(contact.getvBox());
        });
    }

    public void addToMessagesList(Payload payload) {
        if(payload.getClientId()==Main.client.clientId){
            if (!messages.containsKey(destinationId))
                messages.put(destinationId, new LinkedList<>());

            messages.get(destinationId).add(payload);
        } else {
            if (!messages.containsKey(payload.getClientId()))
                messages.put(payload.getClientId(), new LinkedList<>());

            messages.get(payload.getClientId()).add(payload);
        }
    }

    public void showConversationWithClient(int clientId){
        Platform.runLater(() -> {
            messageScrollBox.getChildren().clear();
            if(messages.containsKey(clientId))
            for(Payload payload: messages.get(clientId)){
                showMessage(payload);
            }
        });
    }

    public void changeDestinationId(int destinationId) {
        messageField.setMinHeight(100);
        for(Contact c : contacts){
            c.getContactInfoController().removeBrightestBackround();
        }
        contacts.stream()
                .filter(contact -> contact.getId()==destinationId)
                .findAny()
                .ifPresent(contact -> {
                    contact.animation.stop();
                    contact.getvBox().setBackground(Background.EMPTY);
                    contact.getContactInfoController().addBrightestBackround();
                    destinationClientName=contact.getContactName();
                });
        this.destinationId = destinationId;
    }

    public void playMessageSound(){
        messageSound.play();
    }
}
