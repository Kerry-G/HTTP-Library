#!/bin/bash
DIR="$(dirname "$0")/httpc.jar-jar-with-dependencies.jar"
java -jar "$DIR" "$@"
