import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;


public class ConnectionController {

    @FXML
    private TextField deviceName;

    @FXML
    private Button connectButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    public void submit(ActionEvent event) {

        System.out.println("Device name: " + deviceName.getText());

    }

}
