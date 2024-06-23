package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.scene.paint.Color;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * HSL <-> RGB color translation.
 * Based on Wine: https://source.winehq.org/source/dlls/shlwapi/ordinal.c
 */
public class HslColor {
    public static int MIN_VALUE = 0;
    public static int MAX_VALUE = 240;

    public static HslColor ofHsl(int hue, int saturation, int luminance) {
        return new HslColor(hue, luminance, saturation);
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
    private final int luminance;
    private final int saturation;

    public HslColor(int hue, int luminance, int saturation) {
        // TODO Validate ranges
        // In the Color dialog box, the saturation and luminosity values must be in the range 0 through 240, and the hue value must be in the range 0 through 239.
        // https://learn.microsoft.com/pl-pl/windows/win32/dlgbox/color-dialog-box?redirectedfrom=MSDN
        this.hue = hue;
        this.luminance = luminance;
        this.saturation = saturation;
    }

    public int hue() {
        return hue;
    }

    public HslColor withHue(int newHue) {
        return HslColor.ofHsl(newHue, saturation, luminance);
    }

    public int luminance() {
        return luminance;
    }

    public HslColor withLuminance(int newLuminance) {
        return HslColor.ofHsl(hue, saturation, newLuminance);
    }

    public int saturation() {
        return saturation;
    }

    public HslColor withSaturation(int newSaturation) {
        return HslColor.ofHsl(hue, newSaturation, luminance);
    }

    private static int convertHue(int wHue, int wMid1, int wMid2) {
        wHue = wHue > 240 ? wHue - 240 : wHue < 0 ? wHue + 240 : wHue;

        if (wHue > 160)
            return wMid1;
        else if (wHue > 120)
            wHue = 160 - wHue;
        else if (wHue > 40)
            return wMid2;

        return ((wHue * (wMid2 - wMid1) + 20) / 40) + wMid1;
    }

    private static int getRgb(int h, int mid1, int mid2) {
        return (convertHue(h, mid1, mid2) * 255 + 120) / 240;
    }

    public Color toColor() {
        int wRed;

        if (saturation != 0) {
            int wGreen, wBlue, wMid1, wMid2;

            if (luminance > 120)
                wMid2 = saturation + luminance - (saturation * luminance + 120) / 240;
            else
                wMid2 = ((saturation + 240) * luminance + 120) / 240;

            wMid1 = luminance * 2 - wMid2;

            wRed   = getRgb(hue + 80, wMid1, wMid2);
            wGreen = getRgb(hue, wMid1, wMid2);
            wBlue  = getRgb(hue - 80, wMid1, wMid2);

            return Color.rgb(wRed, wGreen, wBlue);
        }

        wRed = luminance * 255 / 240;
        return Color.rgb(wRed, wRed, wRed);
    }
}
