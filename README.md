# FredBet

Simple football betting application using [Spring Boot](https://projects.spring.io/spring-boot/), [MariaDB](https://mariadb.org/), [Thymeleaf](http://www.thymeleaf.org/) and [Bootstrap](http://getbootstrap.com/). The web pages are constructed in responsive design for using on mobile devises.

You can run the application by issuing the following command:

```bash
mvn install spring-boot:run
```

The application is available under [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

## Running the released Docker image

The released docker image is available on [Docker Hub](https://hub.docker.com/r/hamsterhase/fredbet).

```bash
docker run -d -p 8080:8080 hamsterhase/fredbet
```

## Building your own Docker image

```bash
mvn clean install docker:build
docker run -d -p 8080:8080 hamsterhase/fredbet
```

This will build (and run) an image with name `hamsterhase/fredbet`. In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running with Docker Compose

There is a docker compose file available to run the application with a MariaDB database in one docker container and the application in another. This is recommended for production use.

```bash
cd src/docker/docker-compose/mariadb
docker-compose up -d
```

The application is available under [http://localhost:8080/](http://localhost:8080/).

A sample `docker-compose.yml` file with containerized MariaDB and FredBet will look like this:

```yml
version: '2'
services:
    mariadb:
      image: mariadb:10.1.11
      volumes:
        - /data/db
      ports:
        - "3306:3306"
      environment:
       - MYSQL_DATABASE=fredbetdb
       - MYSQL_ROOT_PASSWORD=secred
       - MYSQL_USER=fred
       - MYSQL_PASSWORD=fred
    fredbet:
      image: hamsterhase/fredbet
      links:
        - mariadb:mariadb
      depends_on:
        - mariadb
      ports:
        - "8080:8080"
      environment:
       - spring.profiles.active=prod
       - FREDBET_DATABASE-URL=jdbc:mariadb://mariadb:3306/fredbetdb
       - FREDBET_DATABASE-USERNAME=fred
       - FREDBET_DATABASE-PASSWORD=fred
```

## FredBet Properties

You can ajust some properties by overriding it as JVM parameters. The properties are located in class `FredbetProperties`.

- `fredbet.create-demo-data`
	- Creates demo data with additional users and matches.
- `fredbet.enable-demo-data-creation-navigation-entry`
	- Disables the navigation entry for (re)creating the matches and demo results.
- `fredbet.favourite-country`
	- Sum points per user for selected country that will be shown in points statistics.
- `fredbet.database-url`
	- The database jdbc connection url, e.g. jdbc:mariadb://localhost:3306/fredbetdb.
- `fredbet.database-username`
	- The database username.
- `fredbet.database-password`
	- The database password.

NOTE: For overriding these properties as JVM parameters you have to apply the Spring Boot Externalize Properties Convention, e.g. for setting the property `fredbet.favourite-country` you will write `-DFREDBET_FAVOURITE-COUNTRY=ireland`.

## Hints

```bash
-Dflyway.enabled=false
```
Disabling Flyway database migration at all. This may be useful if you have an already populated database schema.

## Screenshot

![FredBet Screenshot](src/docs/screenshot/Screenshot1.jpg?raw=true "FredBet Screenshot")

## License

<a rel="license" href="http://creativecommons.org/licenses/by-sa/4.0/">
	<img alt="Creative Commons Lizenzvertrag" style="border-width:0" src="https://i.creativecommons.org/l/by-sa/4.0/88x31.png" />
</a> FredBet (c) by Michael Stähler

FredBet is licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.

You should have received a copy of the license along with this work. If not, see [http://creativecommons.org/licenses/by-sa/4.0/](http://creativecommons.org/licenses/by-sa/4.0/).

## Travis Build Status
[![Build Status](https://travis-ci.org/fred4jupiter/fredbet.svg?branch=master)](https://travis-ci.org/fred4jupiter/fredbet)

## CircleCI Build Status

[![Circle CI](https://circleci.com/gh/fred4jupiter/fredbet.svg?style=shield)](https://circleci.com/gh/fred4jupiter/fredbet)

