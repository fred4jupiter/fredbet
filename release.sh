#!/bin/bash

CIRCLE_BUILD_NUM=1
export CIRCLE_BUILD_NUM

echo $CIRCLE_BUILD_NUM

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.\$CIRCLE_BUILD_NUM versions:commit
mvn scm:tag -Dbasedir=. -Dtag=release_\${project.version}
docker build -t fred4jupiter/fredbet .
mvn versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-SNAPSHOT
