import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private ProgressBar deviceConnectionBar;

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
    private ImageView gridImageView;

    @FXML
    private ImageView workspaceImageView;

    @FXML
    private ImageView workImageView;

    @FXML
    private ComboBox<String> imageToWorkCB;

    @FXML
    private Spinner<Integer> workImageSizeX;

    @FXML
    private Spinner<Integer> workImageSizeY;

    @FXML
    private ImageView gridWorkImageView;

    @FXML
    private CheckBox confirmProcessCB;

    @FXML
    private Spinner<Integer> beltPitchSpinner;

    @FXML
    private Spinner<Integer> pulleyNOTSpinner;

    @FXML
    private Spinner<Integer> stepsPerRevSpinner;

    @FXML
    private Spinner<Integer> microstepsSpinner;

    @FXML
    private ComboBox<WebCamInfo> webcamCB;

    @FXML
    private Button startEngravingProcessButton;

    private final ArduinoController arduinoController = new ArduinoController();

    private File imageToWork;

    private int[][] workspacePixelArray;

    private int[][] workspaceDimensions;

    private final double[] workImageCoordinates = new double[2];

    private MotionController motionController;

    private Webcam webcam;

    private Timeline timelineLeft;
    private Timeline timelineRight;
    private Timeline timelineUp;
    private Timeline timelineDown;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Set grid image
        gridImageView.setImage(new Image(new File("./src/main/resources/grid.png").toURI().toString()));

        //Initialize image options
        ObservableList<String> options = FXCollections.observableArrayList();
        options.add("Black&White image");
        options.add("White&Black image");
        imageToWorkCB.setItems(options);
        imageToWorkCB.setPromptText("Choose image...");
        imageToWorkCB.setOnAction(this::setImageToWork);

        //Set progress bar to red
        deviceConnectionBar.setStyle("-fx-accent: #B50015;");
        deviceConnectionBar.setProgress(1);

        //Initialize spinner image parameters
        SpinnerValueFactory<Integer> valueFactoryX = new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 1060);
        SpinnerValueFactory<Integer> valueFactoryY = new SpinnerValueFactory.IntegerSpinnerValueFactory(20, 1060);
        valueFactoryX.setValue(1);
        valueFactoryY.setValue(1);
        workImageSizeX.setValueFactory(valueFactoryX);
        workImageSizeY.setValueFactory(valueFactoryY);

        //Initialize spinner parameters
        SpinnerValueFactory<Integer> belt = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
        SpinnerValueFactory<Integer> pulley = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 40);
        SpinnerValueFactory<Integer> microsteps = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 256);
        SpinnerValueFactory<Integer> stepPerRev = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000);
        belt.setValue(1);
        pulley.setValue(1);
        microsteps.setValue(1);
        stepPerRev.setValue(1);
        beltPitchSpinner.setValueFactory(belt);
        pulleyNOTSpinner.setValueFactory(pulley);
        microstepsSpinner.setValueFactory(microsteps);
        stepsPerRevSpinner.setValueFactory(stepPerRev);

        //Initialize move buttons
        moveHomeButton.setDisable(true);
        moveRightButton.setDisable(true);
        moveDownButton.setDisable(true);
        moveUpButton.setDisable(true);
        moveLeftButton.setDisable(true);
        initializeMotionButtons();

        //Initialize camera
        ObservableList<WebCamInfo> webcamOptions = FXCollections.observableArrayList();
        int webCamCounter = 0;

        for (Webcam webcam : Webcam.getWebcams()) {
            WebCamInfo webCamInfo = new WebCamInfo();
            webCamInfo.setWebCamIndex(webCamCounter);
            webCamInfo.setWebCamName(webcam.getName());
            webcamOptions.add(webCamInfo);
            webCamCounter++;
        }
        webcamCB.setItems(webcamOptions);
        webcamCB.setPromptText("Choose camera...");
        webcamCB.setOnAction(this::initializeWebCam);

        //Set startEngravingProcessButton to disabled
        startEngravingProcessButton.setDisable(true);

    }

    private void initializeWebCam(ActionEvent event) {
        if(webcam != null) webcam.close();
        webcam = Webcam.getWebcamByName(webcamCB.getValue().getWebCamName());
        webcam.setCustomViewSizes(new Dimension(1920, 1080));
        webcam.setViewSize(WebcamResolution.FHD.getSize());
    }

    private void initializeMotionButtons() {
        timelineLeft = new Timeline(new KeyFrame(Duration.millis(150), actionEvent -> onTimerLeft()));
        timelineLeft.setCycleCount(Animation.INDEFINITE);

        moveLeftButton.armedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                timelineLeft.play();
            } else {
                timelineLeft.stop();
            }
        });

        timelineRight = new Timeline(new KeyFrame(Duration.millis(150), actionEvent -> onTimerRight()));
        timelineRight.setCycleCount(Animation.INDEFINITE);

        moveRightButton.armedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                timelineRight.play();
            } else {
                timelineRight.stop();
            }
        });

        timelineUp = new Timeline(new KeyFrame(Duration.millis(150), actionEvent -> onTimerUp()));
        timelineUp.setCycleCount(Animation.INDEFINITE);

        moveUpButton.armedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                timelineUp.play();
            } else {
                timelineUp.stop();
            }
        });

        timelineDown = new Timeline(new KeyFrame(Duration.millis(150), actionEvent -> onTimerDown()));
        timelineDown.setCycleCount(Animation.INDEFINITE);

        moveDownButton.armedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                timelineDown.play();
            } else {
                timelineDown.stop();
            }
        });

    }

    private void setImageToWork(ActionEvent event) {

        if(imageToWorkCB.getValue().equalsIgnoreCase("black&white image")) {
            imageToWork = new File("./src/main/images/Image_BW.jpg");
        } else if(imageToWorkCB.getValue().equalsIgnoreCase("white&black image")) {
            imageToWork = new File("./src/main/images/Image_WB.jpg");
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

    public void connectToArduino(ActionEvent event){

        if(arduinoController.connectDevice()) {

            deviceConnectionBar.setStyle("-fx-accent: #25e72c;");
            deviceConnectionBar.setProgress(1);

            moveHomeButton.setDisable(false);
            moveRightButton.setDisable(false);
            moveDownButton.setDisable(false);
            moveUpButton.setDisable(false);
            moveLeftButton.setDisable(false);

            deviceConLabel.setText("Successfully connected to the device.");
            deviceConLabel.setAlignment(Pos.CENTER);

        } else {
            deviceConLabel.setText("Unable to connect to the device.");
            deviceConLabel.setAlignment(Pos.CENTER);
        }

    }

    public void loadWorkImage(ActionEvent event) throws IOException {

        BufferedImage cameraImage = ImageIO.read(new File("./src/main/images/camera_photo.jpg"));
        BufferedImage cameraImageResized = ImageIO.read(new File("./src/main/images/camera_photo_resized.jpg"));

        workspaceImageView.setImage(new Image(new File("./src/main/images/camera_photo_resized.jpg").toURI().toString()));

        if(workspacePixelArray != null) {

            workspaceImageView.setOnMouseClicked(ev -> {


                Image image = new Image(imageToWork.toURI().toString());

                double x = ev.getX();
                double y = ev.getY();

                if(workImageSizeX != null && workImageSizeY != null) {

                    boolean isInZone = false;

                    double resizeRatio = (double) cameraImage.getWidth() / cameraImageResized.getWidth();
                    int workImageX = workImageSizeX.getValue();
                    int workImageY = workImageSizeY.getValue();

                    int newX = (int)(x * resizeRatio);
                    int newY = (int)(y * resizeRatio);

                    if(workspacePixelArray[newX][newY] == 1) {

                        int workImageResized = (int)(workspaceDimensions[0][1] / resizeRatio);

                        for(int i = newX; i < (newX + workImageX); i++) {
                            isInZone = workspacePixelArray[i][newY] == 1;
                        }

                        for(int j = newY; j < (newY + workImageY); j++) {
                            isInZone = workspacePixelArray[newX][j] == 1;
                        }

                        if(isInZone) {
                            workImageView.setFitWidth(workImageX);
                            workImageView.setFitHeight(workImageY);
                            workImageView.setLayoutX(14 + x);
                            workImageView.setLayoutY(179 + y);
                            workImageView.setImage(image);

                            gridWorkImageView.setFitWidth(workImageX);
                            gridWorkImageView.setFitHeight(workImageY);
                            gridWorkImageView.setLayoutX(x - ((int)(workspaceDimensions[0][0]/resizeRatio)));
                            gridWorkImageView.setLayoutY(y - ((int)(workspaceDimensions[1][0]/resizeRatio)));
                            gridWorkImageView.setImage(image);

                            workImageCoordinates[0] = newX;
                            workImageCoordinates[1] = newY;

                        }

                    }

                }
            });
        }
    }

    public void detectWorkspace(ActionEvent event) {

        try {
            BufferedImage workspaceImage = ImageIO.read(new File("./src/main/images/camera_photo.jpg"));
            workspacePixelArray = new int[workspaceImage.getWidth()][workspaceImage.getHeight()];

            workspacePixelArray = ImageController.imageToPixelsArray(workspaceImage);

            int rowSumY1 = 0;
            int rowSumY2 = 0;
            int currentPixel;
            int X1 = 0, Y1 = 0, X2 = 0, Y2 = 0;
            int columnSumX1 = 0;
            int columnSumX2 = 0;

            for(int k = 0; k < workspaceImage.getHeight(); k++) {

                for(int l = 0; l < workspaceImage.getWidth(); l++) {

                    currentPixel = workspacePixelArray[l][k];

                    if(k == 0 && currentPixel == 1) rowSumY1 += currentPixel;
                    if(k > 0 && currentPixel == 1) rowSumY2 += currentPixel;
                }

                if(k > 0) {

                    if(rowSumY1 == 0 && rowSumY2 > rowSumY1) {
                        Y1 = k;
                    }

                    if(rowSumY2 == 0 && rowSumY1 > 0) {
                        Y2 = k;
                    }

                    rowSumY1 = rowSumY2;
                    rowSumY2 = 0;

                }
            }

            for(int m = 0; m < workspaceImage.getWidth(); m++) {

                for(int n = 0; n < workspaceImage.getHeight(); n++) {

                    currentPixel = workspacePixelArray[m][n];

                    if(m == 0 && currentPixel == 1) columnSumX1 += currentPixel;
                    if(m > 0 && currentPixel == 1) columnSumX2 += currentPixel;

                }

                if(m > 0) {

                    if(columnSumX1 == 0 && columnSumX2 > columnSumX1) {
                        X1 = m;
                    }

                    if(columnSumX2 == 0 && columnSumX1 > 0) {
                        X2 = m;
                    }

                    columnSumX1 = columnSumX2;
                    columnSumX2 = 0;

                }

            }
            workspaceDimensions = new int[2][2];
            workspaceDimensions[0][0] = X1;
            workspaceDimensions[0][1] = X2;
            workspaceDimensions[1][0] = Y1;
            workspaceDimensions[1][1] = Y2;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void confirmMotionParams(ActionEvent event) {

        motionController = new MotionController(beltPitchSpinner.getValue(),
                                                pulleyNOTSpinner.getValue(),
                                                microstepsSpinner.getValue(),
                                                stepsPerRevSpinner.getValue(),
                                                webcam);
    }

    public void setHomePosition(ActionEvent event) {

        if(arduinoController != null && arduinoController.isArduinoConnected()) {
            motionController.setArduinoController(arduinoController);
        }

    }

    public void startCameraProcess(ActionEvent event) throws IOException, InterruptedException {

        if(motionController.cameraProcess()) {
            mergeImages();
            startEngravingProcessButton.setDisable(false);
        }

    }

    public void startEngravingProcess(ActionEvent event) throws IOException, InterruptedException {

        if(confirmProcessCB.isSelected()) {

            BufferedImage originalImage = ImageIO.read(imageToWork);
            BufferedImage resizedImageToWork = ImageController.resizeImage(originalImage,
                                                                        workImageSizeX.getValue(),
                                                                        workImageSizeY.getValue());

            motionController.engravingProcess(resizedImageToWork, workImageCoordinates);

        }

    }

    public void moveHome(ActionEvent event) {
        motionController.moveHome();
    }

    private void onTimerLeft() {
        motionController.moveX(true);
    }

    private void onTimerRight() {
        motionController.moveX(false);
    }

    private void onTimerUp() {
        motionController.moveY(true);
    }

    private void onTimerDown() {
        motionController.moveY(false);
    }

    private void mergeImages() throws IOException {

        ArrayList<String> list = new ArrayList<>();
        list.add("./src/main/images/image1.jpg");
        list.add("./src/main/images/image2.jpg");
        list.add("./src/main/images/image3.jpg");
        list.add("./src/main/images/image4.jpg");
        list.add("./src/main/images/image5.jpg");
        list.add("./src/main/images/image6.jpg");
        list.add("./src/main/images/image7.jpg");
        list.add("./src/main/images/image8.jpg");
        list.add("./src/main/images/image9.jpg");
        list.add("./src/main/images/image10.jpg");
        list.add("./src/main/images/image11.jpg");
        list.add("./src/main/images/image12.jpg");

        BufferedImage img = ImageIO.read(new File(list.get(0)));
        int newWidth = img.getWidth()*3, newHeight = img.getHeight()*4;

        BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = newImg.getGraphics();

        int x = 0, y = 0, k = 0;
        BufferedImage bi = null;

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 3; j++) {
                bi = ImageIO.read(new File(list.get(k)));
                g.drawImage(bi, x, y, null);
                x += bi.getWidth();
                k++;
            }
            y += bi.getHeight();
            x = 0;
        }

        ImageController.saveJpg(newImg, "./src/main/images/camera_photo");
        ImageController.saveJpg(ImageController.resizeImage(newImg, 600, 253), "./src/main/images/camera_photo_resized");
    }

}
