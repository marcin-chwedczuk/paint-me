package pl.marcinchwedczuk.paintme.gui.mainwindow;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import pl.marcinchwedczuk.paintme.domain.Util;
import pl.marcinchwedczuk.paintme.gui.dragdrop.HelloDragAndDrop;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

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
    private Pane canvas;

    @FXML
    private GridPane canvasPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load the background image
        Image image = new Image(MainWindow.class.getResourceAsStream("sample.png"));

        // Create a BackgroundImage
        BackgroundImage backgroundImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                null,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        // Create a Background with the BackgroundImage
        Background background = new Background(backgroundImage);

        // Create a Pane
        canvas.setBackground(background);

        double[] lastPosition = new double[] { 0, 0 };
        double[] lastSize = new double[] { 256, 265 };

        canvas.setPrefWidth(lastSize[0]);
        canvas.setPrefHeight(lastSize[1]);

        AtomicBoolean draging = new AtomicBoolean(false);

        cornerResize.setOnDragDetected(event -> {
            /* allow any transfer mode */
            cornerResize.startFullDrag();

            // Set a custom image for the drag view
            // Image dragViewImage = new Image(HelloDragAndDrop.class.getResourceAsStream("icons8-drag-100.png"));
            // db.setDragView(dragViewImage);

            lastPosition[0] = event.getSceneX();
            lastPosition[1] = event.getSceneY();
            lastSize[0] = canvas.getPrefWidth();
            lastSize[1] = canvas.getPrefHeight();
            System.out.printf("setOnDragDetected x:%f, y:%f %n", lastPosition[0], lastPosition[1]);

            draging.set(true);
            event.consume();
        });


        cornerResize.setOnMouseDragged(event -> {
            if (!draging.get()) return;

            System.out.printf("setOnMouseDragged %n");

            double deltaX = event.getSceneX() - lastPosition[0];
            double deltaY = event.getSceneY() - lastPosition[1];

            System.out.printf("deltaX: %f, deltaY: %f %n", deltaX, deltaY);
            canvas.setPrefWidth(lastSize[0] + deltaX);
            canvas.setPrefHeight(lastSize[1] + deltaY);
        });


        // Handle mouse release to apply the divider position
        cornerResize.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            draging.set(false);

            System.out.printf("MOUSE_RELEASED x:%f, y:%f %n", event.getSceneX(), event.getSceneY());

            double deltaX = event.getSceneX() - lastPosition[0];
            double deltaY = event.getSceneY() - lastPosition[1];

            System.out.printf("deltaX: %f, deltaY: %f %n", deltaX, deltaY);
            // canvas.setFitWidth(lastSize[0] + deltaX);
            // canvas.setFitHeight(lastSize[1] + deltaY);
        });

        System.out.println("Initailized");
    }

}
