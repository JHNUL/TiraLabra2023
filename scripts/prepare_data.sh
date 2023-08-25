#!/bin/sh

DATA_SET="irish"

if [ ! -z "$1" ]; then
    DATA_SET="$1"
fi

if [ "$DATA_SET" != "irish" ] && [ "$DATA_SET" != "classical" ]; then
    echo "Data set $DATA_SET not recognized, exiting..."
    exit 1
fi

ARCHIVE="data/$DATA_SET.zip"
FOLDER="data/musicxml"

if [ ! -f "$ARCHIVE" ]; then
    echo "No zip archive detected, exiting..."
    exit 1
fi

if [ -d "$FOLDER" ]; then
    echo "Source directory already exists"
    echo -n "Continue and overwrite all content? (yes/no): "
    read choice
    if [ "$choice" != "yes" ]; then
        exit 0
    fi
    rm "$FOLDER"/*
fi

mkdir -p "$FOLDER"

unzip -qo "$ARCHIVE" -d "$FOLDER"

COUNT=$(ls "$FOLDER" | wc -l)

echo "Unzipped dataset $DATA_SET ($COUNT files) to $FOLDER."
