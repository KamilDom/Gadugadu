package pl.edu.wat.kowalski.client;

import javafx.scene.control.Label;
import pl.edu.wat.kowalski.common.CommonExample;

public class MainController {
    public Label helloLabel;

    public void initialize() {
        helloLabel.setText("Hello from client Java. && " + CommonExample.hello());
    }
}
