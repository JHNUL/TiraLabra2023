DATA_SET=irish
CLEAN=no

dataset:
	./scripts/prepare_data.sh $(DATA_SET)

install:
	mvn clean install -f melodify/pom.xml

start:
	./scripts/start.sh $(CLEAN)

build:
	./scripts/build.sh

site:
	mvn clean site -f melodify/pom.xml

test:
	mvn clean test -f melodify/pom.xml

mutation_test:
	mvn clean test-compile pitest:mutationCoverage -f melodify/pom.xml

ui_test:
	rm -f data/output/*.staccato
	rm -f data/output/*.MID
	mvn -DskipUItests=false clean test-compile failsafe:integration-test -f melodify/pom.xml
