import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    @FXML
    public void connectionConfigure(ActionEvent event) {
        System.out.println("TESTSTESTST");
    }

    @FXML
    public void imageAdd(ActionEvent event){

        try {

            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("imageAdd.fxml"));
            Scene scene = new Scene(root);

            stage.setResizable(false);
            stage.setTitle("Program by Filip Majewksi");

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
