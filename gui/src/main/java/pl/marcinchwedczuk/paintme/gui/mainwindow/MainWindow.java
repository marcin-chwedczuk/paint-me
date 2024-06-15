package pl.marcinchwedczuk.paintme.gui.mainwindow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pl.marcinchwedczuk.paintme.domain.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {
    public static MainWindow showOn(Stage window) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("MainWindow.fxml"));

            Scene scene = new Scene(loader.load());
            MainWindow controller = (MainWindow) loader.getController();

            window.setTitle("Main Window");
            window.setScene(scene);
            window.setResizable(true);

            window.show();

            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Rectangle horizontalResize;

    @FXML
    private Rectangle verticalResize;

    @FXML
    private Rectangle cornerResize;

    @FXML
    private ImageView canvas;

    @FXML
    private GridPane canvasPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane.setFillHeight(canvas, true);
        GridPane.setFillWidth(canvas, true);
    }

}
