#!/bin/bash

# call script like so: source ./release_only.sh <BUILD_NUMBER>

if [ -z "$1" ]
  then
    echo "No argument supplied. Please give build number as parameter."
    exit 1
fi

if [ -z "$GITHUB_USERNAME" ]
  then
    echo "The GITHUB username has to be set in variable 'GITHUB_USERNAME'."
    exit 1
fi

if [ -z "$GITHUB_PASSWORD" ]
  then
    echo "The GITHUB password has to be set in variable 'GITHUB_PASSWORD'."
    exit 1
fi

echo "Build number is: $1"

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$1 versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}.$1 -Dusername=$GITHUB_USERNAME -Dpassword=$GITHUB_PASSWORD
mvn package -DskipTests
PROJECT_REL_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion}-SNAPSHOT versions:commit
echo "project release version is: $PROJECT_REL_VERSION"
export PROJECT_REL_VERSION
