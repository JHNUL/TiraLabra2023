package org.juhanir;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Application launcher class, needed for jar.
 * https://openjfx.io/openjfx-docs/ (Runtime images > Non-modular application)
 */
public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/App.fxml"));
        StackPane view = (StackPane) loader.load();
        Scene scene = new Scene(view);
        // Making sure threads close with the UI
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        stage.setTitle("MELODIFY");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
