# FredBet #

Simple football betting application using Spring Boot and MariaDB. You can run the application by issuing the following command:

	mvn install spring-boot:run

The application is available under [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

## Running the released Docker image##

The released docker image is available on [Docker Hub](https://hub.docker.com/r/hamsterhase/fredbet).

	docker run -d -p 8080:8080 hamsterhase/fredbet

## Building your own Docker image##

	mvn clean install docker:build
	docker run -d -p 8080:8080 hamsterhase/fredbet

This will build (and run) an image with name `hamsterhase/fredbet`. In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running with Docker Compose##

There is a docker compose file available to run the application with a MariaDB database in one docker container and the application in another. This is recommended for production use.

	cd src/docker/docker-compose/mariadb
	docker-compose up -d

The application is available under [http://localhost:8080/](http://localhost:8080/).

A sample docker compose file will look like this:

	mariadb:  
	  image: mariadb:10.1.11
	  volumes:
	    - /data/db
	  ports:
	    - "3306:3306"
	  environment:
	   - MYSQL_DATABASE=fredbetdb
	   - MYSQL_ROOT_PASSWORD=secred
	   - MYSQL_USER=<USERNAME>
	   - MYSQL_PASSWORD=<PASSWORD>
	
	fredbet:
	  image: hamsterhase/fredbet
	  links:
	    - mariadb:mariadb
	  ports:
	    - "8080:8080"
	  environment:
	   - spring.profiles.active=docker
	   - JDBC_URL=jdbc:mariadb://mariadb:3306/fredbetdb
	   - JDBC_USERNAME=<USERNAME>
	   - JDBC_PASSWORD=<PASSWORD>

## Screenshot ##

![FredBet Screenshot](/src/docs/screenshot/Screenshot1.jpg?raw=true "FredBet Screenshot")

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

