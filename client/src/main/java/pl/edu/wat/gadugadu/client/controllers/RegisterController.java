package pl.edu.wat.gadugadu.client.controllers;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.wat.gadugadu.client.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class RegisterController {


    public JFXTextField imagePath;
    public JFXTextField name;
    public JFXTextField password;

    public void onCancel(ActionEvent actionEvent) {
        loadLogin();
        closeStage();
    }

    private void loadLogin() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/ui/loginWindow.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Gadugadu");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {

        }
    }

    private void closeStage() {
        ((Stage) name.getScene().getWindow()).close();
    }

    public void onUploadFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("All Images", "*.*")
        );
        File file = fileChooser.showOpenDialog((name.getScene().getWindow()));
        BufferedImage bimg = null;
        // TODO dorobic logike wczytywania i sprawdzania rozmiarow
        try {
            bimg = ImageIO.read(file);
            imagePath.setText(file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public void onSave(ActionEvent actionEvent) {

        if(name.getText().length()<3){
            name.getStyleClass().add("wrong-credentials");
        } else {
            name.getStyleClass().remove("wrong-credentials");
        }

        if(password.getText().length()<3){
            password.getStyleClass().add("wrong-credentials");
        } else {
            password.getStyleClass().remove("wrong-credentials");
        }

        if (name.getText().length()>3 & password.getText().length()>3) {
                Main.client.connect();
                Main.client.register(name.getText(), password.getText());
        }
    }
}
