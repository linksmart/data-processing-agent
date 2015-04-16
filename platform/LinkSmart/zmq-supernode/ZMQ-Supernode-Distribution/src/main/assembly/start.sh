#!/bin/bash

CP=""

for jar in lib/*.jar; do
CP+=$jar:
done

java -client -Dlog4j.debug=true -Dlog4j.configuration=file:$PWD/log4j.properties -cp $CP eu.linksmart.gc.supernode.ProxyApplication "$@";
