# FredBet

## What´s FredBet?

Simple football betting application using [Spring Boot](https://projects.spring.io/spring-boot/), [Thymeleaf](http://www.thymeleaf.org/) and [Bootstrap](http://getbootstrap.com/). The web pages are constructed in responsive design for using on mobile devices.

## Features

- simple betting of football championships
- responsive design (mobile first design)
- extra betting of 1st, 2nd and 3rd winner
- image gallery
- user profile image
- multiple database types supported (H2, MariaDB, MySQL, PostgeSQL)
- integrated user administration
- rich text editor for rules, prices and misc pages
- points statistic
- display other users bets after match kickoff
- ranking page
- Microsoft Excel match import
- Microsoft Excel bets, statistic export
- language switcher (supported languages: englisch, german, polish, catalan, spanish, swedish)
- ranking filter for adults and childs listing
- integrated testing capabilities: create demo users, matches, bets...
- configurable runtime configuration
- use a joker to double your points
- self registration (can be disabled)
- PDF export of user ranking 
- group standings
- change UI design themes

## Testing it locally
### Building and running with Java 25 and Maven installed

You can run the application locally if you have Java 25 and Maven installed. Run the following command:

```bash
mvn install spring-boot:run
```

It will build and run the application. The web UI will be available at [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

### Building and running with Docker and Docker Compose

If you have Docker and Docker Compose installed you can build the application with:

```bash
docker compose --profile app build
```

The next command starts the container connecting to the postgres database:

```bash
docker compose --profile app up -d
```

## Running the released Docker image

The released docker image is available on:

### Docker Hub

You can browse the available releases at [Docker Hub](https://hub.docker.com/r/fred4jupiter/fredbet). Please check
for the latest released version or use the `latest` tag for current development image (not stable).

```bash
docker run -d -p 8080:8080 fred4jupiter/fredbet:<VERSION_TAG>
```

Use the last version tag published or leave empty to use the latest one.

### Github Container Registry

```bash
docker run -d -p 8080:8080 ghcr.io/fred4jupiter/fredbet:<VERSION_TAG>
```

## Building your own Docker image

```bash
mvn clean install
docker build -t fredbet .
docker run -d -p 8080:8080 fredbet
```

This will build (and run) an image with name `fredbet`.

## Running with Docker Compose

There are some docker compose files available to run the application e.g. with a separate database. 
This configuration is recommended for production use.

You can find the docker compose example files in folder `extra/docker-compose` and its subdirectories.

## FredBet Properties

You can override all properties setup in the `src/main/resources/application.yml`. Please have a look 
at [Spring Boots externalized configuration documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) 
on how to setup these properties as JVM parameters or environment variables.

## Runtime Configuration

Some configuration parameters are configurable at runtime. You have to be administrator to edit these values. 
Following settings can be changed at runtime:

- Log Level
- Clearing caches (for available navigation entries, user child relation on ranking page, runtime configuration parameters)
- enable separate adults and children ranking
- changeable usernames
- menu entry for generating test data (for testing only!)
- favourite country (will be used in points statistics)
- used password on password reset
- points for extra bets
- UI design theme

## Production Setup

### Database Setup for Production Use

There are different **database** options to use with FredBet. The default one is a `h2` file-based database, but this is for **development 
and testing purposes only**. For production setup you need to use a supported database (PostgreSQL, Maria DB or MySQL).

#### H2 (default, but NOT for production use)

| JVM Key                             | ENV KEY                             | example value                                                     |
|-------------------------------------|-------------------------------------|-------------------------------------------------------------------|
| spring.datasource.driver-class-name | SPRING_DATASOURCE_DRIVER_CLASS_NAME | org.h2.Driver                                                     |
| spring.datasource.url               | SPRING_DATASOURCE_URL               | jdbc:h2:file:~/fredbetdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE |
| spring.datasource.username          | SPRING_DATASOURCE_USERNAME          | sa                                                                |
| spring.datasource.password          | SPRING_DATASOURCE_PASSWORD          |                                                                   |

These databases are supported for production use:

#### PostgreSQL

| JVM Key                             | ENV KEY                             | example value                            |
|-------------------------------------|-------------------------------------|------------------------------------------|
| spring.datasource.driver-class-name | SPRING_DATASOURCE_DRIVER_CLASS_NAME | org.postgresql.Driver                    |
| spring.datasource.url               | SPRING_DATASOURCE_URL               | jdbc:postgresql://localhost:5432/fredbet |
| spring.datasource.username          | SPRING_DATASOURCE_USERNAME          | fred                                     |
| spring.datasource.password          | SPRING_DATASOURCE_PASSWORD          | fred                                     |


#### MySQL

| JVM Key                             | ENV KEY                             | example value                        |
|-------------------------------------|-------------------------------------|--------------------------------------|
| spring.datasource.driver-class-name | SPRING_DATASOURCE_DRIVER_CLASS_NAME | com.mysql.jdbc.Driver                |
| spring.datasource.url               | SPRING_DATASOURCE_URL               | jdbc:mysql://localhost:3306/fredbet  |
| spring.datasource.username          | SPRING_DATASOURCE_USERNAME          | fred                                 |
| spring.datasource.password          | SPRING_DATASOURCE_PASSWORD          | fred                                 |


#### Maria DB

| JVM Key                            | ENV KEY                             | example value                        |
|------------------------------------|-------------------------------------|--------------------------------------|
| spring.datasource.driver-class-name | SPRING_DATASOURCE_DRIVER_CLASS_NAME | org.mariadb.jdbc.Driver              |
| spring.datasource.url              | SPRING_DATASOURCE_URL               | jdbc:mariadb://mariadb:3306/fredbet  |
| spring.datasource.username         | SPRING_DATASOURCE_USERNAME          | fred                                 |
| spring.datasource.password         | SPRING_DATASOURCE_PASSWORD          | fred                                 |


### Docker Compose with Traefik, Postgres, Let´s Encrypt Integration and AWS S3 Backup

Another option is to run all on a virtual machine with a Docker Compose file. You can use the example located in
`extra/docker-compose/traefik_letsencrypt`. Copy the `.env.template` to `.env` and set the values accordingly.

## Hints

```bash
-Dspring.liquibase.enabled = false
```
Disabling Liquibase database migration at all. This may be useful if you have an already populated database schema.

## Health Check

You can call this URL for a health check:

[http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

You will see a response of `{"status":"UP"}`. The health check URL is callable without authentication.

## H2-Console

While running in `dev` profile (JVM param: `spring.profiles.active=dev`) the H2 web console is available 
at [http://localhost:8080/console](http://localhost:8080/console).

## MS Excel-Templates ready to import

The following Microsoft Excel templates with matches are prepared for import:

* [World Cup 2026](extra/excel_templates/WorldCup_2026.xlsx?raw=true)

## Screenshots

![FredBet Screenshot 1](extra/docs/screenshot/Screenshot1.jpg?raw=true "FredBet Screenshot 1")

![FredBet Screenshot 2](extra/docs/screenshot/Screenshot2.jpg?raw=true "FredBet Screenshot 2")

![FredBet Screenshot 3](extra/docs/screenshot/Screenshot3.jpg?raw=true "FredBet Screenshot 3")

## GitHub Build Status

![Docker latest](https://github.com/fred4jupiter/fredbet/actions/workflows/publish-docker-latest.yml/badge.svg)

![Docker Release](https://github.com/fred4jupiter/fredbet/actions/workflows/publish-release.yml/badge.svg)
