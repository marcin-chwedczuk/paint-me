module pl.marcinchwedczuk.paintme.gui {
    requires pl.marcinchwedczuk.paintme.domain;

    requires javafx.controls;
    requires javafx.fxml;

    exports pl.marcinchwedczuk.paintme.gui;
    exports pl.marcinchwedczuk.paintme.gui.mainwindow;

    // Allow @FXML injection to private fields.
    opens pl.marcinchwedczuk.paintme.gui.mainwindow;
}