package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

public class HtmlHelpViewerWrapper {
    private final WebView webView;

    public HtmlHelpViewerWrapper(WebView webView) {
        this.webView = webView;
    }

    public void initialize() {
        webView.getEngine().load(getClass().getResource("javafx-css-docs.html").toExternalForm());
        webView.setContextMenuEnabled(false);

        // Enable navigation within the document
        webView.getEngine().setOnStatusChanged((WebEvent<String> event) -> {
            // WARNING: Insecure but this is a local HTML file from resources, so should be fine
            if (event.getData() != null && event.getData().contains("javafx-css-docs.html")) {
                webView.getEngine().load(event.getData());
            }
        });
    }

    public void showHelpFor(Class<?> controlClass) {
        // TODO: Go to document table of contents if null
        if (controlClass == null) return;

        webView.getEngine().executeScript("{ var el = document.getElementById('" + controlClass.getSimpleName().toLowerCase() + "'); if (el) el.scrollIntoView(); }");
    }

    // TODO: Add go to table of contents option
}
