# FredBet #

Simple football betting application using Spring Boot and MongoDB. You can run the application by issuing the following commands:

	mvn clean install spring-boot:run

The application is available under [http://localhost:8080/](http://localhost:8080/). Log in with the admin default account credentials `admin/admin`.

## Running within Docker ##

	mvn clean install docker:build
	docker run -d -p 8080:8080 fred4jupiter/fredbet

This will build (and run) an image with name `fred4jupiter/fredbet`. In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running within Docker Compose##

There is a docker compose file available to run the application with a MariaDB database in one docker container and the application in another. For that run

	cd src/docker/docker-compose/mariadb
	docker-compose up -d

The application is available under [http://localhost:8080/](http://localhost:8080/).

## Travis Build Status ##
[![Build Status](https://travis-ci.org/fred4jupiter/fredbet.svg?branch=master)](https://travis-ci.org/fred4jupiter/fredbet)

## CircleCI Build Status ##

[![Circle CI](https://circleci.com/gh/fred4jupiter/fredbet.svg?style=shield)](https://circleci.com/gh/fred4jupiter/fredbet)
