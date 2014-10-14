#!/bin/bash -e

#
# Initialize the vars
#
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR=$DIR/..

echo "`date`: Started Simulator..."

exec java -jar eu.almanac.event.datafusion.imp-1.0.0-SNAPSHOT-jar-with-dependencies.jar "tcp://130.192.86.227:1883" 