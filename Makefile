dataset:
	./scripts/prepare_data.sh

clean_start:
	./scripts/clean_start.sh

build:
	./scripts/build.sh

test:
	mvn clean test -f melodify/pom.xml

mutation_test:
	mvn clean test-compile pitest:mutationCoverage -f melodify/pom.xml

ui_test:
	rm -f data/output/*.staccato
	rm -f data/output/*.MID
	mvn -DskipUItests=false clean test-compile failsafe:integration-test -f melodify/pom.xml
