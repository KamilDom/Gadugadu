package pl.edu.wat.gadugadu.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.wat.gadugadu.client.controllers.LoginController;
import pl.edu.wat.gadugadu.client.controllers.MainController;
import pl.edu.wat.gadugadu.client.controllers.RegisterController;

public class Main extends Application {
    public static Client client;
    public static LoginController loginController;
    public static RegisterController registerController;
    public static MainController mainController;

    public static void main(String[] args) {
        // te dwie linie potrzebne zeby nie pokazywalo niepotrzbnych warningow w konsoli
        System.err.close();
        System.setErr(System.out);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/loginWindow.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        client = new Client(1883, "127.0.0.20", "gadugadu");
        primaryStage.setTitle("Gadugadu Client");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
