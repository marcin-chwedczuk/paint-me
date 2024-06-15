package pl.marcinchwedczuk.paintme.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.marcinchwedczuk.paintme.gui.dragdrop.HelloDragAndDrop;
import pl.marcinchwedczuk.paintme.gui.mainwindow.MainWindow;

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) {
        MainWindow.showOn(stage);
        // HelloDragAndDrop.start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}