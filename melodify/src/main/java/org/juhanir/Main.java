package org.juhanir;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Application entrypoint.
 */
public class Main {

  private static Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    Launcher.main(args);
  }

}
