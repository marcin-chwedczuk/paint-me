package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
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
    public TextArea cssText;

    @FXML
    public TextArea warnings;

    @FXML
    public ComboBox<String> selectedControl;

    @FXML
    public BorderPane controlContainer;

    @FXML
    public TextArea helpText;

    private int cacheBuster = 100000;

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

                if (control instanceof Labeled) {
                    ((Labeled) control).setText("Foobar");
                }

                // Get list of properties
                StringBuilder sb = new StringBuilder();

                List<? extends Styleable> ll = List.of(control);

                Class<?> current = controlClass;
                while (!current.getSimpleName().equals("Object")) {
                    Method m = current.getMethod("getClassCssMetaData");
                    if (m != null) {
                        List<CssMetaData<Control, ?>> stylables = (List<CssMetaData<Control, ?>>) m.invoke(null);
                        for (CssMetaData<Control, ?> entry : stylables) {
                            sb.append(entry.getProperty()).append('\n')
                                    .append('\t').append("init: ").append(entry.getInitialValue(control)).append('\n');

                            var subs = (List<CssMetaData<Control, ?>>) (Object) entry.getSubProperties();
                            if (subs != null) {
                                for (var sub : subs) {
                                    sb.append('\t').append(sub.getProperty()).append(" init: ").append(sub.getInitialValue(control)).append('\n');
                                }
                            }
                        }

                        break;
                    }

                    current = current.getSuperclass();
                }

                helpText.setText(sb.toString());

            } catch (Exception e) {
                StringWriter out = new StringWriter();
                PrintWriter writer = new PrintWriter(out);

                e.printStackTrace(writer);

                writer.flush();
                warnings.setText(out.toString());
            }
        });

        cssText.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                System.out.println("RELOADED CSS");
                InMemoryCss.cssText = cssText.getText();

                if (controlContainer.getChildren().isEmpty()) return;

                ObservableList<String> stylesheets = ((Control) controlContainer.getChildren().get(0)).getStylesheets();
                stylesheets.clear();
                stylesheets.add("fxcss://" + InMemoryCss.class.getName() + "?cacheBuster=" + cacheBuster++);
            }
        });
    }
}
