# Current test coverage

![test coverage](/docs/images/coverage_report.png)

## Tests

Tested modules are domain, services and utils. Entrypoint and JavaFX launcher (org.juhanir) and the UI module (org.juhanir.view) are not unit-tested. UI layer is tested with a smoke system test as explained below.

### Unit tests

The project is unit tested with JUnit. Tests are run automatically in CI environment when opening/updating a pull request. A failed unit test run will prevent merging the pull request. Previous unit test runs can be viewed from https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml.

Full unit test suite can be run manually from the project root with:
```sh
mvn clean test -f melodify/pom.xml
```

Inputs for strict unit tests (testing one method in isolation) are usually formed in the test class and consist of valid and invalid inputs for the methods in question. Classes are also tested in integration with each other. For these "integration tests", inputs are from real training data when feasible (see for example [TrainingServiceTest.java](/melodify/src/test/java/org/juhanir/services/TrainingServiceTest.java)).


### Mutation tests

This project uses a mutation test framework called pitest (see brief intro at https://pitest.org/). It also collects line coverage which can be seen in the current coverage image above. The report will be generated under the `melodify/target` folder in html format and can be viewed in the browser. Mutation tests are currently not part of CI and can be run manually with:

```sh
mvn clean test-compile pitest:mutationCoverage -f melodify/pom.xml
```

### Testing the generation results

As the generated melodies are produced by weighted random selection, it is not possible to test them in great accuracy other than to check that they exist in the input data. Unit tests cover cases *with small inputs* where each generated sequence is checked to exist in the source data. Some tests use large inputs from multiple actual training data files to get more confidence on the generation correctness.

### Performace tests

None at the moment, the trie lookup and insert operations might be somehow tested to be linear dependent on the key size? At least generations are almost instantaneous from the UI.

### User Interface tests

The project uses TestFX framework to test the running application by interacting with the UI and these are the only automated tests that target the UI layer. While the tests run, don't interfere with the UI. These tests are excluded from the maven lifecycle and can only be run manually:

```sh
mvn -DskipUItests=false clean test-compile failsafe:integration-test -f melodify/pom.xml
```
