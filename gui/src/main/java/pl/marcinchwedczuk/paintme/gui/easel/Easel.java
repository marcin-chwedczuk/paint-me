package pl.marcinchwedczuk.paintme.gui.easel;

import javafx.beans.DefaultProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;

@DefaultProperty("canvas")
public class Easel extends Control {
    private final ObjectProperty<Region> canvasProperty =
            new SimpleObjectProperty<>(this, "canvas", null);

    private final DoubleProperty zoomProperty =
            new SimpleDoubleProperty(this, "zoom", 1.0);

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EaselSkin(this);
    }

    protected void fireZoomedEvent() {
        fireEvent(new EaselZoomedEvent());
    }

    public void addCustomEventHandler(EventHandler<EaselZoomedEvent> handler) {
        addEventHandler(EaselZoomedEvent.CUSTOM_EVENT_TYPE, handler);
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

    public DoubleProperty zoomProperty() {
        return zoomProperty;
    }

    public double getZoom() {
        return zoomProperty().get();
    }

    public void setZoom(double zoom) {
        zoomProperty().set(zoom);
    }
}
