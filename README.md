# FredBet #

Simple football betting application using Spring Boot and MariaDB. You can run the application by issuing the following command:

	mvn install spring-boot:run

The application is available under [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

## Running within Docker ##

	mvn clean install docker:build
	docker run -d -p 8080:8080 fred4jupiter/fredbet

This will build (and run) an image with name `fred4jupiter/fredbet`. In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running within Docker Compose##

There is a docker compose file available to run the application with a MariaDB database in one docker container and the application in another. For that run

	cd src/docker/docker-compose/mariadb
	docker-compose up -d

The application is available under [http://localhost:8080/](http://localhost:8080/).

## License ##

<a rel="license" href="http://creativecommons.org/licenses/by-sa/4.0/">
	<img alt="Creative Commons Lizenzvertrag" style="border-width:0" src="https://i.creativecommons.org/l/by-sa/4.0/88x31.png" />
</a> FredBet (c) by Michael Stähler

FredBet is licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.

You should have received a copy of the license along with this work. If not, see [http://creativecommons.org/licenses/by-sa/4.0/](http://creativecommons.org/licenses/by-sa/4.0/).

## Travis Build Status ##
[![Build Status](https://travis-ci.org/fred4jupiter/fredbet.svg?branch=master)](https://travis-ci.org/fred4jupiter/fredbet)

## CircleCI Build Status ##

[![Circle CI](https://circleci.com/gh/fred4jupiter/fredbet.svg?style=shield)](https://circleci.com/gh/fred4jupiter/fredbet)

