#!/bin/bash

# release section
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$GO_PIPELINE_COUNTER versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$GO_PIPELINE_COUNTER -Dusername=$GITHUB_USERNAME -Dpassword=$GITHUB_PASSWORD
mvn verify -DskipTests
docker build -t opitzconsulting/fredbet .
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-SNAPSHOT versions:commit

# tag image and push to docker cloud repository
docker login -e $DOCKER_EMAIL -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker tag -f opitzconsulting/fredbet hamster40/fredbet:latest
docker push hamster40/fredbet:latest

# redeploy service in docker-cloud
# docker-cloud service redeploy fredbet