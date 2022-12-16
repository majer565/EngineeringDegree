import org.ardulink.core.Link;
import org.ardulink.core.convenience.Links;

public class ArduinoController {

    public static final byte STEPX = 2;
    public static final byte STEPY = 3;
    public static final byte STEPZ = 4;

    public static final byte DIRX = 5;
    public static final byte DIRY = 6;
    public static final byte DIRZ = 7;

    public static final byte ENABLE_PIN = 8;

    /**
        NEMA 17 stepper motor need 200 steps for revolution
        additional jumpers can increase this value
        for this project I use 3 jumpers
     */
    public static final int STEPS = 3200;

    private Link arduinoConnection;

    private boolean isArduinoConnected = false;

    public boolean connectDevice() {
        try {

            arduinoConnection = Links.getDefault();
            isArduinoConnected = true;
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isArduinoConnected() {
        return isArduinoConnected;
    }

    public Link getArduinoConnection() {
        return arduinoConnection;
    }
}
