package org.juhanir.view;

import java.io.IOException;
import org.juhanir.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class UserInterfaceIT {

  @Start
  void setUp(Stage stage) throws IOException {
    new Main().start(stage);
  }

  @Test
  void should_contain_button_with_text(FxRobot robot) {
    // try something like
    // https://github.com/TestFX/TestFX/blob/master/subprojects/testfx-core/src/main/java/org/testfx/util/WaitForAsyncUtils.java#L297
    robot.lookup("#progressIndicator").tryQuery().isEmpty();
  }

}
