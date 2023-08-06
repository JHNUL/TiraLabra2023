[![unit-tests](https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml/badge.svg)](https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml)

# Melodify

Assignment for University of Helsinki CS Data Structures and Algorithms lab.

The application will read MusicXML files as training data to populate a stochastic model that is used to predict the next note given a current note or a sequence of preceding notes. The model is using a Markov Chain of user-defined degree for the prediction.

## Instructions how to run

_Note that the project is very much in progress, implemented functionality can be perused from weekly docs._

Prerequisites:
- JDK >= 17
- maven >= 3.8.7


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

Tested on the following operating systems: Debian 12

## Documentation

[Specification (määrittelydokumentti)](/docs/specifications.md)
[Testing](/docs/testing.md)

#### Weeklies

- [Week 1](/docs/weeklies/week1.md)
- [Week 2](/docs/weeklies/week2.md)
- [Week 3](/docs/weeklies/week3.md)

Select the commit with tag "weekN" to review the repository in the intended state for the week N submission.
