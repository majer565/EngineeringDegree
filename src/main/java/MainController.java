import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ProgressBar deviceConnectionBar;

    @FXML
    private Slider speedSlider;

    @FXML
    private Button moveHomeButton;

    @FXML
    private Button moveRightButton;

    @FXML
    private Button moveDownButton;

    @FXML
    private Button moveUpButton;

    @FXML
    private Button moveLeftButton;

    @FXML
    private Label deviceConLabel;

    @FXML
    private Label speedNumber;

    private int speed = 20;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!Main.isConnectDeviceStatus()) {
            deviceConnectionBar.setStyle("-fx-accent: #B50015;");
            deviceConnectionBar.setProgress(1);

            moveHomeButton.setDisable(true);
            moveRightButton.setDisable(true);
            moveDownButton.setDisable(true);
            moveUpButton.setDisable(true);
            moveLeftButton.setDisable(true);
        }

        speedSlider.valueProperty().addListener((observableValue, number, t1) -> {
            final double roundedValue = Math.floor(t1.doubleValue() / 10.0) * 10.0;
            speedSlider.valueProperty().set(roundedValue);
            speed = (int) speedSlider.getValue();
            speedNumber.setText(Integer.toString(speed));
        });
    }

    @FXML
    public void deviceConnectionConfigure(ActionEvent event) {

        if(!openStage("deviceConfig.fxml")) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not open deviceConfig.fxml stage [001]");
        }

    }

    public void cameraConnectionConfigure(ActionEvent event) {
        if(!openStage("cameraConfig.fxml")) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not open cameraConfig.fxml stage [002]");
        }
    }

    @FXML
    public void imageAdd(ActionEvent event){

        if(!openStage("imageAdd.fxml")) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not open imageAdd.fxml stage [003]");
        }

    }

    public boolean openStage(String stageName) {

        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(stageName));
            Scene scene = new Scene(root);

            stage.setResizable(false);
            stage.setTitle("Program by Filip Majewski");

            stage.setScene(scene);
            stage.show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void deviceReconnect(ActionEvent event) {

        if(Main.isConnectDeviceStatus()) {
            deviceConnectionBar.setStyle("-fx-accent: #25e72c;");
            deviceConnectionBar.setProgress(1);

            moveHomeButton.setDisable(false);
            moveRightButton.setDisable(false);
            moveDownButton.setDisable(false);
            moveUpButton.setDisable(false);
            moveLeftButton.setDisable(false);
        } else {
            deviceConLabel.setText("Cannot find the device. Configure it in device tab.");
            deviceConLabel.setAlignment(Pos.CENTER);
        }

    }

    public void moveHome(ActionEvent event) {

    }

    public void moveLeft(ActionEvent event) {

    }

    public void moveRight(ActionEvent event) {

    }

    public void moveDown(ActionEvent event) {

    }

    public void moveUp(ActionEvent event) {

    }
}
