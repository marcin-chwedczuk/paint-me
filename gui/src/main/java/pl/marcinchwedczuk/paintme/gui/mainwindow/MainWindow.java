package pl.marcinchwedczuk.paintme.gui.mainwindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import pl.marcinchwedczuk.paintme.gui.easel.Easel;

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
    private Canvas drawingCanvas;

    @FXML
    private Canvas previewCanvas;

    @FXML
    private StackPane drawingCanvasContainer;

    @FXML
    private Easel easel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialized");

        // TODO: Important timeing, this must change only as the result of canvas container
        // change, not the zoom change
        drawingCanvasContainer.widthProperty().addListener((o, oldW, newW) -> {
            drawingCanvas.setWidth(newW.doubleValue() / easel.getZoom());
            previewCanvas.setWidth(newW.doubleValue() / easel.getZoom());
        });
        drawingCanvasContainer.heightProperty().addListener((o, oldW, newW) -> {
            drawingCanvas.setHeight(newW.doubleValue() / easel.getZoom());
            previewCanvas.setHeight(newW.doubleValue() / easel.getZoom());
        });

        previewCanvas.setOnMouseMoved(e -> {
            System.out.println("MouseMoved");

            clearPreview();

            GraphicsContext ctx = previewCanvas.getGraphicsContext2D();
            ctx.setFill(Color.BLACK);
            ctx.fillOval(e.getX() - 5, e.getY() - 5, 10, 10);
        });

        previewCanvas.setOnMouseExited(e -> {
            clearPreview();
        });

        previewCanvas.setOnMouseClicked(e -> {
            clearPreview();

            GraphicsContext ctx = drawingCanvas.getGraphicsContext2D();
            ctx.setFill(Color.BLACK);
            ctx.fillOval(e.getX() - 5, e.getY() - 5, 10, 10);
        });
    }

    private void clearPreview() {
        GraphicsContext ctx = previewCanvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, previewCanvas.getWidth(), previewCanvas.getHeight());
    }

    @FXML
    void buttonTest(ActionEvent event) {
    }


    @FXML
    void zoomButton(ActionEvent event) {
        if (previewCanvas.getTransforms().isEmpty()) {
            previewCanvas.getTransforms().add(new Scale(4, 4, 0, 0));
            drawingCanvas.getTransforms().add(new Scale(4, 4, 0, 0));
            easel.setZoom(4);
        } else {
            previewCanvas.getTransforms().clear();
            drawingCanvas.getTransforms().clear();
            easel.setZoom(1);
        }
    }
}
