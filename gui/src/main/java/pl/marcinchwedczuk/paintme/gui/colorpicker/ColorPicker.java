package pl.marcinchwedczuk.paintme.gui.colorpicker;


import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import pl.marcinchwedczuk.paintme.gui.extra.Converter;
import pl.marcinchwedczuk.paintme.gui.extra.GenericBidirectionalBinding;

// Docs: https://learn.microsoft.com/en-us/windows/win32/dlgbox/color-dialog-box
public class ColorPicker extends HBox {
    // HSL is the source of truth
    private final SimpleObjectProperty<HslColor> hslColorProperty = new SimpleObjectProperty<>(this, "hslColor", HslColor.ofHsl(80, 80, 80));
    private final SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(this, "color", Color.AQUA);

    private final Canvas hueSaturationArea;
    private final Canvas luminanceBar;
    private final Polygon triangleLuminancePointer;

    public ColorPicker() {
        getStyleClass().add("color-picker");

        GenericBidirectionalBinding.bindBidirectional(
                colorProperty, hslColorProperty, new Converter<Color, HslColor>() {
                    @Override
                    public Color toFirst(HslColor hslColor) {
                        return (hslColor != null) ? hslColor.toColor() : null;
                    }

                    @Override
                    public HslColor toSecond(Color color) {
                        return (color != null) ? HslColor.ofColor(color) : null;
                    }
                }
        );

        hueSaturationArea = new Canvas();
        hueSaturationArea.setWidth(239);
        hueSaturationArea.setHeight(240);

        var hueSaturationAreaPane = new Pane(hueSaturationArea);
        hueSaturationAreaPane.getStyleClass().add("hue-saturation-area");

        luminanceBar = new Canvas();
        luminanceBar.setWidth(12);
        luminanceBar.setHeight(240);
        luminanceBar.setMouseTransparent(true);

        triangleLuminancePointer = new Polygon(0, 0, 1, 1, 1, -1);
        triangleLuminancePointer.setMouseTransparent(true);
        Translate moveY = new Translate(0, 0);
        moveY.yProperty().bind(colorProperty.map(HslColor::ofColor).map(c -> 240 - c.luminosity()));
        triangleLuminancePointer.getTransforms().addAll(moveY, new Scale(8, 8));

        var luminanceBarAreaPane = new HBox(luminanceBar, triangleLuminancePointer);
        luminanceBarAreaPane.getStyleClass().add("luminance-area");

        getChildren().addAll(hueSaturationAreaPane, luminanceBarAreaPane);

        hueSaturationArea.setOnMouseClicked(e -> {
            int x = (int) e.getX();
            int y = (int) e.getY();

            int hue = Math.clamp(x, HslColor.MIN_HUE, HslColor.MAX_HUE);
            int saturation = Math.clamp(240 - y, HslColor.MIN_SATURATION, HslColor.MAX_SATURATION);

            setHslColor(getHslColor().withHue(hue).withSaturation(saturation));
        });

        luminanceBarAreaPane.setOnMouseClicked(e -> {
            int y = (int) e.getY();

            int luminance = Math.clamp(240 - y, HslColor.MIN_LUMINOSITY, HslColor.MAX_LUMINOSITY);

            setHslColor(getHslColor().withLuminosity(luminance));
        });

        hslColorProperty().addListener((e) -> {
            redrawPickerAreas();
        });

        // Initial draw
        redrawPickerAreas();
    }

    private void redrawPickerAreas() {
        int x = getHslColor().hue();
        int y = HslColor.MAX_SATURATION - getHslColor().saturation();

        redrawHueSaturationArea(x, y);
        redrawLuminanceBar();
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

        int step = 8;
        for (int lum = 0; lum <= 240; lum += step) {
            c2d.setFill(getHslColor().withLuminosity(240 - lum).toColor());
            c2d.fillRect(0, lum, w, step);
        }
    }

    public ObjectProperty<Color> colorProperty() {
        return this.colorProperty;
    }

    public Color getColor() {
        return colorProperty().get();
    }

    public void setColor(Color c) {
        colorProperty().set(c);
    }

    public ObjectProperty<HslColor> hslColorProperty() {
        return this.hslColorProperty;
    }

    public HslColor getHslColor() {
        return hslColorProperty().get();
    }

    public void setHslColor(HslColor c) {
        hslColorProperty().set(c);
    }
}
