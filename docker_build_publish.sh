#!/bin/bash

if [ -z "$1" ]
  then
    echo "The DOCKER_EMAIL has to be given as first parameter."
    exit 1
fi

if [ -z "$2" ]
  then
    echo "The DOCKER_USERNAME has to be given as second parameter."
    exit 1
fi

if [ -z "$3" ]
  then
    echo "The DOCKER_PASSWORD has to be given as third parameter."
    exit 1
fi

if [ -z "$4" ]
  then
    echo "The RELEASE_VERSION has to be given as fourth parameter."
    exit 1
fi

DOCKER_EMAIL=$1
DOCKER_USERNAME=$2
DOCKER_PASSWORD=$3
RELEASE_VERSION=$4

# tag image and push to docker hub
docker login -e $DOCKER_EMAIL -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:$RELEASE_VERSION
docker push hamsterhase/fredbet:$RELEASE_VERSION
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:latest
docker push hamsterhase/fredbet:latest

