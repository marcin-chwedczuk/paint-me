package pl.marcinchwedczuk.paintme.gui.easel;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class EaselSkin extends SkinBase<Easel> {
    private static final int CANVAS_CELL_COL_INDEX = 1;
    private static final int CANVAS_CELL_ROW_INDEX = 1;

    private Region canvas;

    @FXML
    private Rectangle horizontalResize;

    @FXML
    private Rectangle verticalResize;

    @FXML
    private Rectangle cornerResize;

    @FXML
    private Rectangle previewRectangle;

    @FXML
    private GridPane canvasPane;

    public EaselSkin(Easel control) {
        super(control);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Easel.fxml"));
        fxmlLoader.setController(this);

        // Hack for scene builder ".fxml file not found" error.
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        ScrollPane scrollPane;
        try {
            scrollPane = fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException("Cannot load FXML file.", exception);
        }

        getChildren().add(scrollPane);

        // Register listeners
        registerChangeListener(control.canvasProperty(), (o, oldValue, newValue) -> {
            replaceCanvasNode(newValue);
        });

        // Run listeners
        replaceCanvasNode(control.getCanvas());

        init();

        System.out.println("Control initialized properly");
    }

    private void replaceCanvasNode(Region newCanvas) {
        canvasPane.getChildren().removeIf(node ->
                Objects.equals(GridPane.getColumnIndex(node), CANVAS_CELL_COL_INDEX) &&
                Objects.equals(GridPane.getRowIndex(node),    CANVAS_CELL_ROW_INDEX));

        if (newCanvas != null) {
            canvasPane.add(newCanvas, CANVAS_CELL_COL_INDEX, CANVAS_CELL_ROW_INDEX, 1, 1);
        }

        // TODO: Better handling for null's
        canvas = newCanvas == null ? new Pane() : newCanvas;
    }

    public void init() {

        double[] lastPosition = new double[] { 0, 0 };
        double[] lastSize = new double[] { canvas.getLayoutBounds().getWidth(), canvas.getLayoutBounds().getHeight() };

        getSkinnable().zoomProperty().addListener((observable, oldZoom, newZoom) -> {
            double newW = canvas.getLayoutBounds().getWidth() / oldZoom.doubleValue() * newZoom.doubleValue();
            double newH = canvas.getLayoutBounds().getHeight() / oldZoom.doubleValue() * newZoom.doubleValue();
            canvas.setMinSize(newW, newH);
            canvas.setMaxSize(newW, newH);

            getSkinnable().fireZoomedEvent();
        });

        AtomicBoolean draging = new AtomicBoolean(false);

        cornerResize.setOnDragDetected(event -> {
            /* allow any transfer mode */
            cornerResize.startFullDrag();

            lastPosition[0] = event.getSceneX();
            lastPosition[1] = event.getSceneY();
            lastSize[0] = canvas.getLayoutBounds().getWidth() / getSkinnable().getZoom() ;
            lastSize[1] = canvas.getLayoutBounds().getHeight() / getSkinnable().getZoom();
            System.out.printf("setOnDragDetected x:%f, y:%f %n", lastPosition[0], lastPosition[1]);

            draging.set(true);
            previewRectangle.setVisible(true);
            previewRectangle.setWidth(lastSize[0] * getSkinnable().getZoom());
            previewRectangle.setHeight(lastSize[1] * getSkinnable().getZoom());
            event.consume();
        });


        cornerResize.setOnMouseDragged(event -> {
            if (!draging.get()) return;

            System.out.printf("setOnMouseDragged %n");

            double deltaX = event.getSceneX() - lastPosition[0];
            double deltaY = event.getSceneY() - lastPosition[1];

            System.out.printf("deltaX: %f, deltaY: %f %n", deltaX, deltaY);
            // canvas.setPrefWidth(lastSize[0] + deltaX);
            // canvas.setPrefHeight(lastSize[1] + deltaY);
            previewRectangle.setWidth(lastSize[0] * getSkinnable().getZoom() + deltaX);
            previewRectangle.setHeight(lastSize[1] * getSkinnable().getZoom() + deltaY);
        });


        // Handle mouse release to apply the divider position
        cornerResize.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            draging.set(false);

            System.out.printf("MOUSE_RELEASED x:%f, y:%f %n", event.getSceneX(), event.getSceneY());

            double deltaX = event.getSceneX() - lastPosition[0];
            double deltaY = event.getSceneY() - lastPosition[1];

            previewRectangle.setVisible(false);
            previewRectangle.setWidth(0);
            previewRectangle.setHeight(0);

            double newWidth = lastSize[0] * getSkinnable().getZoom() + deltaX;
            double newHeight = lastSize[1] * getSkinnable().getZoom() + deltaY;

            System.out.printf("New size W: %f H: %f %n", newWidth, newHeight);

            canvas.setMaxSize(newWidth, newHeight);
            canvas.setMinSize(newWidth, newHeight);

            // TODO: Replace with better event e.g. canvas_size_changed
            getSkinnable().fireZoomedEvent();
        });

        System.out.println("Initailized");
    }

    private static <T> void registerChangeListener(ObservableValue<T> property, ChangeListener<? super T> listener) {
        property.addListener(listener);
    }
}
