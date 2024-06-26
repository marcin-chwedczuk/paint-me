package pl.marcinchwedczuk.paintme.gui.colorpicker;


import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import pl.marcinchwedczuk.paintme.gui.extra.BeveledPane;

// Docs: https://learn.microsoft.com/en-us/windows/win32/dlgbox/color-dialog-box
public class ColorPicker extends HBox {

    private final SimpleObjectProperty<HslColor> colorProperty = new SimpleObjectProperty<>(this, "color", HslColor.ofRgb(50, 50, 50));

    private final Canvas hueSaturationArea;
    private final Canvas luminanceBar;
    private final Polygon triangleLuminancePointer;

    public ColorPicker() {
        hueSaturationArea = new Canvas();
        hueSaturationArea.setWidth(239);
        hueSaturationArea.setHeight(240);

        int sniperSize = 8;
        int halfSS = sniperSize / 2;
        int oneHalfSS = sniperSize + halfSS;

        var sniperMark = new Group(
                // top
                new Rectangle(-halfSS, -halfSS - 3 * sniperSize, sniperSize, sniperSize * 2),

                // bottom
                new Rectangle(-halfSS, halfSS + sniperSize, sniperSize, sniperSize * 2),

                // left
                new Rectangle(-3 * sniperSize - halfSS, -halfSS, 2 * sniperSize, sniperSize),

                // right
                new Rectangle(sniperSize + halfSS, -halfSS, 2 * sniperSize, sniperSize)
        );

        Translate sniperMarkTranslation = new Translate(0, 0);
        // sniperMarkTranslation.xProperty().bind(colorProperty.map(HslColor::hue));
        // sniperMarkTranslation.yProperty().bind(colorProperty.map(HslColor::saturation));
        sniperMark.getTransforms().add(sniperMarkTranslation);

        var hueSaturationAreaPane = new StackPane(hueSaturationArea);

        luminanceBar = new Canvas();
        luminanceBar.setWidth(10);
        luminanceBar.setHeight(240);

        triangleLuminancePointer = new Polygon(0, 0, 1, 1, 1, -1);

        getChildren().addAll(hueSaturationAreaPane, luminanceBar, triangleLuminancePointer);

        redrawHueSaturationArea(120, 120);
        redrawLuminanceBar();

        Translate moveY = new Translate(0, 0);
        moveY.yProperty().bind(colorProperty.map(c -> 240 - c.luminance()));
        triangleLuminancePointer.getTransforms().addAll(moveY, new Scale(5, 5));

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

        luminanceBar.setOnMouseClicked(e -> {
            int y = (int) Math.round(e.getY());

            int luminance = Math.clamp(240 - y, 0, 240);

            System.out.println("LUMINANCE: " + luminance);
            HslColor currentColor = colorProperty.get();
            HslColor newColor = currentColor.withLuminance(luminance);
            colorProperty.set(newColor);
        });
    }

    private void redrawHueSaturationArea(int clickX, int clickY) {
        GraphicsContext c2d = hueSaturationArea.getGraphicsContext2D();

        for (int sat = 0; sat <= 240; sat++) {
            for (int hue = 0; hue <= 239; hue++) {
                // TODO: Draw 4x4 pixel boxes
                // TODO: Consider using dithering as they do in original Color Picker
                Color color = HslColor.ofHsl(hue / 4 * 4, sat / 4 * 4, 120).toColor();

                c2d.setFill(color);
                c2d.fillRect(hue, 240 - sat, 1, 1);
            }
        }


        int sniperSize = 4;
        int halfSS = sniperSize / 2;

        c2d.setFill(Color.BLACK);
        // top
        c2d.fillRect(clickX - halfSS, clickY - halfSS - 3 * sniperSize, sniperSize, sniperSize * 2);
        // bottom
        c2d.fillRect(clickX - halfSS, clickY + halfSS + sniperSize, sniperSize, sniperSize * 2);
        // left
        c2d.fillRect(clickX - 3 * sniperSize - halfSS, clickY - halfSS, 2 * sniperSize, sniperSize);
        // right
        c2d.fillRect(clickX + sniperSize + halfSS, clickY - halfSS, 2 * sniperSize, sniperSize);
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
}
