package org.juhanir;

import org.juhanir.ui.Gui;

/**
 * Main class to start the application.
 */
final class Main {

  /**
   * Private constructor.
   */
  private Main() {

  }

  /**
   * Main method that starts the application.
   *
   * @param args arguments to the main method
   */
  public static void main(final String[] args) {
    Gui gui = new Gui();
    gui.run();
  }

}
