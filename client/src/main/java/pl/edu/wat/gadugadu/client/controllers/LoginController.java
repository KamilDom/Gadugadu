package pl.edu.wat.gadugadu.client.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.wat.gadugadu.client.Main;

import java.io.IOException;

public class LoginController {

    public JFXTextField username;
    public JFXPasswordField password;

    public void initialize() {
        Main.loginController=this;
        username.setText("user");
        password.setText("user");
    }



    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    void loadMain() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/ui/mainWindow.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Gadugadu");
            stage.setScene(new Scene(parent));
            stage.show();
        }
        catch (IOException ex) {

        }
    }

    public void onLogin(ActionEvent actionEvent) {
        String uname = username.getText();
        String pword = password.getText();

        Main.client.connect();

        //TODO do kodowania w sha w bazie danych
        //String pword = DigestUtils.shaHex(password.getText());


        if (uname.equals("user") && pword.equals("user")) {
            //if (uname.equals(preference.getUsername()) && pword.equals(preference.getPassword())) {
            closeStage();
            loadMain();
            Main.client.login(uname,pword);

        }
        else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
    }

    public void onExit(ActionEvent actionEvent) {
        ((Stage) username.getScene().getWindow()).close();
    }

    public void onRegister(ActionEvent actionEvent) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/ui/registerWindow.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Gadugadu");
            stage.setScene(new Scene(parent));
            stage.show();
        }
        catch (IOException ex) {

        }
    }
}
