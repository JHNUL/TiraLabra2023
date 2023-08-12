## Hours

Big refactor to use FXML files to describe UI and controller classes to handle functionality. Added checkstyle local config for more control.

I was able to make some sensible generations from tight selection of training data. This week I used a set of Irish jigs in one key only. Also I hardcoded a rhythm for the playback to mimic the genre and style of the training data. For some reason the key of D made far better sounding generations than others.

The UI now works sufficiently. The user should be able to use it even without instructions but there are nonetheless instructions for use in the readme of the repo. Playback is nice, but when it is started it kind of "swallows" something from the beginning and its not entirely in sync for the first couple of beats before it evens out. Maybe due to starting a background task for the playback.

With the FXML refactoring I created the appcontroller and eventhandler classes, of which eventhandlers could be tested but are not currently. Next I will introduce some tests for these. It is most important to test the actual business logic from services and domain, where the coverage is already not terrible, but I need to add more tests on generations using larger inputs of training data. With the current selection of training data the outputs are not horrible, but I'm not convinced that there are no bugs anywhere, because with other training data sets I have gotten weird results with clear chromatic sequences which of course can and do exist in music but I think I got a little too much of them.

Notable learnings:
- JavaFX tasks and Java multithreading

Focus:
- Testing to see if there is weirdness in the generations (unit tests are quite extensive, but work mostly on small inputs)
- Select one genre and style of training data to make generations sane. This already happens with the Irish jig theme, but investigate others

|Amount|What I did|
|-|-|
|4|UI refactoring to FXML|
|2|Generate XML and save to file|
|5|Play generated file(s), use concurrency for UI-blocking jobs, ability to stop playback|
|1|Get better training data, convert large set of Irish folk tunes from abc to xml|
|2|Make the generation initial prefix follow the key|
|5|Write documentation, small fixes here and there|
|19||
