#!/bin/bash

# call script like so: source ./release_only.sh

if [ $# -eq 0 ]; then
    echo "No arguments provided. Usage: /release_only.sh GITHUB_USERNAME GITHUB_PASSWORD DOCKER_EMAIL DOCKER_USERNAME DOCKER_PASSWORD"
    exit 1
fi

if [ -z "$1" ]
  then
    echo "The GITHUB username has to be given as first parameter."
    exit 1
fi

if [ -z "$2" ]
  then
    echo "The GITHUB password has to be given as second parameter."
    exit 1
fi

if [ -z "$3" ]
  then
    echo "The DOCKER_EMAIL has to be given as third parameter."
    exit 1
fi

if [ -z "$4" ]
  then
    echo "The DOCKER_USERNAME has to be given fourth parameter."
    exit 1
fi

if [ -z "$5" ]
  then
    echo "The DOCKER_PASSWORD has to be given fourth parameter."
    exit 1
fi

GITHUB_USERNAME=$1
GITHUB_PASSWORD=$2
DOCKER_EMAIL=$3
DOCKER_USERNAME=$4
DOCKER_PASSWORD=$5

git config user.name "fred4jupiter"
git config user.email "hamsterhase@gmx.de"

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} -Dusername=$GITHUB_USERNAME -Dpassword=$GITHUB_PASSWORD
mvn clean package -DskipTests
PROJECT_REL_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit

NEXT_DEV_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

echo "release version is: $PROJECT_REL_VERSION"
echo "next development version is: $NEXT_DEV_VERSION"


# docker image tagging and publishing in Docker Hub
docker login -e $DOCKER_EMAIL -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:$PROJECT_REL_VERSION
docker push hamsterhase/fredbet:$PROJECT_REL_VERSION
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:latest
docker push hamsterhase/fredbet:latest

docker logout
