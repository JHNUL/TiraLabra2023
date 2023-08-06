package org.juhanir;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main application class.
 */
public class Main extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader loader = new FXMLLoader(Main.class.getResource("/AppController.fxml"));
    AnchorPane view = (AnchorPane) loader.load();
    Scene scene = new Scene(view);
    stage.setTitle("MELODIFY");
    stage.setScene(scene);
    stage.show();
  }

  public void run() {
    launch();
  }

}
