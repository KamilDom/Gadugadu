package pl.edu.wat.gadugadu.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        // te dwie linie potrzebne zeby nie pokazywalo niepotrzbnych warningow w konsoli
        System.err.close();
        System.setErr(System.out);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(root, 400, 150);

        primaryStage.setTitle("Gadugadu Server");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
