package pl.marcinchwedczuk.paintme.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import pl.marcinchwedczuk.paintme.gui.dragdrop.HelloDragAndDrop;
import pl.marcinchwedczuk.paintme.gui.dynamiccss.FxCssURLStreamHandler;
import pl.marcinchwedczuk.paintme.gui.mainwindow.MainWindow;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

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
        // TODO: Blurry text on Linux - try to fix this....
        // System.setProperty("prism.lcdtext", "true");

        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
            @Override
            public URLStreamHandler createURLStreamHandler(String protocol) {
                // TODO: Working but needs polishing
                System.out.println("PROTOCOL setURLStreamHandlerFactory: " + protocol);
                if ("fxcss".equals(protocol)) {
                    return new FxCssURLStreamHandler();
                }

                System.out.println("NULL FOR: " + protocol);
                return null;
            }
        });

        launch();
    }
}