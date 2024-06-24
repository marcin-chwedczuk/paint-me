import java.net.spi.URLStreamHandlerProvider;

module pl.marcinchwedczuk.paintme.gui {
    requires pl.marcinchwedczuk.paintme.domain;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires org.reflections;
    requires org.jsoup;

    exports pl.marcinchwedczuk.paintme.gui;
    exports pl.marcinchwedczuk.paintme.gui.easel;
    exports pl.marcinchwedczuk.paintme.gui.mainwindow;
    exports pl.marcinchwedczuk.paintme.gui.colorpicker;
    exports pl.marcinchwedczuk.paintme.gui.extra;
    exports pl.marcinchwedczuk.paintme.gui.dynamiccss;
    exports pl.marcinchwedczuk.paintme.gui.csstool;
    exports icons;

    // Allow @FXML injection to private fields.
    opens pl.marcinchwedczuk.paintme.gui.mainwindow;
    opens pl.marcinchwedczuk.paintme.gui.colorpicker;
    opens pl.marcinchwedczuk.paintme.gui.easel;
    opens pl.marcinchwedczuk.paintme.gui.extra;
    opens pl.marcinchwedczuk.paintme.gui.dynamiccss;
    opens pl.marcinchwedczuk.paintme.gui.csstool;
    opens icons;

    provides java.net.spi.URLStreamHandlerProvider with pl.marcinchwedczuk.paintme.gui.ClasspathURLStreamHandlerProvider;
}