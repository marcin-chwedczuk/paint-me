package pl.marcinchwedczuk.paintme.gui.easel;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

@DefaultProperty("canvas")
public class Easel extends Control {
    private final ObjectProperty<Region> canvasProperty =
            new SimpleObjectProperty<>(this, "canvas", null);

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EaselSkin(this);
    }

    public final ObjectProperty<Region> canvasProperty() {
        return canvasProperty;
    }

    public final void setCanvas(Region value) {
        canvasProperty().set(value);
    }

    public final Region getCanvas() {
        return canvasProperty().get();
    }
}
