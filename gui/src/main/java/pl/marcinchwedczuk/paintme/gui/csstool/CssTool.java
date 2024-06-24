package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderWidths;
import javafx.stage.Stage;
import org.reflections.serializers.JsonSerializer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
    private TextArea cssText;

    @FXML
    private TextArea warnings;

    @FXML
    private ComboBox<String> selectedControl;

    @FXML
    private BorderPane controlContainer;

    @FXML
    private ListView<String> cssProperties;

    @FXML
    private TreeView<String> controlStructure;

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
                fillControlStructure(control);

                if (control instanceof Labeled) {
                    ((Labeled) control).setText("Foobar");
                }

                // Get list of properties
                StringBuilder sb = new StringBuilder();
                List<String> items = new ArrayList<>();

                List<? extends Styleable> ll = List.of(control);

                Class<?> current = controlClass;
                while (!current.getSimpleName().equals("Object")) {
                    Method m = current.getMethod("getClassCssMetaData");
                    if (m != null) {
                        List<CssMetaData<Control, ?>> stylables = (List<CssMetaData<Control, ?>>) m.invoke(null);
                        for (CssMetaData<Control, ?> entry : stylables) {
                            sb.append(entry.getProperty()).append(": ")
                                    .append(formatValue(control, entry)).append('\n');

                            var subs = (List<CssMetaData<Control, ?>>) (Object) entry.getSubProperties();
                            if (subs != null) {
                                for (var sub : subs) {
                                    sb.append('\t').append(sub.getProperty()).append(": ")
                                            .append(formatValue(control, sub)).append('\n');
                                }
                            }

                            items.add(sb.toString()); sb.delete(0, sb.length());
                        }

                        break;
                    }

                    current = current.getSuperclass();
                }

                Collections.sort(items);
                cssProperties.setItems(FXCollections.observableList(items));

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
                InMemoryCssStylesheet.setContents(cssText.getText());

                if (controlContainer.getChildren().isEmpty()) return;

                ObservableList<String> stylesheets = ((Control) controlContainer.getChildren().get(0)).getStylesheets();
                stylesheets.clear();
                stylesheets.add(InMemoryCssStylesheet.getStylesheetUrl());
            }
        });
    }

    private void fillControlStructure(Control control) {
        controlStructure.setRoot(null);

        control.skinProperty().addListener(o -> {
            Platform.runLater(() -> {
                TreeItem<String> root = new TreeItem<>();
                fillControlStructure(control, root, false);

                controlStructure.setRoot(root);
            });
        });
    }

    private void fillControlStructure(Node current, TreeItem<String> currentItem, boolean skinNode) {
        String cssClasses = String.join("", current.getStyleClass());
        String javaClass = current.getClass().getSimpleName();
        currentItem.setValue(String.format("%s%s(%s)", skinNode ? "#" : "", javaClass, cssClasses));
        System.out.println("ADDING " + currentItem.getValue() + " with skinNode = " + skinNode);

        if (!skinNode) {
            // addSkinNodes(current, currentItem);
        }

        if (current instanceof Parent p) {
            for (Node child : p.getChildrenUnmodifiable()) {
                TreeItem<String> childItem = new TreeItem<>();
                fillControlStructure(child, childItem, skinNode);
                currentItem.getChildren().add(childItem);
            }
        }
    }

    private void addSkinNodes(Node current, TreeItem<String> currentItem) {
        if (current instanceof Skinnable s) {
            Skin<?> skin = s.getSkin();
            if (skin == null) return;

            if (skin instanceof SkinBase<?> skinBase) {
                for (Node child : skinBase.getChildren()) {
                    TreeItem<String> childItem = new TreeItem<>();
                    fillControlStructure(child, childItem, true);
                    currentItem.getChildren().add(childItem);
                }
            }
        }
    }


    private static String formatValue(Control stylable, CssMetaData<Control, ?> cssMeta) {
        StyleableProperty<?> prop = cssMeta.getStyleableProperty(stylable);
        if (prop == null) {
            if (cssMeta.isInherits()) return "inherits";
            return "#" + formatValue(cssMeta.getInitialValue(stylable));
        }

        return formatValue(prop.getValue());
    }

    private static String formatValue(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Object[] arr) {
            return Arrays.stream(arr)
                    .map(CssTool::formatValue)
                    .collect(Collectors.joining(",", "[", "]"));
        }

        // Missing toString's from accessible classes
        if (obj instanceof BorderWidths bw) {
            // TODO
            return String.format("BorderWidths %f %f %f %f",
                    bw.getTop(), bw.getRight(), bw.getBottom(), bw.getLeft());
        }

        if (obj instanceof BackgroundSize bs) {
            // TODO
            return String.format("BackgroundSize %f %f", bs.getWidth(), bs.getHeight());
        }

        if (obj instanceof BackgroundPosition bp) {
            // TODO
            return String.format("BackgroundPosition %f %f", bp.getHorizontalPosition(), bp.getVerticalPosition());
        }

        // Missing toString's from inaccessible classes
        String str = obj.toString();

        if (str != null && str.startsWith("[com.sun")) {
            // Serialize to JSON as a last resort
            // TODO: Will require playing with add-opens to get reflections access meh :(
        }

        return str;
    }
}
