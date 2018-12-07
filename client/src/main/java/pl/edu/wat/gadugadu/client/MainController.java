package pl.edu.wat.gadugadu.client;

import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import pl.edu.wat.gadugadu.common.ClientStatus;

import java.util.Arrays;

public class MainController {

    public TextArea messageBox;
    private Client client;

    public void initialize() {
        client = new Client(1883,"0.0.0.0","gadugadu",this);
    }
    
    public void onConnectToServer(ActionEvent actionEvent) {
        client.connect();
    }

    public void onEnter(KeyEvent keyEvent) {
        //TODO dorobic mozwliwosc wprowadzania znaku nowej linii przez kombinacje SHIFT + ENTER

        if(keyEvent.getCode().toString().equals("ENTER") & !messageBox.getText().isBlank()){
                //sprawdzanie czy wprowadzono komende zmiany statusu
                Arrays.stream(ClientStatus.statusFromInputBox)
                    .filter(removeLastChar(messageBox.getText())::equals)
                    .findAny()
                    .ifPresentOrElse(s -> client.changeStatus(
                        ClientStatus.valueOf(Arrays.asList(ClientStatus.statusFromInputBox).indexOf(s))),
                        () ->client.publishMessage(removeLastChar(messageBox.getText()))
                    );

                messageBox.clear();
        }

    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

}
