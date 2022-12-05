import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ProgressBar deviceConnectionBar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if(!Main.isConnectDeviceStatus()) {
            deviceConnectionBar.setStyle("-fx-accent: #B50015;");
            deviceConnectionBar.setProgress(1);
        }

    }

    @FXML
    public void deviceConnectionConfigure(ActionEvent event) {

        if(!openStage("deviceConfig.fxml")) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not open deviceConfig.fxml stage [001]");
        }

    }

    public void cameraConnectionConfigure(ActionEvent event) {
        //System.out.println("CAMERA");
        //Webcam webcam = Webcam.getWebcamByName("A4tech FHD 1080P PC Camera 0");
        //if(webcam != null) System.out.println("Webcam: " + webcam.getName());
        //else System.out.println("Cannot find webcam");

        //webcam.setViewSize(WebcamResolution.VGA.getSize());

        //WebcamUtils.capture(webcam, "camera.jpg");

        if(!openStage("cameraConfig.fxml")) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not open deviceConfig.fxml stage [001]");
        }
    }

    @FXML
    public void imageAdd(ActionEvent event){

        if(!openStage("imageAdd.fxml")) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not open deviceConfig.fxml stage [001]");
        }

    }

    public boolean openStage(String stageName) {

        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource(stageName));
            Scene scene = new Scene(root);

            stage.setResizable(false);
            stage.setTitle("Program by Filip Majewksi");

            stage.setScene(scene);
            stage.show();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
