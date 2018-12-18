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

    public JFXTextField id;
    public JFXPasswordField password;


    public void initialize() {
        Main.loginController=this;
        id.setText("1");
        password.setText("1234");
    }



    private void closeStage() {
        ((Stage) id.getScene().getWindow()).close();
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
        String uname = id.getText();
        String pword = password.getText();

        if(!Main.client.isConnected())
            Main.client.connect();

        //TODO do kodowania w sha w bazie danych
        //String pword = DigestUtils.shaHex(password.getText());


      //  if (uname.equals("1") && pword.equals("user")) {
            //if (uname.equals(preference.getUsername()) && pword.equals(preference.getPassword())) {
            closeStage();
            loadMain();
            Main.client.login(Integer.valueOf(uname),pword);
            Main.client.clientId=Integer.valueOf(uname);
       /* }
        else {
            id.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }*/
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
        }
        catch (IOException ex) {

        }
    }
}
