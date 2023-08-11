## Hours

Use FXML files to describe UI and controller classes to handle functionality. Add checkstyle local config for more control.

Things to consider: modes other than major/minor are in the training data but "treated as" major, meaning the initial sequence for the generation starts from the base note of the major scale. This might not make much difference as the notes in the scale are the same between different modes of the same key, but the melodic behaviour is different.

|Amount|What I did|
|-|-|
|4|UI refactoring to FXML|
|2|Generate XML and save to file|
|5|Play generated file(s), use concurrency for UI-blocking jobs, ability to stop playback|
|1|Get better training data, convert large set of Irish folk tunes from abc to xml|
|2|Make the generation initial prefix follow the key|
|||