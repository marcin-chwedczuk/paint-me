module pl.marcinchwedczuk.paintme.gui {
    requires pl.marcinchwedczuk.paintme.domain;

    requires javafx.controls;
    requires javafx.fxml;

    exports pl.marcinchwedczuk.paintme.gui;
    exports pl.marcinchwedczuk.paintme.gui.easel;
    exports pl.marcinchwedczuk.paintme.gui.mainwindow;
    exports icons;

    // Allow @FXML injection to private fields.
    opens pl.marcinchwedczuk.paintme.gui.mainwindow;
    opens pl.marcinchwedczuk.paintme.gui.easel;
    opens icons;
}