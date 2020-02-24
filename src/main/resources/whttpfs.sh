#!/bin/bash

DIR="$(dirname "$0")/httpfs.jar-jar-with-dependencies.jar"
java -jar "$DIR" "$@"
echo "Press any key to continue"
while [ true ] ; do
read -t 3 -n 1
if [ $? = 0 ] ; then
exit ;
else
echo "waiting for the keypress"
fi
done
