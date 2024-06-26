package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.scene.paint.Color;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * HSL <-> RGB color translation.
 * Based on Wine: https://source.winehq.org/source/dlls/shlwapi/ordinal.c
 */
public class HslColor {
    // In the Color dialog box, the saturation and luminosity values must be in the range 0 through 240, and the hue value must be in the range 0 through 239.
    // https://learn.microsoft.com/pl-pl/windows/win32/dlgbox/color-dialog-box?redirectedfrom=MSDN

    public static final int MIN_HUE = 0;
    public static final int MAX_HUE = 239;

    public static final int MIN_SATURATION = 0;
    public static final int MAX_SATURATION = 240;

    public static final int MIN_LUMINOSITY = 0;
    public static final int MAX_LUMINOSITY = 240;

    public static HslColor ofHsl(int hue, int saturation, int luminosity) {
        return new HslColor(hue, luminosity, saturation);
    }

    public static HslColor ofColor(Color color) {
        return ofRgb(
                toIntComponent(color.getRed()),
                toIntComponent(color.getGreen()),
                toIntComponent(color.getBlue()));
    }

    private static int toIntComponent(double component) {
        return Math.clamp((int)Math.round(component * 255.0), 0, 255);
    }

    public static HslColor ofRgb(int wR, int wG, int wB) {
        int wMax, wMin;
        int wHue, wLuminosity, wSaturation;

        wMax = max(wR, max(wG, wB));
        wMin = min(wR, min(wG, wB));

        wLuminosity = ((wMax + wMin) * 240 + 255) / 510;

        if (wMax == wMin) {
            /* Achromatic case */
            wSaturation = 0;
            /* Hue is now unrepresentable, but this is what native returns... */
            wHue = 160;
        } else {
            /* Chromatic case */
            int wDelta = wMax - wMin, wRNorm, wGNorm, wBNorm;

            /* Saturation */
            if (wLuminosity <= 120) {
                wSaturation = ((wMax + wMin)/2 + wDelta * 240) / (wMax + wMin);
            } else {
                wSaturation = ((510 - wMax - wMin)/2 + wDelta * 240) / (510 - wMax - wMin);
            }

            /* Hue */
            wRNorm = (wDelta/2 + wMax * 40 - wR * 40) / wDelta;
            wGNorm = (wDelta/2 + wMax * 40 - wG * 40) / wDelta;
            wBNorm = (wDelta/2 + wMax * 40 - wB * 40) / wDelta;

            if (wR == wMax)
                wHue = wBNorm - wGNorm;
            else if  (wG == wMax)
                wHue = 80 + wRNorm - wBNorm;
            else
                wHue = 160 + wGNorm - wRNorm;

            if (wHue < 0)
                wHue += 240;
            else if (wHue > 240)
                wHue -= 240;
        }

        return new HslColor(wHue, wLuminosity, wSaturation);
    }

    private final int hue;
    private final int luminosity;
    private final int saturation;

    public HslColor(int hue, int luminosity, int saturation) {
        if (hue < MIN_HUE || hue > MAX_HUE)
            throw new IllegalArgumentException("hue");

        if (luminosity < MIN_LUMINOSITY || luminosity > MAX_LUMINOSITY)
            throw new IllegalArgumentException("luminosity");

        if (saturation < MIN_SATURATION || saturation > MAX_SATURATION)
            throw new IllegalArgumentException("saturation");

        this.hue = hue;
        this.luminosity = luminosity;
        this.saturation = saturation;
    }

    public int hue() {
        return hue;
    }

    public HslColor withHue(int newHue) {
        return HslColor.ofHsl(newHue, saturation, luminosity);
    }

    public int luminosity() {
        return luminosity;
    }

    public HslColor withLuminosity(int newLuminosity) {
        return HslColor.ofHsl(hue, saturation, newLuminosity);
    }

    public int saturation() {
        return saturation;
    }

    public HslColor withSaturation(int newSaturation) {
        return HslColor.ofHsl(hue, newSaturation, luminosity);
    }

    private static int convertHue(int hue, int mid1, int mid2) {
        hue = hue > 240 ? hue - 240 : hue < 0 ? hue + 240 : hue;

        if (hue > 160)
            return mid1;
        else if (hue > 120)
            hue = 160 - hue;
        else if (hue > 40)
            return mid2;

        return ((hue * (mid2 - mid1) + 20) / 40) + mid1;
    }

    private static int getRgb(int h, int mid1, int mid2) {
        return (convertHue(h, mid1, mid2) * 255 + 120) / 240;
    }

    public Color toColor() {
        int red, green, blue, mid1, mid2;

        if (saturation != 0) {
            if (luminosity > 120)
                mid2 = saturation + luminosity - (saturation * luminosity + 120) / 240;
            else
                mid2 = ((saturation + 240) * luminosity + 120) / 240;

            mid1 = luminosity * 2 - mid2;

            red   = getRgb(hue + 80, mid1, mid2);
            green = getRgb(hue, mid1, mid2);
            blue  = getRgb(hue - 80, mid1, mid2);

            return Color.rgb(red, green, blue);
        }

        red = luminosity * 255 / 240;
        return Color.rgb(red, red, red);
    }
}
