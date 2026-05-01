#!/bin/bash

set -e

CSV_FILE="$1"

javac --release 8 -d bin src/core/*.java src/gui/*.java src/main/*.java 

if [ -n "$CSV_FILE" ]; then
    if [ ! -f "$CSV_FILE" ]; then
        ALT_PATH="$(pwd)/$CSV_FILE"

        if [ -f "$ALT_PATH" ]; then
            CSV_FILE="$ALT_PATH"
        else
            echo "Error: File '$CSV_FILE' not found."
            exit 1
        fi
    fi
fi

# run program
if [ -n "$CSV_FILE" ]; then
    echo "Running program with input: $CSV_FILE"
    java -cp bin main/Main "$CSV_FILE" 2>/dev/null
else
    echo "Running program without input file"
    java -cp bin main/Main 2>/dev/null
fi


# java -cp bin main/Main


