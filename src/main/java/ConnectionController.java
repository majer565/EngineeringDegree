import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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

    public void connectDevice(ActionEvent event) {

        if(deviceName.getText().isEmpty()) {

            progressBar.setStyle("-fx-accent: #B50015;");
            progressBar.setProgress(1);
            progressLabel.setText("Cannot connect to the device");
            progressLabel.setFont(Font.font("System", 12));
            progressLabel.setAlignment(Pos.CENTER);

        } else {

            System.out.println("Device name: " + deviceName.getText());
            progressBar.setStyle("-fx-accent: #25e72c;");
            progressBar.setProgress(1);
            progressLabel.setText("Connected to the device");
            progressLabel.setAlignment(Pos.CENTER);

            Main.setConnectDeviceStatus(true);

        }

    }

    public void closeWindow(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();

    }


}
