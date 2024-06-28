package pl.marcinchwedczuk.paintme.gui.csstool;

import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import pl.marcinchwedczuk.paintme.gui.colorpicker.ColorDialog;
import pl.marcinchwedczuk.paintme.gui.util.ExceptionUtil;

import java.io.IOException;
import java.util.List;

// FakeControl component used to load arbitrary FXML
// and work on the stylesheet
@DefaultProperty("children")
public class FakeControl extends Control {
    @Override
    protected Skin<?> createDefaultSkin() {
        return new SkinBase<>(this) {
            {
                FXMLLoader loader = new FXMLLoader(
                        ColorDialog.class.getResource("ColorDialog.fxml"));

                try {
                    loader.load();

                    Parent root = loader.getRoot();
                    getChildren().add(root);

                    FakeControl.this.getStylesheets().addListener((InvalidationListener) (o) -> {
                        List<String> stylesheets = FakeControl.this.getStylesheets().stream().toList();
                        root.getStylesheets().clear();
                        root.getStylesheets().addAll(stylesheets);
                    });
                } catch (IOException exception) {
                    getChildren().add(new ScrollPane(new Text(ExceptionUtil.toReadableString(exception))));
                }
            }
        };
    }
}
