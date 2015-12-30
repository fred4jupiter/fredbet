#!/bin/bash

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$CIRCLE_BUILD_NUM versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$CIRCLE_BUILD_NUM
docker build -t fred4jupiter/fredbet .
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-SNAPSHOT versions:commit

# Deploy section
docker login -e $TUTUM_EMAIL -u $TUTUM_USER -p $TUTUM_PASS  tutum.co
docker tag -f fred4jupiter/fredbet tutum.co/fred4jupiter/fredbet:$CIRCLE_BUILD_NUM
docker push tutum.co/fred4jupiter/fredbet:$CIRCLE_BUILD_NUM
