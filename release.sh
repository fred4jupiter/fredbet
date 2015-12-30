#!/bin/bash

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$CIRCLE_BUILD_NUM versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$CIRCLE_BUILD_NUM -Dusername=$GITHUB_USERNAME -Dpassword=$GITHUB_PASSWORD
docker build -t fred4jupiter/fredbet .
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-SNAPSHOT versions:commit
