import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;
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
    private Pane cameraPanel;

    private class WebCamInfo {

        private String webCamName;
        private int webCamIndex;

        public String getWebCamName() {
            return webCamName;
        }

        public void setWebCamName(String webCamName) {
            this.webCamName = webCamName;
        }

        public int getWebCamIndex() {
            return webCamIndex;
        }

        public void setWebCamIndex(int webCamIndex) {
            this.webCamIndex = webCamIndex;
        }

        @Override
        public String toString() {
            return webCamName;
        }
    }

    private ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();
    private boolean stopCamera = false;
    private BufferedImage grabbedImage;
    private Webcam selWebCam = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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

    }

    protected void initializeWebCam(ActionEvent event) {

        Webcam webcam = Webcam.getWebcamByName(cameraChoice.getValue().getWebCamName());
        WebcamPanel panel = new WebcamPanel(webcam);

        final SwingNode swingNode = new SwingNode();
        createAndSetSwingContent(swingNode, panel);

        cameraPanel.getChildren().add(swingNode);

        /*final int webCamIndex = cameraChoice.getValue().getWebCamIndex();

        Task<Void> webCamInitializer = new Task<>() {

            @Override
            protected Void call() throws Exception {

                if (selWebCam == null) {
                    selWebCam = Webcam.getWebcams().get(webCamIndex);
                    selWebCam.open();
                } else {
                    closeCamera();
                    selWebCam = Webcam.getWebcams().get(webCamIndex);
                    selWebCam.open();
                }
                return null;
            }

        };

        new Thread(webCamInitializer).start();*/
    }

    private void createAndSetSwingContent(final SwingNode swingNode, WebcamPanel panel) {
        SwingUtilities.invokeLater(() -> swingNode.setContent(panel));
    }

    private void closeCamera() {
        if (selWebCam != null) {
            selWebCam.close();
        }
    }

    public void startCameraPreview(ActionEvent event) {
        startWebCamStream();
        startCameraPreviewButton.setDisable(true);
        stopCameraPreviewButton.setDisable(false);
    }

    public void stopCameraPreview(ActionEvent event) {
        stopCamera = true;
        startCameraPreviewButton.setDisable(false);
        stopCameraPreviewButton.setDisable(true);
        cameraImageView.setCache(false);
    }

    protected void startWebCamStream() {

        stopCamera = false;
        Task<Void> task = new Task<>() {

            @Override
            protected Void call() throws Exception {

                while (!stopCamera) {
                    try {
                        if ((grabbedImage = selWebCam.getImage()) != null) {

                            Platform.runLater(() -> {
                                final Image mainImage = SwingFXUtils
                                        .toFXImage(grabbedImage, null);
                                imageProperty.set(mainImage);
                            });

                            grabbedImage.flush();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
        cameraImageView.imageProperty().bind(imageProperty);

    }
}
