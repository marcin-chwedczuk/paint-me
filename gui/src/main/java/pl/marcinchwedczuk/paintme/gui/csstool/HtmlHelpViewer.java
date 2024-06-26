package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HtmlHelpViewer extends VBox implements Initializable {
    @FXML
    private WebView webView;

    public HtmlHelpViewer() {
        // TODO: Remove duplication across project
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "HtmlHelpViewer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        // Hack for scene builder "xxx.fxml file not found" error.
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
}
