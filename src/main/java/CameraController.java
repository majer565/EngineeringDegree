import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class CameraController implements Initializable {


    @FXML
    private ComboBox<WebCamInfo> cameraChoice;

    @FXML
    private Button startCameraPreviewButton;

    @FXML
    private Button stopCameraPreviewButton;

    @FXML
    private ImageView cameraImageView;

    @FXML
    private ImageView cameraTakenImage;

    @FXML
    private Button takeImageButton;

    private boolean stopCamera = true;
    private Webcam chosenWebCam = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        takeImageButton.setDisable(true);

        ObservableList<WebCamInfo> options = FXCollections.observableArrayList();
        int webCamCounter = 0;

        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            options.add(webCamInfo);
            webCamCounter++;
        }

        cameraChoice.setItems(options);
        cameraChoice.setPromptText("Choose camera...");
        cameraChoice.setOnAction(this::initializeWebCam);

        cameraImageView.setFitHeight(270);
        cameraImageView.setFitWidth(480);
        cameraImageView.setPreserveRatio(true);

    }

    protected void initializeWebCam(ActionEvent event) {

        if(chosenWebCam != null) chosenWebCam.close();

        chosenWebCam = Webcam.getWebcamByName(cameraChoice.getValue().getWebCamName());
        chosenWebCam.setCustomViewSizes(new Dimension(1920, 1080));
        chosenWebCam.setViewSize(WebcamResolution.FHD.getSize());
        takeImageButton.setDisable(false);
    }

    public void startCameraPreview(ActionEvent event) {
        if(chosenWebCam != null) {
            startCameraStream();
            startCameraPreviewButton.setDisable(true);
            stopCameraPreviewButton.setDisable(false);
        }
    }

    public void stopCameraPreview(ActionEvent event) {
        if(!stopCamera) {
            stopCamera = true;
            startCameraPreviewButton.setDisable(false);
            stopCameraPreviewButton.setDisable(true);
            cameraImageView.setCache(false);
            chosenWebCam.close();
        }
    }

    public void cameraTakeImage(ActionEvent event) {

        if(chosenWebCam.open() && chosenWebCam != null) {
            final BufferedImage image = chosenWebCam.getImage();
            if(ImageController.saveJpg(ImageController.convertToBW(image), "./src/main/images/workspace")) {
                cameraTakenImage.setImage(new Image(new File("./src/main/images/workspace.jpg").toURI().toString()));
            } else {
                System.out.println(new Date(System.currentTimeMillis()) + ": Could not save image [013]");
            }
        }

    }

    public void closeCameraWindow(ActionEvent event) {

        if(!stopCamera) chosenWebCam.close();

        Stage stage = (Stage) startCameraPreviewButton.getScene().getWindow();
        stage.close();

    }

    protected void startCameraStream() {

        stopCamera = false;
        chosenWebCam.open();

        Thread thread = new Thread(() -> {

            while(!stopCamera) {
                final BufferedImage image = chosenWebCam.getImage();
                final Image image2 = SwingFXUtils.toFXImage(image, null);
                cameraImageView.setImage(image2);
            }

        });

        thread.start();

    }
}
