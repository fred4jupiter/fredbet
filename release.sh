#!/bin/bash

mvn build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${CIRCLE_BUILD_NUM}
mvn scm:tag -Dbasedir=. -Dtag=release_${project.version}
mvn versions:revert