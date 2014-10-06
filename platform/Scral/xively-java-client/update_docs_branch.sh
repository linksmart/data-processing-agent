#!/bin/bash -x
rev=$(git rev-parse --short HEAD)
rm -rf doc/
javadoc -d doc/ \
    -sourcepath src/main/java/ \
    -subpackages com.xively.client \
    -stylesheetfile src/main/res/doc_stylesheet.css \
  && cd doc/ \
  && git init \
  && git remote add github git@github.com:xively/Xively4J \
  && git add . \
  && git commit -m "[docs] Regerated documentation for $rev" \
  && git push github master:gh-pages -f
