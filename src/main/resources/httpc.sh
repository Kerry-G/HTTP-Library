#!/bin/bash
DIR="$(dirname "$0")/HTTP-Library-1.0-SNAPSHOT-jar-with-dependencies.jar"
java -jar "$DIR" "$@"
