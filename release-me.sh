#!/bin/bash

for i in "$@"
do
case $i in
    -du=*|--docker-username=*)
    DOCKER_USERNAME="${i#*=}"
    ;;
    -dp=*|--docker-password=*)
    DOCKER_PASSWORD="${i#*=}"
    ;;
    -gu=*|--github-username=*)
    GITHUB_USERNAME="${i#*=}"
    ;;
    -gp=*|--github-password=*)
    GITHUB_PASSWORD="${i#*=}"
    ;;
esac
done

if [ $# -eq 0 ]; then
    echo "No arguments provided. Usage example: ./release-me.sh -du=fred -dp='secret' -gu=freddy -gp='secret'"
    exit 1
fi

if [ -z "$DOCKER_USERNAME" ]
  then
    echo "Please define the Docker username. Example: -du=fred"
    exit 1
fi

if [ -z "$DOCKER_PASSWORD" ]
  then
    echo "Please define the Docker password. Example: -dp=secret"
    exit 1
fi

if [ -z "$GITHUB_USERNAME" ]
  then
    echo "Please define the Github username. Example: -gu=freddy"
    exit 1
fi

if [ -z "$GITHUB_PASSWORD" ]
  then
    echo "Please define the Github password. Example: -gp=secret"
    exit 1
fi

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

# docker image tagging and publishing on Docker Hub
echo "$DOCKER_PASSWORD" | docker login --username "$DOCKER_USERNAME" --password-stdin
docker tag fred4jupiter/fredbet fred4jupiter/fredbet:"$PROJECT_REL_VERSION"
docker push fred4jupiter/fredbet:"$PROJECT_REL_VERSION"
docker logout
