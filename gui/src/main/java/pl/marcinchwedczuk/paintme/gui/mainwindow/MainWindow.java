package pl.marcinchwedczuk.paintme.gui.mainwindow;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import pl.marcinchwedczuk.paintme.gui.easel.Easel;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        easel.addCustomEventHandler(e -> {
            // TODO: Fix this shit
            double newWidth = drawingCanvasContainer.getMaxWidth();
            double newHeight = drawingCanvasContainer.getMaxHeight();

            System.out.printf("New layout W: %f H: %f %n", newWidth, newHeight);

            drawingCanvas.setWidth(newWidth / easel.getZoom());
            previewCanvas.setWidth(newWidth / easel.getZoom());

            drawingCanvas.setHeight(newHeight / easel.getZoom());
            previewCanvas.setHeight(newHeight / easel.getZoom());
        });

        previewCanvas.setOnMouseMoved(e -> {
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

            System.out.printf("Circle at %f %f %n", e.getX(), e.getY());
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

    private TextArea textPreview;
    private Text helper = new Text();

    @FXML
    void textButton(ActionEvent event) {
        textPreview = new TextArea();
        textPreview.setBorder(Border.EMPTY);
        textPreview.setPrefColumnCount(20);
        textPreview.setPrefRowCount(2);
        textPreview.setWrapText(true);
        textPreview.setStyle(
                "-fx-padding: 0; " +
                        "-fx-background-insets: 0; " +
                        "-fx-border-insets: 0;"
        );

        StackPane.setAlignment(helper, Pos.TOP_LEFT);

        var customSkin = new TextAreaSkin(textPreview) {
            public Text getTextNode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                Method method = TextAreaSkin.class.getDeclaredMethod("getTextNode");
                // Allow access to the method (make it accessible)
                method.setAccessible(true);
                // Invoke the method
                return (Text) method.invoke(this);
            }
        };

        textPreview.setSkin(customSkin);

        textPreview.setTextFormatter(new TextFormatter<String>(change -> {
            if (change.isAdded() || change.isReplaced() || change.isContentChange()) {
                helper.setFont(textPreview.getFont());
                helper.setText(change.getControlNewText());
                helper.setWrappingWidth(textPreview.getLayoutBounds().getWidth());
                helper.setTextOrigin(VPos.TOP);
                helper.setFontSmoothingType(FontSmoothingType.LCD);
                helper.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);

                try {
                System.out.println("TEXT NODE: " + customSkin.getTextNode());
                System.out.println("TEXT NODE: " + helper);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                double w = helper.getLayoutBounds().getWidth();
                double h = helper.getLayoutBounds().getHeight();

                if (w > textPreview.getLayoutBounds().getWidth() || h > textPreview.getLayoutBounds().getHeight()) {
                    System.out.printf("Stop change %n");
                    return null;
                }
            }

            return change;
        }));

        drawingCanvasContainer.getChildren().addAll(helper, textPreview);

        textPreview.setText("TEST");

        textPreview.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                drawingCanvasContainer.getChildren().remove(textPreview);

                // Draw text on canvas
                var params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                WritableImage textImg = textPreview.snapshot(params, null);

                GraphicsContext c2d = drawingCanvas.getGraphicsContext2D();
                // Low quality shit:
                // c2d.drawImage(textImg, 0, 0);

                c2d.setFill(Color.YELLOWGREEN);
                c2d.fillRect(0, 0, 5, 5);
                c2d.fillRect(20, 20, 5, 5);

                c2d.setFill(Color.YELLOW);
                c2d.setFont(textPreview.getFont());
                c2d.setTextBaseline(VPos.TOP);
                c2d.fillText(textPreview.getText(), 1, 1);
            }
        });
    }
}
