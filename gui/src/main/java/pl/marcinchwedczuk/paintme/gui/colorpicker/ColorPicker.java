package pl.marcinchwedczuk.paintme.gui.colorpicker;


import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import pl.marcinchwedczuk.paintme.gui.extra.BeveledPane;

import java.util.Random;

public class ColorPicker extends HBox {

    private final Canvas hueSaturationArea;
    private final Canvas luminanceBar;
    private final Polygon triangleLuminancePointer;

    public ColorPicker() {
        hueSaturationArea = new Canvas();
        hueSaturationArea.setWidth(239);
        hueSaturationArea.setHeight(240);

        hueSaturationArea.setOnMouseClicked(e -> {
            var r = new Random();
            hueSaturationArea.setWidth(140 + r.nextInt(100));
            hueSaturationArea.setHeight(140 + r.nextInt(100));

            GraphicsContext xxx = hueSaturationArea.getGraphicsContext2D();
            xxx.setFill(Color.color(r.nextDouble(), r.nextDouble(), r.nextDouble()));
            xxx.fillRect(0, 0, hueSaturationArea.getWidth(), hueSaturationArea.getHeight());
        });

        var hueSaturationAreaPane = new BeveledPane(hueSaturationArea);

        // Define the colors for the border
        Color lightGray = Color.rgb(192, 192, 192);
        Color darkGray = Color.rgb(128, 128, 128);
        Color white = Color.rgb(255, 255, 255);
        Color black = Color.rgb(0, 0, 0);

        // Create a Border with different colors for each side to mimic the 3D effect
        Border border = new Border(new BorderStroke(
                black, darkGray, white, lightGray,
                BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(3), new Insets(20, 30, 20, 30)
        ));
        // hueSaturationAreaPane.setBorder(border);

        luminanceBar = new Canvas();
        luminanceBar.setWidth(20);
        luminanceBar.setHeight(240);

        triangleLuminancePointer = new Polygon(0, 0, 1, 1, 1, -1);
        triangleLuminancePointer.getTransforms().add(new Scale(20, 20));

        getChildren().addAll(hueSaturationAreaPane, luminanceBar, triangleLuminancePointer);

        drawHueSaturationArea();
    }

    private void drawHueSaturationArea() {
        GraphicsContext c2d = hueSaturationArea.getGraphicsContext2D();

        c2d.setFill(Color.MAGENTA);
        c2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int sat = 0; sat <= 240; sat++) {
            for (int hue = 0; hue <= 239; hue++) {
                // TODO: Draw 4x4 pixel boxes
                // TODO: Consider using dithering as they do in original Color Picker
                Color color = HslColor.ofHsl(hue / 4 * 4, 120, sat / 4 * 4).toColor();

                c2d.setFill(color);
                c2d.fillRect(hue, 240 - sat, 1, 1);
            }
        }
    }
}
