#!/bin/sh

CLEAN=no

if [ ! -z "$1" ] && [ "$1" = "yes" ]; then
    # remove existing generations
    rm -f data/output/*.staccato
    rm -f data/output/*.MID
fi

# start
mvn clean javafx:run -f melodify/pom.xml
