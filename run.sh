#!/usr/bin/env bash

set -e

mkdir -p bin

javac --release 8 -d bin src/core/*.java src/gui/*.java src/main/*.java

java -cp bin main.Main
