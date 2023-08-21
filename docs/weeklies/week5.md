## Week5

Added more tests with several real source files for more confidence on generation correctness. Also added a UI test framework TestFX to run a simple walkthrough of the user interface, so there is some test automation for the UI layer as well. The "view" classes are not unit tested, as their unit testing would mean bootstrapping a JavaFX.Application context for any of the elements to work. It felt more meaningful to system-test the whole thing via the UI.

I also added a better info message functionality to the UI, included a logger service and removed all System.out.printlns. There is also now a selection for time signature. This affects whether the generated melody is in 1/4 or 1/8 notes and also the rhythm for the playback from the app. A MIDI file from the generation is also produced.

Quite a lot of small issues trying to get the jar to contain all needed dependencies. Issues about `com.sun.xml.bind...` that did not appear when running with javafx launcher but did appear when executing the jar. Needed to add a new dependency to the project for that. Also the application entrypoint must not extend the javafx.Application so needed to add a wrapper class for it. Other issues as well, it is somewhat difficult to get the external data folder to work both with a local build with maven using the javafx launcher and with the built Java Archive. Lack of experience I guess. Due to how the path to `data` is resolved, the jar works if it is in a folder structure like this and you run `java -jar name_of_jar.jar` _inside_ the jarfolder.
```
.
├── jarfolder (jar inside here)
└── data
      ├── musicxml
      └── output
```
In a more user-oriented app the data source folder could be selected by opening a filesystem browser, but I'm not going to do that here.

TODO:
- add some more songs for the final data package
- go throught the code and check the suggestions from the code review
- if time, add some flare to the UI

## Hours

|Amount|What I did|
|-|-|
|2|Test coverage with larger inputs for generation|
|5|UI testing with TestFX|
|2|simplify displaying messages in the UI|
|2|create a working jar|
|3|time signature to generation and playback|
|2|update and finalize docs for week 5 submission|
|16||
