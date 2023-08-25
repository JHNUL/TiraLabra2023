[![unit-tests](https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml/badge.svg)](https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml)

# Melodify

Assignment for University of Helsinki CS Data Structures and Algorithms lab.

The application will read MusicXML files as training data to populate a stochastic model that is used to predict the next note. The model is using a Markov Chain of user-defined degree for the prediction.

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
- [Week 6](/docs/weeklies/week6.md)

Select the commit with tag "weekN" to review the repository in the intended state for the week N submission.


## Instructions how to run

Prerequisites:
- JDK >= 17
- maven >= 3.8.7

Tested on the following operating systems: Debian 12, Windows 10 (using Git Bash)

Run the following commands in the repository root to start the application (or use targets in makefile):
```sh
# NOTE! must unzip training data before starting the application
# Args to script are classical or irish (genre of music in dataset)
# ./scripts/prepare_data.sh classical
./scripts/prepare_data.sh irish

# Install dependencies
mvn clean install -f melodify/pom.xml

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

The jar will expect the following folder structure to exist for data, the application will not try to create folders:
```sh
# data folder must be a sibling to the jar
.
melodify.jar
data/
  ├── musicxml/ # the training data here
  └── output/ # generations will appear here
```


### Usage

After starting the application training data is loaded and user can select a key and the degree of the Markov Chain to use for training the model. Lower degrees (two to six-ish) allow for more adventuring and tend to produce more interesting melodies. In practice, the higher the degree the closer it should be to training data and more likely it will find a sequence that has no following notes (learned from an ending of a song) if the generation is long enough.

![training](/docs/images/howto_train.png)

After setting the parameters and training the model the generate button becomes enabled. Note duration tells the app to generate quarter, eighth or sixteenth notes. Generated melodies will be written to the `data/output` folder for playback in the app and also saved in MIDI format.

![generating](/docs/images/howto_generate.png)

The generation is seeded with an initial sequence that is the length of the selected Markov Chain degree (three in the example here). The application generates the initial sequence automatically starting from the base note of the key and then picking the most common next note until the initial sequence is created after which every next note follows the probability distribution. Generation should be more or less instantaneous and the file should become visible in the dropdown. Once a file is selected playback can be started.

![playback](/docs/images/howto_playback.png)

Playback happens on another thread so as to not block the UI and therefore also prevent pressing the stop button. Other controls are disabled during playback. It's possible that the generation is shorter if it found a sequence with no following notes.

NOTE: Listening the generation from `data/output` with an external player that handles MIDI is slightly more smooth. The playback in the app it is often not perfectly in sync right in the beginning but evens out quite quick to a stable rhythm.

![stop](/docs/images/howto_stop.png)
