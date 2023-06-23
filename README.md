# FredBet

## What´s FredBet?

Simple football betting application using [Spring Boot](https://projects.spring.io/spring-boot/), [Thymeleaf](http://www.thymeleaf.org/) and [Bootstrap](http://getbootstrap.com/). The web pages are constructed in responsive design for using on mobile devices.

## Features

- simple betting of football championchips
- responsive design (mobile first design)
- extra betting of 1st, 2nd and 3rd winner
- image gallery (image storage support: filesystem, database)
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
- Group standings
- change UI design themes

## Testing it locally

You can run the application by issuing the following command:

```bash
mvn install spring-boot:run
```

The application is available under [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running the released Docker image

The released docker image is available on:

### Docker Hub

You can browse the available releases at [Docker Hub](https://hub.docker.com/r/fred4jupiter/fredbet). Please check
for the latest released version or use the `latest` tag for current development image (not stable).

```bash
docker run -d -p 8080:8080 fred4jupiter/fredbet:2.8.2
```

### Github Container Registry

```bash
docker run -d -p 8080:8080 ghcr.io/fred4jupiter/fredbet:2.8.2
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

These properties has to be set at application startup.

| Key | Default Value | Description |
|--------|--------|--------|
| spring.profiles.active | h2 | Active Spring profile at startup. Possible values: `h2,dev,maria,mysql,postgres`.  |
| fredbet.image-location | DATABASE | Location where to store the images/photos. Possible values: `FILE_SYSTEM, DATABASE` |
| fredbet.image-file-system-base-folder | user home folder | In case you selected to save the images in file system this is the path to the folder. |
| fredbet.default-language | de | The default language you prefer. |

Please have a look at [Spring Boots externalized configuration documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) on how to setup these properties as JVM parameters or environment variables.

## Database Properties

| Key | Default Value | Description |
|--------|--------|--------|
| spring.datasource.url | jdbc:h2:file:~/fredbet/fredbetdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE | JDBC connection URL. |
| spring.datasource.username | sa | The database username. |
| spring.datasource.password | | The database password. |
| spring.datasource.driver-class-name | org.h2.Driver | see driver class below |

### Driver class names

| Database | Driver Class | Spring Profile | 
|--------|--------|--------|
| H2 | org.h2.Driver | h2 |
| MySQL | com.mysql.jdbc.Driver | mysql |
| MariaDB | org.mariadb.jdbc.Driver |  maria |
| PostgreSQL | org.postgresql.Driver | postgres |

Activate the spring profile via JVM (e.g. `-Dspring.profiles.active=h2`) or system environment variable (e.g. `SPRING_PROFILES_ACTIVE=h2`).

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

## Production Environment

FredBet is designed to run within the Amazon Web Services (AWS) cloud as production environment. 
Typically you run the docker container in EC2 container service (ECS) with these environment properties:

| Key | Value | Description |
|--------|--------|--------|
| spring.profiles.active | e.g. postgres | see section `Driver class names` |

Other properties depend on your production setup (see possible properties above). Add also the properties for your database connection (see above).

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

While running in `dev` profile the H2 web console is available at [http://localhost:8080/console](http://localhost:8080/console).

## MS Excel-Templates ready to import

The following Microsoft Excel templates with matches are prepared for import:

* [Women World Championship 2023 Template](extra/excel_templates/Women_WC_2023.xlsx?raw=true)

## Screenshots

![FredBet Screenshot 1](extra/docs/screenshot/Screenshot1.jpg?raw=true "FredBet Screenshot 1")

![FredBet Screenshot 2](extra/docs/screenshot/Screenshot2.jpg?raw=true "FredBet Screenshot 2")

![FredBet Screenshot 3](extra/docs/screenshot/Screenshot3.jpg?raw=true "FredBet Screenshot 3")

## GitHub Build Status

![Docker latest](https://github.com/fred4jupiter/fredbet/actions/workflows/publish-docker-latest.yml/badge.svg)

![Docker Release](https://github.com/fred4jupiter/fredbet/actions/workflows/publish-release.yml/badge.svg)
