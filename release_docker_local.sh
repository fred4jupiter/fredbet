#!/bin/bash

if [ $# -eq 0 ]; then
    echo "No arguments provided. Usage: /release_only.sh DOCKER_USERNAME DOCKER_PASSWORD"
    exit 1
fi

if [ -z "$1" ]
  then
    echo "The DOCKER_USERNAME has to be given fourth parameter."
    exit 1
fi

if [ -z "$2" ]
  then
    echo "The DOCKER_PASSWORD has to be given fourth parameter."
    exit 1
fi

DOCKER_USERNAME=$1
DOCKER_PASSWORD=$2

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} -Dusername=$GITHUB_USERNAME -Dpassword=$GITHUB_PASSWORD
mvn clean verify -DskipTests
PROJECT_REL_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
docker build -t fred4jupiter/fredbet .

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit
NEXT_DEV_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

git commit -a -m "next dev version $NEXT_DEV_VERSION"
git push

echo "release version is: $PROJECT_REL_VERSION"
echo "next development version is: $NEXT_DEV_VERSION"

# docker image tagging and publishing in Docker Hub
docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker tag fred4jupiter/fredbet hamsterhase/fredbet:$PROJECT_REL_VERSION
docker push hamsterhase/fredbet:$PROJECT_REL_VERSION
docker logout
