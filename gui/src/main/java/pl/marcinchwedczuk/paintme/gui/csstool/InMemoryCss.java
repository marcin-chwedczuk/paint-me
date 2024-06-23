package pl.marcinchwedczuk.paintme.gui.csstool;

import pl.marcinchwedczuk.paintme.gui.dynamiccss.DynamicCssProvider;

public class InMemoryCss implements DynamicCssProvider {
    public static volatile String cssText = "/* nothing */";

    @Override
    public String loadCss() {
        return cssText;
    }
}
