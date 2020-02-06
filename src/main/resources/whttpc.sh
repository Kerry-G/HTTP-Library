#!/bin/bash

java -jar ./HTTP-Library-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
echo "Press any key to continue"
while [ true ] ; do
read -t 3 -n 1
if [ $? = 0 ] ; then
exit ;
else
echo "waiting for the keypress"
fi
done
