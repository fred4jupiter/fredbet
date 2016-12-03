#!/bin/bash

# call script like so: source ./release_only.sh

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

git config user.name "fred4jupiter"
git config user.email "hamsterhase@gmx.de"

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} -Dusername=$1 -Dpassword=$2
mvn package -DskipTests
PROJECT_REL_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit
mvn scm:checkin -Dbasedir=. -Dmessage="next dev version" -Dusername=$1 -Dpassword=$2

NEXT_DEV_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

echo "release version is: $PROJECT_REL_VERSION"
echo "next development version is: $NEXT_DEV_VERSION"

export PROJECT_REL_VERSION
export NEXT_DEV_VERSION
