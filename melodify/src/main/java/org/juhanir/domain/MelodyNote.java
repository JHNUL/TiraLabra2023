package org.juhanir.domain;

/**
 * A representation of a note containing enough data to be played. Serializable to MusicXML pitch
 * element.
 */
public class MelodyNote {

  private String step;
  private int alter;
  private int octave;
  private static final String[] signs = new String[] { "b", "", "#" };

  /**
   * Constructor for MelodyNote.
   *
   * @param step the name of the note
   * @param alter -1 for flat, 0 for natural, 1 for sharp
   * @param octave value of the octave
   */
  public MelodyNote(String step, int alter, int octave) {
    this.step = step;
    this.alter = alter;
    this.octave = octave;
  }

  public String getStep() {
    return this.step;
  }

  public int getAlter() {
    return this.alter;
  }

  public int getOctave() {
    return this.octave;
  }

  @Override
  public String toString() {
    return String.format("%s%s", this.getStep(), signs[this.getAlter() + 1]);
  }
}