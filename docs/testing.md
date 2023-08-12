# Current test coverage

![test coverage](/docs/images/coverage_report.png)

## Tests

### Unit tests

The project is unit tested with JUnit. Tests are run automatically in CI environment when opening/updating a pull request. A failed unit test run will prevent merging the pull request. Previous unit test runs can be viewed from https://github.com/JHNUL/TiraLabra2023/actions/workflows/run_unit_tests.yaml.

Full unit test suite can be run manually from the project root with `mvn test -f melodify/pom.xml`.

Inputs for strict unit tests (testing one method in isolation) are usually formed in the test class and consist of valid and invalid inputs for the methods in question. Classes are also tested in integration with each other. For these "integration tests", inputs are from real training data when feasible (see for example [TrainingServiceTest.java](/melodify/src/test/java/org/juhanir/services/TrainingServiceTest.java)).

The user interface class is currently tested only manually and in an exploratory way during development.

### Mutation tests

This project uses a mutation test framework called pitest (see brief intro at https://pitest.org/). It also collects line coverage which can be seen in the current coverage image above. Mutation tests can be run manually with `mvn test-compile pitest:mutationCoverage -f melodify/pom.xml` and the report will be under the `melodify/target` folder in html format and can be viewed in the browser. Mutation tests are currently not part of CI.

### Testing the generation results

As the generated melodies are produced by weighted random selection, it is not possible to test them in great accuracy other than to check that they exist in the input data. Unit tests cover cases *with small inputs* where each generated sequence is checked to exist in the source data. For large inputs from multiple actual training data files this is not yet done.

### Performace tests

None at the moment, the trie lookup and insert operations might be somehow tested to be linear dependent on the key size? At least generations are almost instantaneous from the UI.
