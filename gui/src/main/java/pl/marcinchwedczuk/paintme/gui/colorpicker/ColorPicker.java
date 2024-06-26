package pl.marcinchwedczuk.paintme.gui.colorpicker;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

// Docs: https://learn.microsoft.com/en-us/windows/win32/dlgbox/color-dialog-box
public class ColorPicker extends HBox {

    private final SimpleObjectProperty<HslColor> colorProperty = new SimpleObjectProperty<>(this, "color", HslColor.ofRgb(50, 50, 50));

    private final Canvas hueSaturationArea;
    private final Canvas luminanceBar;
    private final Polygon triangleLuminancePointer;

    public ColorPicker() {
        getStyleClass().add("color-picker");

        hueSaturationArea = new Canvas();
        hueSaturationArea.setWidth(239);
        hueSaturationArea.setHeight(240);

        var hueSaturationAreaPane = new Pane(hueSaturationArea);
        hueSaturationAreaPane.getStyleClass().add("hue-saturation-area");

        luminanceBar = new Canvas();
        luminanceBar.setWidth(10);
        luminanceBar.setHeight(240);
        luminanceBar.setMouseTransparent(true);

        triangleLuminancePointer = new Polygon(0, 0, 1, 1, 1, -1);
        triangleLuminancePointer.setMouseTransparent(true);
        Translate moveY = new Translate(0, 0);
        moveY.yProperty().bind(colorProperty.map(c -> 240 - c.luminance()));
        triangleLuminancePointer.getTransforms().addAll(moveY, new Scale(5, 5));

        var luminanceBarAreaPane = new HBox(luminanceBar, triangleLuminancePointer);
        luminanceBarAreaPane.getStyleClass().add("luminance-area");

        getChildren().addAll(hueSaturationAreaPane, luminanceBarAreaPane);

        redrawHueSaturationArea(120, 120);
        redrawLuminanceBar();

        hueSaturationArea.setOnMouseClicked(e -> {
            int x = (int) Math.round(e.getX());
            int y = (int) Math.round(e.getY());

            int hue = Math.clamp(x, 0, 239);
            int saturation = Math.clamp(240 - y, 0, 240);

            HslColor currentColor = colorProperty.get();
            HslColor newColor = currentColor.withHue(hue).withSaturation(saturation);
            colorProperty.set(newColor);

            redrawHueSaturationArea(x, y);
            redrawLuminanceBar();
        });

        luminanceBarAreaPane.setOnMouseClicked(e -> {
            int y = (int) Math.round(e.getY());

            int luminance = Math.clamp(240 - y, 0, 240);

            HslColor currentColor = colorProperty.get();
            HslColor newColor = currentColor.withLuminance(luminance);
            colorProperty.set(newColor);
        });
    }

    private void redrawHueSaturationArea(int clickX, int clickY) {
        GraphicsContext c2d = hueSaturationArea.getGraphicsContext2D();

        int step = 4;
        for (int sat = 0; sat <= 240; sat += step) {
            for (int hue = 0; hue <= 239; hue += step) {
                // TODO: Consider using dithering as they do in original Color Picker
                Color color = HslColor.ofHsl(hue, sat, 120).toColor();

                c2d.setFill(color);
                c2d.fillRect(hue, 240 - sat, step, step);
            }
        }

        drawSniperMark(c2d, clickX, clickY);
    }

    private static void drawSniperMark(GraphicsContext c2d, int x, int y) {
        int size = 4;
        int halfSize = size / 2;

        c2d.setFill(Color.BLACK);

        // top
        c2d.fillRect(x - halfSize, y - halfSize - 3 * size, size, size * 2);
        // bottom
        c2d.fillRect(x - halfSize, y + halfSize + size, size, size * 2);
        // left
        c2d.fillRect(x - 3 * size - halfSize, y - halfSize, 2 * size, size);
        // right
        c2d.fillRect(x + size + halfSize, y - halfSize, 2 * size, size);
    }

    private void redrawLuminanceBar() {
        GraphicsContext c2d = luminanceBar.getGraphicsContext2D();
        double w = luminanceBar.getWidth();

        HslColor color = colorProperty.get();

        int step = 8;
        for (int lum = 0; lum <= 240; lum += step) {
            c2d.setFill(color.withLuminance(240 - lum).toColor());
            c2d.fillRect(0, lum, w, step);
        }
    }

    public ObjectProperty<HslColor> colorProperty() {
        return this.colorProperty;
    }
}
