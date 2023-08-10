package org.juhanir;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main application class.
 */
public class Main extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/AppController.fxml"));
    StackPane view = (StackPane) loader.load();
    Scene scene = new Scene(view);
    // Making sure that threads close with the application
    stage.setOnCloseRequest(event -> {
      Platform.exit();
      System.exit(0);
    });
    stage.setTitle("MELODIFY");
    stage.setScene(scene);
    stage.show();
  }

  public void run() {
    launch();
  }

}
