package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorDialog implements Initializable {

    public static void showModal(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ColorDialog.class.getResource("ColorDialog.fxml"));

            Stage childWindow = new Stage();
            childWindow.initOwner(owner);
            childWindow.initModality(Modality.APPLICATION_MODAL);
            // childWindow.initStyle(StageStyle.UTILITY);
            childWindow.setTitle("About...");
            childWindow.setResizable(false);
            childWindow.setScene(new Scene(loader.load()));

            childWindow.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Rectangle selectedColorPreview;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private NumberTextField hueTextField;

    @FXML
    private NumberTextField satTextField;

    @FXML
    private NumberTextField lumTextField;

    @FXML
    private NumberTextField redTextField;

    @FXML
    private NumberTextField greenTextField;

    @FXML
    private NumberTextField blueTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Dialog initialized");

        selectedColorPreview.fillProperty().bind(colorPicker.colorProperty());

        hueTextField.setMin(HslColor.MIN_HUE);
        hueTextField.setMax(HslColor.MAX_HUE);
        hueTextField.setNumericValue(colorPicker.getHslColor().hue());

        satTextField.setMin(HslColor.MIN_SATURATION);
        satTextField.setMax(HslColor.MAX_SATURATION);
        satTextField.setNumericValue(colorPicker.getHslColor().saturation());

        lumTextField.setMin(HslColor.MIN_LUMINOSITY);
        lumTextField.setMax(HslColor.MAX_LUMINOSITY);
        lumTextField.setNumericValue(colorPicker.getHslColor().luminosity());

        redTextField.setMin(0);
        redTextField.setMax(255);

        greenTextField.setMin(0);
        greenTextField.setMax(255);

        blueTextField.setMin(0);
        blueTextField.setMax(255);
    }
}
