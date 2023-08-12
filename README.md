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

Select the commit with tag "weekN" to review the repository in the intended state for the week N submission.


## Instructions how to run

Prerequisites:
- JDK >= 17
- maven >= 3.8.7

Tested on the following operating systems: Debian 12, Windows 10

Run the following commands in the project root to start the application:
```sh
# Unzip training data (must run before starting the application)
./scripts/prepare_data.sh

# Install dependencies
mvn install -f melodify/pom.xml

# Start application
mvn javafx:run -f melodify/pom.xml
```

Other commands:
```sh
# Run unit tests
mvn test -f melodify/pom.xml

# Create mutation test report (found in melodify/target/pit-reports)
mvn test-compile pitest:mutationCoverage -f melodify/pom.xml

# Create site for the project (e.g. checkstyle report found in melodify/target/site)
mvn site -f melodify/pom.xml
```

### Usage

After starting the application the training data is loaded and user can select a key and the degree of the Markov's Chain (min 1 max 6) to use for training the model. Current (week4) training data package contains a set of Irish jigs in D and this should come across the generated melodies at least a little bit.

![training](/docs/images/howto_train.png)

After setting the parameters and training the model the generate button becomes enabled. Melodies will be written to file for playback. Generation should be more or less instantaneous and the file should become visible in the dropdown. Filename shows the key and degree as well as timestamp to the milliseconds in case the user mashes the generate button like I did for the example here.

![generating](/docs/images/howto_generate.png)

Once a file is selected playback can be started.

![playback](/docs/images/howto_playback.png)

Playback can be started and stopped from the buttons. Playback happens on another thread so as to not block the UI and therefore also prevent pressing the stop button. Other controls are disabled during playback. The generated melody should play from system audio with a MIDI piano sound and a hardcoded rhythm to fit the current training data. Full generated melody is 90 notes long as an arbitrary hardcoded limit for now. It's possible that the generation is shorter if it found a sequence with no following notes.

NOTE: when starting the playback it is often not perfectly in sync right in the beginning but evens out quite quick to a stable rhythm (something to fix later perhaps).

![stop](/docs/images/howto_stop.png)