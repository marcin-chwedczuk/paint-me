package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import pl.marcinchwedczuk.paintme.gui.extra.Converter;
import pl.marcinchwedczuk.paintme.gui.extra.GenericBidirectionalBinding;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        GenericBidirectionalBinding.bindBidirectional(
                hueTextField.textProperty(),
                colorPicker.hslColorProperty(),
                new ObjectToComponentConverter<>(
                        color -> Integer.toString(color.hue()),
                        (text, oldColor) -> oldColor.withHue(Integer.parseInt(text))));


        satTextField.setMin(HslColor.MIN_SATURATION);
        satTextField.setMax(HslColor.MAX_SATURATION);
        GenericBidirectionalBinding.bindBidirectional(
                satTextField.textProperty(),
                colorPicker.hslColorProperty(),
                new ObjectToComponentConverter<>(
                        color -> Integer.toString(color.saturation()),
                        (text, oldColor) -> oldColor.withSaturation(Integer.parseInt(text))));

        lumTextField.setMin(HslColor.MIN_LUMINOSITY);
        lumTextField.setMax(HslColor.MAX_LUMINOSITY);
        GenericBidirectionalBinding.bindBidirectional(
                lumTextField.textProperty(),
                colorPicker.hslColorProperty(),
                new ObjectToComponentConverter<>(
                        color -> Integer.toString(color.luminosity()),
                        (text, oldColor) -> oldColor.withLuminosity(Integer.parseInt(text))));

        redTextField.setMin(0);
        redTextField.setMax(255);

        greenTextField.setMin(0);
        greenTextField.setMax(255);

        blueTextField.setMin(0);
        blueTextField.setMax(255);
    }

    private static class ObjectToComponentConverter<TProp, UObj> implements Converter<TProp, UObj> {
        private final Function<UObj, TProp> getter;
        private final BiFunction<TProp, UObj, UObj> setter;

        private UObj currentObj;

        public ObjectToComponentConverter(Function<UObj, TProp> getter,
                                          BiFunction<TProp, UObj, UObj> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public TProp toFirst(UObj obj) {
            var updated = getter.apply(obj);
            this.currentObj = obj;
            return updated;
        }

        @Override
        public UObj toSecond(TProp prop) {
            var updated = setter.apply(prop, currentObj);
            this.currentObj = updated;
            return updated;
        }
    }
}
