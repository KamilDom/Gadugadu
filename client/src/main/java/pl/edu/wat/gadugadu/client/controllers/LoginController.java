package pl.edu.wat.gadugadu.client.controllers;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.wat.gadugadu.client.Main;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class LoginController {

    public JFXTextField id;
    public JFXPasswordField password;
    public StackPane loginPane;
    public VBox loginVBox;

    public void initialize() {
        Main.loginController=this;
        id.setText("100");
        password.setText("1234");

    }



    public void closeLoginStage() {
        Platform.runLater(() ->  ((Stage) id.getScene().getWindow()).close());
    }

    public void loadMainStage() {
        Platform.runLater(() -> {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("/ui/mainWindow.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setTitle("Gadugadu");
                stage.setScene(new Scene(parent));
                stage.show();
            } catch (IOException ex) {

            }
        });
    }

    public void onLogin(ActionEvent actionEvent) {
        String uname = id.getText();
        String pword = password.getText();

        if(!Main.client.isConnected())
            Main.client.connect();

        //TODO do kodowania w sha w bazie danych
        //String pword = DigestUtils.shaHex(password.getText());
        if(Main.client.isConnected()) {
            Main.client.login(Integer.valueOf(uname), pword);
            Main.client.clientId = Integer.valueOf(uname);
        }

    }

    public void onExit(ActionEvent actionEvent) {
        ((Stage) id.getScene().getWindow()).close();
    }

    public void onRegister(ActionEvent actionEvent) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/ui/registerWindow.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Gadugadu");
            stage.setScene(new Scene(parent));
            stage.show();
            ((Stage) id.getScene().getWindow()).close();
        }
        catch (IOException ex) {

        }
    }


    public void showLoginError() {
        id.getStyleClass().add("wrong-credentials");
        password.getStyleClass().add("wrong-credentials");
    }

    public void showErrorDialog(){
        Platform.runLater(() -> {
            BoxBlur boxBlur = new BoxBlur(3, 3, 3);
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            JFXButton button = new JFXButton("Cancel");
            JFXDialog dialog = new JFXDialog(loginPane, dialogLayout, JFXDialog.DialogTransition.TOP);
            button.setPrefWidth(100);
            button.setOnAction(e -> dialog.close());

            Label label = new Label("Failed to connect to a server");
            label.getStyleClass().add("jfx-layout-heading-error");
            dialogLayout.setHeading(label);
            dialogLayout.setActions(button);
            dialog.show();
            dialog.setOnDialogClosed(event -> {
                loginVBox.setEffect(null);
            });

            loginVBox.setEffect(boxBlur);
        });
    }

}
