package org.juhanir.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.juhanir.Constants;

/**
 * Playback utils
 */
public class Playback {

  /**
   * Get the rhythm layer for Jfugue player.
   *
   * @param timeSignature time signature
   * @return rhythm layer string
   */
  public static String resolveRhythm(String staccatoString) {

    String timeSignature = "4/4";

    String pattern = "TIME:(\\S+)";

    Pattern regex = Pattern.compile(pattern);
    Matcher matcher = regex.matcher(staccatoString);

    if (matcher.find()) {
      timeSignature = matcher.group(1);
    }

    if (timeSignature.equals("4/4")) {
      return String.format("T%s V9 [CLOSED_HI_HAT]q Rq [CLOSED_HI_HAT]q Rq [CLOSED_HI_HAT]q Rq",
          Constants.PLAYBACK_TEMPO);
    }
    return String.format("T%s V9 [CLOSED_HI_HAT]i*3:2 Ri*3:2 Ri*3:2 [CLOSED_HI_HAT]i*3:2 Ri*3:2 Ri*3:2",
        Constants.PLAYBACK_TEMPO);
  }
}
