
public class Main {

    private static boolean connectDeviceStatus;

    public static void main(String[] args) {
        JavaFXClass.main(args);
    }

    public static boolean isConnectDeviceStatus() {
        return connectDeviceStatus;
    }

    public static void setConnectDeviceStatus(boolean connectDeviceStatus) {
        Main.connectDeviceStatus = connectDeviceStatus;
    }
}
