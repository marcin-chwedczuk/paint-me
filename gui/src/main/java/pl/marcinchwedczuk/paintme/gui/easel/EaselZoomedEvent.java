package pl.marcinchwedczuk.paintme.gui.easel;

import javafx.event.Event;
import javafx.event.EventType;

public class EaselZoomedEvent extends Event {
    public static final EventType<EaselZoomedEvent> CUSTOM_EVENT_TYPE = new EventType<>(Event.ANY, "EASEL_ZOOMED_EVENT");

    public EaselZoomedEvent() {
        super(CUSTOM_EVENT_TYPE);
    }
}
