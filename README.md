# FredBet

Simple football betting application using [Spring Boot](https://projects.spring.io/spring-boot/), [MariaDB](https://mariadb.org/), [Thymeleaf](http://www.thymeleaf.org/) and [Bootstrap](http://getbootstrap.com/). The web pages are constructed in responsive design for using on mobile devices.

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
       - fredbet.database-type=maria-db
       - FREDBET_DATABASE-URL=jdbc:mariadb://mariadb:3306/fredbetdb
       - FREDBET_DATABASE-USERNAME=fred
       - FREDBET_DATABASE-PASSWORD=fred
```

### Running with Docker Compose and a Load Balancer (HAProxy)

There is also a sample docker compose file running FredBet with HAProxy for scaling up the application instances.

The file is located at `src/docker/docker-compose/mariadb_haproxy/docker-compose.yml`.

Scaling up the docker instance will be achieved by issuing the command:

```bash
docker-compose scale fredbet=2
```

## Database Properties

You can ajust some properties by overriding it as JVM parameters. The properties are located in class `FredbetProperties`.

- `fredbet.database-url`
	- The database jdbc connection url, e.g. jdbc:mariadb://localhost:3306/fredbetdb.
- `fredbet.database-username`
	- The database username.
- `fredbet.database-password`
	- The database password.
- `fredbet.database-type=h2`
	- The used database type. Choose one of these values: `h2, maria-db, mysql, postgres`

NOTE: For overriding these properties as JVM parameters you have to apply the Spring Boot Externalize Properties Convention, e.g. for setting the property `fredbet.favourite-country` you will write `-DFREDBET_FAVOURITE-COUNTRY=ireland`.

For setting up the database you use e.g.

```bash
-DFREDBET_DATABASE-URL = jdbc:postgresql://127.0.0.1:5432/testdb
-DFREDBET_DATABASE-USERNAME = someuser
-DFREDBET_DATABASE-PASSWORD = somepass
-DFREDBET_DATABASE-TYPE = postgres
```

## Additional Properties

You can ajust some properties by overriding it as JVM parameters. The properties are located in class `FredbetProperties`.

- `fredbet.create-demo-data`
	- Creates demo data with additional users and matches.
- `fredbet.enable-demo-data-creation-navigation-entry`
	- Disables the navigation entry for (re)creating the matches and demo results.
- `fredbet.favourite-country`
	- Sum points per user for selected country that will be shown in points statistics.
- `fredbet.points-final-winner`
	- Extra betting points for winner (defaults to 10).
- `fredbet.points-semi-final-winner`
	- Extra betting points for the semi final winner (defaults to 5).

## Production Environment

FredBet is designed to run within the Amazon Web Services (AWS) cloud as production environment. Typically you run the docker container in EC2 container service (ECS) with these environment properties with storing the images of the image gallery in S3:

- `spring.profiles.active = prod`
- `fredbet.image-location = aws-s3`
- `fredbet.aws-s3bucket-name = fredbet`
  - or any other name for your S3 bucket
- `fredbet.database-url = jdbc:mysql://<HOST>:3306/<DB_NAME>`
	- for MySQL database
- `fredbet.database-username = fredbet`
- `fredbet.database-password = password`
- `fredbet.database-type = mysql`

Be sure to use an instance profile with sufficient privileges for S3. You can ajust these values with if following properties:

- `cloud.aws.credentials.profileName = ecsInstanceRole`
	- name of the instance profile (this is the default name)

The policy to access your S3 bucket will look like this:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": "s3:*",
            "Resource": "arn:aws:s3:::<BUCKET_NAME>/*"
        }
    ]
}
```

If you not want to use the instance profile for authorization (or you can´t, e.g. not running in AWS) you can set the access key and secret access key manually with these environment variables:

- `cloud.aws.credentials.accessKey = XXX`
- `cloud.aws.credentials.secretKey = XXX`
- `cloud.aws.credentials.instanceProfile = false`

## Hints

```bash
-Dliquibase.enabled = false
```
Disabling Liquibase database migration at all. This may be useful if you have an already populated database schema.

## Health Check

You can call this URL for a health check:

[http://localhost:8080/manage/health](http://localhost:8080/manage/health).

You will see a response of `{"status":"UP"}`. The health check URL is callable without authentication.

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
