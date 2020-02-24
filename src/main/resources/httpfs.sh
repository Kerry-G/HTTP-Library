#!/bin/bash
DIR="$(dirname "$0")/httpfs.jar-jar-with-dependencies.jar"
java -jar "$DIR" "$@"
