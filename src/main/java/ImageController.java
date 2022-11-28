import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    @FXML
    private ImageView loadedImageView;

    @FXML
    private ImageView BWImageView;

    @FXML
    private ImageView WBImageView;

    @FXML
    private HBox hbox;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadedImageView.setImage(new Image(new File("./src/main/images/loaded_image.jpg").toURI().toString()));
            progressBar.setStyle("-fx-accent: #25e72c;");
            progressBar.setProgress(1);
            progressLabel.setText("Image properly loaded");
            progressLabel.setAlignment(Pos.CENTER);

            BWImageView.setImage(new Image(new File("./src/main/images/Image_BW.jpg").toURI().toString()));
            WBImageView.setImage(new Image(new File("./src/main/images/Image_WB.jpg").toURI().toString()));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

    public void loadImage(ActionEvent event){

        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG file", "*.jpg"));

        File file = chooser.showOpenDialog(null);

        File saveFile = new File("./src/main/images/loaded_image.jpg");

        try {

            BufferedImage bufferedImage = ImageIO.read(file);

            if(!isEmpty(Path.of("/src/main/images/"))) {
                FileUtils.cleanDirectory(new File("src/main/images/"));
            }

            ImageIO.write(bufferedImage, "jpg", saveFile);

            Image image = new Image(saveFile.toURI().toString());
            loadedImageView.setImage(image);

            progressBar.setStyle("-fx-accent: #25e72c;");
            progressBar.setProgress(1);
            progressLabel.setText("Image properly loaded");
            progressLabel.setAlignment(Pos.CENTER);

        } catch (Exception e) {
            progressBar.setStyle("-fx-accent: #B50015;");
            progressBar.setProgress(1);
            progressLabel.setText("Unable to load the image");
            progressLabel.setAlignment(Pos.CENTER);
        }
    }

    public void convertLoadedImage(ActionEvent event) {

        try {
            BufferedImage bufferedImage = ImageIO.read(new File("./src/main/images/loaded_image.jpg"));

            if(saveJpg(convertToBW(bufferedImage), "./src/main/images/Image_BW")) {

                BWImageView.setImage(new Image(new File("./src/main/images/Image_BW.jpg").toURI().toString()));

            } else System.out.println(new Date(System.currentTimeMillis()) + ": Could not convert image to bw [012]");

            if(saveJpg(reverseBW(convertToBW(bufferedImage)), "./src/main/images/Image_WB")) {

                WBImageView.setImage(new Image(new File("./src/main/images/Image_WB.jpg").toURI().toString()));

            } else System.out.println(new Date(System.currentTimeMillis()) + ": Could not convert image to wb [013]");

        } catch (Exception e) {
            System.out.println(new Date(System.currentTimeMillis()) + ": Could not convert image [011]");
        }

    }

    public void closeImageWindow(ActionEvent event) {

        Stage stage = (Stage) loadedImageView.getScene().getWindow();
        stage.close();

    }

    public boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            }
        }

        return false;
    }

    public static BufferedImage convertToBW(BufferedImage img) {

        BufferedImage blackWhite = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2d = blackWhite.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return blackWhite;
    }

    public static BufferedImage reverseBW(BufferedImage img) {

        BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = newImg.createGraphics();
        g2d.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
        g2d.dispose();

        for(int i = 0; i < newImg.getHeight(); i++) {

            for(int j = 0; j < newImg.getWidth(); j++) {

                int pixel = new Color(newImg.getRGB(j, i)).getRed();

                switch (pixel) {
                    case 255 -> newImg.setRGB(j, i, new Color(0, 0, 0).getRGB());
                    case 0 -> newImg.setRGB(j, i, new Color(255, 255, 255).getRGB());
                }

            }
        }
        return newImg;

    }

    public static boolean saveJpg(BufferedImage image, String filename) {

        try{
            if(ImageIO.write(image, "jpg", new File(filename + ".jpg"))) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}
