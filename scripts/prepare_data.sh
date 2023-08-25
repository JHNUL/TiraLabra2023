#!/bin/sh

ARCHIVE="data/music_big.zip"
FOLDER="data/musicxml"

if [ ! -f "$ARCHIVE" ]; then
    echo "No zip archive detected, exiting..."
    exit 1
fi

if [ -d "$FOLDER" ]; then
    echo "Source directory already exists"
    read -p "Continue and overwrite all content? (yes/no): " choice
    if [ "$choice" != "yes" ]; then
        exit 0
    fi
    rm "$FOLDER"/*
fi

mkdir -p "$FOLDER"

unzip -qo "$ARCHIVE" -d "$FOLDER"

COUNT=$(ls "$FOLDER" | wc -l)

echo "Unzipped $COUNT files to $FOLDER."
