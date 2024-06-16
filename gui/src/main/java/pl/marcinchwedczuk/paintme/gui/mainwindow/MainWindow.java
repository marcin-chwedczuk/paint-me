package pl.marcinchwedczuk.paintme.gui.mainwindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {
    public static MainWindow showOn(Stage window) {
        try {
            FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("MainWindow.fxml"));

            loader.setClassLoader(MainWindow.class.getClassLoader());
            Scene scene = new Scene(loader.load());
            MainWindow controller = (MainWindow) loader.getController();

            window.setTitle("Main Window");
            window.setScene(scene);
            window.setResizable(true);
            window.setWidth(480);
            window.setHeight(400);

            window.show();

            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private ImageView drawingCanvas;
    @FXML
    private Pane drawingCanvasContainer;

    private WritableImage image;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized");

        drawingCanvasContainer.widthProperty().addListener((o, oldValue, newValue) -> {
            resizeDrawingCanvas();
        });
        drawingCanvasContainer.heightProperty().addListener((o, oldValue, newValue) -> {
            resizeDrawingCanvas();
        });

        drawingCanvasContainer.setOnMouseClicked(e -> {
            drawRandom(e.getX(), e.getY());
        });

        drawingCanvasContainer.setOnMouseMoved(e -> {
            //if (e.getButton() == MouseButton.PRIMARY) {
                drawRandom(e.getX(), e.getY());
            //}
        });

        resizeDrawingCanvas();
    }

    @FXML
    void buttonTest(ActionEvent event) {
    }

    private void resizeDrawingCanvas() {
        double w = Math.max(1, drawingCanvasContainer.getWidth());
        double h = Math.max(1, drawingCanvasContainer.getHeight());

        image = new WritableImage((int)w, (int)h);
        drawingCanvas.setImage(image);
    }

    private void drawRandom(double x, double y) {
        image.getPixelWriter().setColor((int)x, (int)y, Color.BLACK);
    }
}
