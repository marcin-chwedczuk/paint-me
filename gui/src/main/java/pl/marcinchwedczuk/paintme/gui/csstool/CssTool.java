package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
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
    private TextArea warnings;

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

        List<String> controls = List.of(
                RadioButton.class.getName(),
                Button.class.getName(),
                CheckBox.class.getName()
        );

        selectedControl.setItems(FXCollections.observableList(new ArrayList<>(controls)));

        selectedControl.getSelectionModel().selectedItemProperty().addListener((o, oldValue, newValue) -> {
            controlContainer.getChildren().clear();

            try {
                Class<?> controlClass = Class.forName(newValue);

                Control control = (Control) controlClass.newInstance();

                controlContainer.setCenter(control);
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

            } catch (Exception e) {
                StringWriter out = new StringWriter();
                PrintWriter writer = new PrintWriter(out);

                e.printStackTrace(writer);

                writer.flush();
                warnings.setText(out.toString());
            }
        });
    }

}
