## Hours

Quite a lot of small issues trying to get the jar to contain all needed dependencies. Issues about `com.sun.xml.bind...` that did not appear when running with javafx launcher but didn't work in the jar. Needed to add a new dependency to the project for that. Also the application entrypoint must not extend the javafx.Application so needed to add a wrapper class for it. Other issues as well,

|Amount|What I did|
|-|-|
|2|Test coverage with larger inputs for generation|
|5|UI testing with TestFX|
|2|simplify displaying messages in the UI|
|2|create a working jar|
|||
