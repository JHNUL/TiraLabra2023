#!/bin/sh

# remove existing jar if any
rm -rf melodify/shade

# build the jar
mvn clean compile package -f melodify/pom.xml

# copy to project root where access to data works properly
cp melodify/shade/*.jar .
