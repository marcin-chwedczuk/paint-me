package pl.marcinchwedczuk.paintme.gui.colorpicker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ColorPicker implements Initializable {
    public static void showModal(Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ColorPicker.class.getResource("ColorPicker.fxml"));

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
    private Canvas colors;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Dialog initialized");

        GraphicsContext c2d = colors.getGraphicsContext2D();

        for (int sat = 0; sat <= 240; sat++) {
            for (int hue = 0; hue <= 239; hue++) {
                // TODO: Draw 4x4 pixel boxes
                // TODO: Consider using dithering as they do in original Color Picker
                Color color = HSL.fromHSL(hue / 4 * 4, 120, sat / 4 * 4).toRgb();

                c2d.setFill(color);
                c2d.fillRect(hue, 240 - sat, 1, 1);
            }
        }
    }
}
