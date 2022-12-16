import com.github.sarxos.webcam.Webcam;
import org.ardulink.core.Pin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MotionController {

    //Home position
    public static final double HOME_X = 0;
    public static final double HOME_Y = 0;

    private final int BELT_PITCH;
    private final int PULLY_NOT;
    private final int MICROSTEP;
    private final int STEP_PER_REV;

    private final int STEPS_PER_MM;
    private final double MM_PER_STEP;

    public static final double PIXEL_PER_MM = 0.26458;

    private ArduinoController arduinoController;
    private final Webcam webcam;

    private double currentPositionX;
    private double currentPositionY;

    private final double CAMERA_RECTANGLE_X = 137.2;
    private final double CAMERA_RECTANGLE_Y = 75;

    private final Pin.DigitalPin pinX = Pin.digitalPin(ArduinoController.STEPX);
    private final Pin.DigitalPin pinY = Pin.digitalPin(ArduinoController.STEPY);


    public MotionController(int BELT_PITCH, int PULLEY_NOT, int MICROSTEP, int STEP_PER_REV, Webcam webcam) {
        this.BELT_PITCH = BELT_PITCH;
        this.PULLY_NOT = PULLEY_NOT;
        this.MICROSTEP = MICROSTEP;
        this.STEP_PER_REV = STEP_PER_REV;
        this.webcam = webcam;
        STEPS_PER_MM = calculateStepsPerMM();
        MM_PER_STEP = (double) 1/STEPS_PER_MM;
    }

    public void moveHome() {
        try {
            returnToHome();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void moveX(boolean direction) {
        try {
            arduinoController.getArduinoConnection().switchDigitalPin(pinX, direction);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void moveY(boolean direction) {
        try {
            arduinoController.getArduinoConnection().switchDigitalPin(pinY, direction);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public boolean cameraProcess() throws IOException, InterruptedException {

        //Step1
        move(18.7, pinX, true);
        TimeUnit.MICROSECONDS.sleep(500);
        move(CAMERA_RECTANGLE_Y/2, pinY, false);
        takeImage("./src/main/images/camera-photos/image1");

        //Step2
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image2");

        //Step3
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image3");

        //Step4
        move(CAMERA_RECTANGLE_X*2, pinX, true);
        TimeUnit.MICROSECONDS.sleep(500);
        move(CAMERA_RECTANGLE_Y, pinY, false);
        takeImage("./src/main/images/camera-photos/image4");

        //Step5
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image5");

        //Step6
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image6");

        //Step7
        move(CAMERA_RECTANGLE_X*2, pinX, true);
        TimeUnit.MICROSECONDS.sleep(500);
        move(CAMERA_RECTANGLE_Y, pinY, false);
        takeImage("./src/main/images/camera-photos/image7");

        //Step8
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image8");

        //Step9
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image9");

        //Step10
        move(CAMERA_RECTANGLE_X*2, pinX, true);
        TimeUnit.MICROSECONDS.sleep(500);
        move(CAMERA_RECTANGLE_Y, pinY, false);
        takeImage("./src/main/images/camera-photos/image10");

        //Step11
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image11");

        //Step12
        move(CAMERA_RECTANGLE_X, pinX, false);
        takeImage("./src/main/images/camera-photos/image12");

        //Step13 - return home
        returnToHome();

        //Step14 - check if all photos are in camera-photos folder
        File photoDir = new File("./src/main/images/camera-photos/");

        if(photoDir != null && photoDir.list().length == 12) {
            return true;
        } else return false;

    }

    public void engravingProcess(BufferedImage image, double[] workImageCoordinatesInPixels) throws IOException, InterruptedException {

        double workImageX = Math.round((workImageCoordinatesInPixels[0] * PIXEL_PER_MM) * 10) / 10.0;
        double workImageY = Math.round((workImageCoordinatesInPixels[1] * PIXEL_PER_MM) * 10) / 10.0;

        //Set HOME position
        returnToHome();

        //Moves to image position
        move(workImageX, pinX, false);
        TimeUnit.MILLISECONDS.sleep(500);
        move(workImageY, pinY, false);

        //Engraving process
        int[][] pixelsArray = ImageController.imageToPixelsArray(image);

        for(int i = 0; i < image.getHeight(); i++) {

            move(0.125, pinY, false);
            TimeUnit.MILLISECONDS.sleep(100);

            for(int j = 0; j < image.getWidth(); j++) {

                int pixel = pixelsArray[j][i];

                switch (pixel) {
                    case 0 -> {
                        move(0.0375, pinX, false);
                        //laser.engrave()
                        TimeUnit.MILLISECONDS.sleep(100);
                        move(0.0875, pinX, false);
                        //laser.engrave()
                        TimeUnit.MILLISECONDS.sleep(100);
                        move(0.0875, pinX, false);
                        //laser.engrave()
                        TimeUnit.MILLISECONDS.sleep(100);
                        move(0.05, pinX, false);
                    }
                    case 1 -> move(0.2625, pinX, false);
                }

            }

            TimeUnit.MILLISECONDS.sleep(100);
            move(image.getWidth()*0.2625, pinX, true);

        }

        returnToHome();

    }

    public void setArduinoController(ArduinoController controller) {
        this.arduinoController = controller;
    }

    private int calculateStepsPerMM() {
        return (MICROSTEP*STEP_PER_REV)/(BELT_PITCH*PULLY_NOT);
    }

    private void takeImage(String fileName) throws InterruptedException {

        TimeUnit.SECONDS.sleep(5);

        BufferedImage takenImage = webcam.getImage();
        ImageController.saveJpg(takenImage, fileName);

        TimeUnit.SECONDS.sleep(1);

    }

    /**
     *
     * @return <br>
     * Boolean array which contains information<br>
     * if engraver should move left/right [0] or up/down [1]
     */
    private boolean[] isCurrentPositionInZone() {

        boolean[] directionsInZone = new boolean[2];

        directionsInZone[0] = currentPositionX > 0;
        directionsInZone[1] = currentPositionY > 0;

        return directionsInZone;

    }

    private void move(double MM_TO_MOVE, Pin.DigitalPin pin, boolean direction) throws IOException {

        for(int i = 0; i < (MM_TO_MOVE / MM_PER_STEP); i++) {
            arduinoController.getArduinoConnection().switchDigitalPin(pin, direction);
            changeCurrentPosition(pin.pinNum(), direction);
        }

    }

    /**
     *
     * @param dimension 2 - X, 3 - Y
     * @param direction true=left/up, false=right/down
     */
    private void changeCurrentPosition(int dimension, boolean direction) {

        switch (dimension) {
            case 2:
                if(direction) currentPositionX -= MM_PER_STEP;
                else currentPositionX += MM_PER_STEP;
                break;
            case 3:
                if(direction) currentPositionY -= MM_PER_STEP;
                else currentPositionY += MM_PER_STEP;
                break;
        }
    }

    /**
     * Moves engraver to HOME position
     */
    private void returnToHome() throws IOException, InterruptedException {

        boolean[] zoneDimension = isCurrentPositionInZone();

        move(currentPositionX, pinX, zoneDimension[0]);
        TimeUnit.MICROSECONDS.sleep(500);
        move(currentPositionY, pinY, zoneDimension[1]);

    }

}
