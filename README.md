[![unit-tests](https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml/badge.svg)](https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml)

# Melodify

Assignment for University of Helsinki CS Data Structures and Algorithms lab.

The application will read MusicXML files as training data to populate a stochastic model that is used to predict the next note given a current note or a sequence of preceding notes. The model is using a Markov Chain of user-defined degree for the prediction.

## Documentation

- [Specification (määrittelydokumentti)](/docs/specifications.md)
- [Testing (testausdokumentti)](/docs/testing.md)
- [Implementation (toteutusdokumentti)](/docs/implementation.md)

#### Weeklies

- [Week 1](/docs/weeklies/week1.md)
- [Week 2](/docs/weeklies/week2.md)
- [Week 3](/docs/weeklies/week3.md)
- [Week 4](/docs/weeklies/week4.md)
- [Week 5](/docs/weeklies/week5.md)

Select the commit with tag "weekN" to review the repository in the intended state for the week N submission.


## Instructions how to run

Prerequisites:
- JDK >= 17
- maven >= 3.8.7

Tested on the following operating systems: Debian 12, Windows 10 (using Git Bash)

Run the following commands in the repository root to start the application:
```sh
# Unzip training data (must run before starting the application)
./scripts/prepare_data.sh

# Install dependencies
mvn install -f melodify/pom.xml

# Start application
mvn clean javafx:run -f melodify/pom.xml
```

Build site:
```sh
# e.g. checkstyle report found in melodify/target/site
mvn clean site -f melodify/pom.xml
```

Build a jar:
```sh
mvn clean compile package -f melodify/pom.xml
```

The jar will expect the following folder structure for source data:
```sh
# data folder must be a sibling to the jar
.
melodify.jar
data/
  ├── musicxml/
  └── output/
```


### Usage

After starting the application the training data is loaded and user can select a key and the degree of the Markov's Chain (min 1 max 6) to use for training the model. Current training data package contains a set of Irish jigs in D and this should come across the generated melodies at least a little bit.

![training](/docs/images/howto_train.png)

After setting the parameters and training the model the generate button becomes enabled. Time signature selection is a small curiosity here for playback as it adds a rhythm and controls whether the generation is quarter or eighth notes. Generated melodies will be written to file for playback in the app and also saved in MIDI format.

![generating](/docs/images/howto_generate.png)

Generation should be more or less instantaneous and the file should become visible in the dropdown. Once a file is selected playback can be started.

![playback](/docs/images/howto_playback.png)

Playback happens on another thread so as to not block the UI and therefore also prevent pressing the stop button. Other controls are disabled during playback. Full generated melody is 120 notes long as an arbitrary hardcoded limit for now. It's possible that the generation is shorter if it found a sequence with no following notes.

NOTE: when starting the playback it is often not perfectly in sync right in the beginning but evens out quite quick to a stable rhythm (something to fix later perhaps).

![stop](/docs/images/howto_stop.png)
