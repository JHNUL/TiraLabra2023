clean_start:
	./scripts/clean_start.sh

build:
	./scripts/build.sh

prepare_data:
	./scripts/prepare_data.sh

test:
	mvn clean test -f melodify/pom.xml

mutation_test:
	mvn clean test-compile pitest:mutationCoverage -f melodify/pom.xml

ui_test:
	mvn -DskipUItests=false clean test-compile failsafe:integration-test -f melodify/pom.xml
