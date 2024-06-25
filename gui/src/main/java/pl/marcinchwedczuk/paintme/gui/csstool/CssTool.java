package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CssTool implements Initializable {
    public static CssTool showOn(Stage window) {
        try {
            FXMLLoader loader = new FXMLLoader(CssTool.class.getResource("CssTool.fxml"));

            Scene scene = new Scene(loader.load());
            CssTool controller = (CssTool) loader.getController();

            window.setTitle("CSS Tool");
            window.setScene(scene);
            window.setResizable(true);

            window.show();

            return controller;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private AutocompleteTextArea cssText;

    @FXML
    private WebView oracleHelpViewer;

    @FXML
    private ComboBox<String> selectedControl;

    @FXML
    private BorderPane controlContainer;

    @FXML
    private ListView<String> cssProperties;

    @FXML
    private ControlStructureTreeView controlStructure;

    // TODO: Split into several classes

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("INITIALIZING CSS TOOL");

        try {
            oracleHelpViewer.getEngine().load(getClass().getResource("javafx-css-docs.html").toExternalForm());
            oracleHelpViewer.setContextMenuEnabled(false);

            oracleHelpViewer.getEngine().setOnStatusChanged((WebEvent<String> event) -> {
                if (event.getData() != null && event.getData().contains("javafx-css-docs.html")) {
                    System.out.println("LOADING: " + event.getData());
                    oracleHelpViewer.getEngine().load(event.getData());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> controlClassNames = JavaFxControlClassesFinder.findControlClasses().stream()
                .map(Class::getName)
                .toList();
        selectedControl.setItems(FXCollections.observableArrayList(controlClassNames));

        cssText.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                e.consume();

                InMemoryCssStylesheet.setContents(cssText.getText());
                Node node = controlContainer.getCenter();
                if (node != null) {
                    ((Control)node).getStylesheets().clear();
                    ((Control)node).getStylesheets().add(InMemoryCssStylesheet.getStylesheetUrl());
                }
            }
        });

        selectedControl.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            controlContainer.getChildren().clear();

            try {
                Class<?> controlClass = Class.forName(newValue);

                Control control = (Control) controlClass.newInstance();

                controlContainer.setCenter(control);

                // TODO: Connect to zoom
                control.getTransforms().add(new Scale(3, 3));

                BorderPane.setAlignment(control, Pos.CENTER);
                controlStructure.setObservedControl(control);

                if (control instanceof Labeled) {
                    ((Labeled) control).setText("Foobar");
                }

                List<CssProperty> cssProps = JavaFxCssUtil.discoverCssProperties(controlClass);

                var cssPropsWithValues = cssProps.stream()
                        .map(prop -> String.format("%s: %s", prop.name(), prop.defaultValue()))
                        .toList();
                cssProperties.setItems(FXCollections.observableArrayList(cssPropsWithValues));

                var cssPropsNames = cssProps.stream()
                                .map(CssProperty::name)
                                .toList();
                cssText.setSuggestions(cssPropsNames);

                oracleHelpViewer.getEngine().executeScript("{ var el = document.getElementById('" + controlClass.getSimpleName().toLowerCase() + "'); if (el) el.scrollIntoView(); }");

            } catch (Exception e) {
                StringWriter out = new StringWriter();
                PrintWriter writer = new PrintWriter(out);

                e.printStackTrace(writer);

                writer.flush();

                new Alert(Alert.AlertType.WARNING, out.toString(), ButtonType.OK).showAndWait();
            }
        });
    }

}
