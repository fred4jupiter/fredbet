#!/bin/bash

# release section
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$CIRCLE_BUILD_NUM versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$CIRCLE_BUILD_NUM -Dusername=$GITHUB_USERNAME -Dpassword=$GITHUB_PASSWORD
mvn package
docker build -t fred4jupiter/fredbet .
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-SNAPSHOT versions:commit

# Deploy section
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:$CIRCLE_BUILD_NUM
docker push hamsterhase/fredbet:$CIRCLE_BUILD_NUM
