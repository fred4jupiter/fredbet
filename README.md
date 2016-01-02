# FredBet #

Simple football betting application using Spring Boot and MongoDB. You can run the application by issuing the following commands:

	mvn clean install spring-boot:run

The application is available under [http://localhost:8080/](http://localhost:8080/). Log in with the admin default account credentials `admin/admin`.

## Running within Docker ##

	mvn clean install docker:build
	docker run -d -p 8080:8080 fred4jupiter/fredbet

This will build (and run) an image with name `fred4jupiter/fredbet`. In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory MongoDB.

### Running with data replication

There are examples for running the application with a MongoDB-Replica Set. See

	src/docker/test/docker-compose.yml
	src/docker/test/tutum-test-replicaset.yml

for this purpose.

## Other Notes ##

Notes for pushing into Tutums private repository:

	docker login tutum.co
	docker tag <USERNAME>/fredbet tutum.co/<USERNAME>/fredbet
	docker push tutum.co/<USERNAME>/fredbet

Replace `<USERNAME>` with your real user name.

## Travis Build Status ##
[![Build Status](https://travis-ci.org/fred4jupiter/fredbet.svg?branch=master)](https://travis-ci.org/fred4jupiter/fredbet)

## CircleCI Build Status ##

[![Circle CI](https://circleci.com/gh/fred4jupiter/fredbet.svg?style=shield)](https://circleci.com/gh/fred4jupiter/fredbet)
