#!/bin/sh

# remove existing generations
rm -f data/output/*.staccato
rm -f data/output/*.MID

# start
mvn clean javafx:run -f melodify/pom.xml
