package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;

public class NumberTextField extends TextField {
    private final IntegerProperty min = new SimpleIntegerProperty(this, "min", 0);
    private final IntegerProperty max = new SimpleIntegerProperty(this, "max", 100);

    // TODO: Wrap into vetoable decorator to prevent setting value outside of min max range
    private final IntegerProperty numericValue = new SimpleIntegerProperty(this, "numericValue", getMin());

    public NumberTextField() {
        setText(Integer.toString(getMin()));

        setTextFormatter(new TextFormatter<>(change -> {
            if (!change.isContentChange()) return change;

            String proposedText = change.getControlNewText();

            // Those cause problems with parseInt call.
            if (proposedText.contains("-") || proposedText.contains("+")) {
                // block change
                return null;
            }

            if (proposedText.isBlank()) {
                change.setText(Integer.toString(getMin()));
                return change;
            }

            try {
                int i = Integer.parseInt(proposedText);
                if (i < getMin() || i > getMax()) {
                    return null;
                }
            } catch (NumberFormatException e) {
                return null;
            }

            // If control contains zero, replace it with first digit
            if ("0".equals(change.getControlText())) {
                change.setRange(0, getLength());
            }

            return change;
        }));

        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(textProperty(), numericValue, converter);
    }

    public IntegerProperty min() { return min; }
    public IntegerProperty max() { return max; }
    public IntegerProperty numericValue() { return numericValue; }

    public void setMin(int min) {
        min().set(min);
    }
    public int getMin() { return min().get(); }

    public void setMax(int max) {
        max().set(max);
    }
    public int getMax() { return max().get(); }

    public int getNumericValue() {
        return numericValue().get();
    }
    public void setNumericValue(int value) {
        numericValue().set(value);
    }
}
