package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
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
    private Region selectedColorPreview;

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

    private ToggleGroup selectedColorToggleGroup = new ToggleGroup();

    @FXML
    private GridPane predefinedColors;

    @FXML
    private GridPane customColors;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Dialog initialized");

        initializeColorPickerBindings();

        final int COLUMNS = 8;
        final int PREDEF_ROWS = 6;
        final int CUSTOM_ROWS = 2;

        // Setup GridPanes

        NodeArray<RadioButton> radioButtons = new NodeArray<>(PREDEF_ROWS + CUSTOM_ROWS, COLUMNS);

        // Fill GridPanes
        for (int r = 0; r < PREDEF_ROWS + CUSTOM_ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                var radio = new RadioButton();
                radio.setToggleGroup(selectedColorToggleGroup);
                radioButtons.setNode(radio, r, c);

                if (r < PREDEF_ROWS) {
                    predefinedColors.add(radio, c, r);
                } else {
                    customColors.add(radio, c, r - PREDEF_ROWS);
                }
            }
        }

        for(RadioButton button : radioButtons) {
            // Be default radio that get focus will be selected, we want to split
            // this behaviour into two: focusing and selecting (using Space).
            button.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
                RadioButton radioToFocus = null;

                if (e.getCode() == KeyCode.DOWN) {
                    e.consume();
                    radioToFocus = radioButtons.getNodeBelow(button);
                } else if (e.getCode() == KeyCode.UP) {
                    e.consume();
                    radioToFocus = radioButtons.getNodeAbove(button);
                } else if (e.getCode() == KeyCode.LEFT) {
                    e.consume();
                    radioToFocus = radioButtons.getNodeOnLeft(button);
                } else if (e.getCode() == KeyCode.RIGHT) {
                    e.consume();
                    radioToFocus = radioButtons.getNodeOnRight(button);
                }

                if (radioToFocus != null) radioToFocus.requestFocus();
            });
        }
    }

    private void initializeColorPickerBindings() {
        selectedColorPreview.backgroundProperty().bind(colorPicker.colorProperty().map(Background::fill));

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
        GenericBidirectionalBinding.bindBidirectional(
                redTextField.textProperty(),
                colorPicker.colorProperty(),
                new ObjectToComponentConverter<>(
                        color -> Integer.toString((int)Math.round(color.getRed() * 255)),
                        (text, oldColor) -> Color.color(Integer.parseInt(text) / 255.0, oldColor.getGreen(), oldColor.getBlue())));

        greenTextField.setMin(0);
        greenTextField.setMax(255);
        GenericBidirectionalBinding.bindBidirectional(
                greenTextField.textProperty(),
                colorPicker.colorProperty(),
                new ObjectToComponentConverter<>(
                        color -> Integer.toString((int)Math.round(color.getGreen() * 255)),
                        (text, oldColor) -> Color.color(oldColor.getRed(), Integer.parseInt(text) / 255.0, oldColor.getBlue())));

        blueTextField.setMin(0);
        blueTextField.setMax(255);
        GenericBidirectionalBinding.bindBidirectional(
                blueTextField.textProperty(),
                colorPicker.colorProperty(),
                new ObjectToComponentConverter<>(
                        color -> Integer.toString((int)Math.round(color.getBlue() * 255)),
                        (text, oldColor) -> Color.color(oldColor.getRed(), oldColor.getGreen(), Integer.parseInt(text) / 255.0)));
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
