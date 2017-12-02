# FredBet

## What´s FredBet?

Simple football betting application using [Spring Boot](https://projects.spring.io/spring-boot/), [Thymeleaf](http://www.thymeleaf.org/) and [Bootstrap](http://getbootstrap.com/). The web pages are constructed in responsive design for using on mobile devices.

## Features

- simple betting of football championchips
- responsive design (mobile first design)
- extra betting of 1st, 2nd and 3rd winner
- image gallery (image storage support: filesystem, database, AWS S3)
- user profile image
- multiple database types supported (H2, MariaDB, MySQL, PostgeSQL)
- integrated user administration
- rich text editor for rules, prices and misc pages
- points statistic
- display other users bets after match kickoff
- ranking page
- Microsoft Excel match import
- Microsoft Excel bets, statistic export
- language switcher (supported languages: englisch, german (by now))
- ranking filter for adults and childs listing

## Testing it locally

You can run the application by issuing the following command:

```bash
mvn install spring-boot:run
```

The application is available under [http://localhost:8080/](http://localhost:8080/) and runs (by default) with an in-memory H2 database. Log in with the admin account `admin/admin`.

In the `dev` profile (which will be activated if no profile is specified) the application starts with an embedded in-memory H2 database.

## Running the released Docker image

The released docker image is available on [Docker Hub](https://hub.docker.com/r/fred4jupiter/fredbet).

```bash
docker run -d -p 8080:8080 fred4jupiter/fredbet
```

## Building your own Docker image

```bash
mvn clean install docker:build
docker run -d -p 8080:8080 fred4jupiter/fredbet
```

This will build (and run) an image with name `fred4jupiter/fredbet`. 

## Running with Docker Compose

There are some docker compose files available to run the application e.g. with a separate database. This configuration is recommended for production use.

You can find the docker compose files in folder

```
src/docker/docker-compose
```

Example for FredBet with MariaDB:

```bash
cd src/docker/docker-compose
docker-compose -f mariadb.yml up -d
```

## FredBet Properties

| Key | Default Value | Description |
|--------|--------|--------|
| fredbet.create-demo-data | false | Creates demo data with additional users and matches. |
| fredbet.enable-demo-data-creation-navigation-entry | false | Disables the navigation entry for (re)creating the matches and demo results. |
| fredbet.favourite-country | germany | Sum points per user for selected country that will be shown in points statistics. |
| fredbet.image-size | 1920 | Pixel length side for storing images in photo gallery. |
| fredbet.thumbnail-size | 75 | Pixel length side for storing thumbnail images. |
| fredbet.image-location | FILE_SYSTEM | Location where to store the images/photos. Possible values: FILE_SYSTEM, DATABASE, AWS_S3 |
| fredbet.image-file-sytem-base-folder | the users home folder | In case you selected to save the images in file system this is the path to the folder. |
| fredbet.password-for-reset | fredbet | If the administrator resets the password of a user this password will be set as default password. The user should changed it after login. |
| fredbet.aws-s3bucket-name | mis-demo | Name of the AWS bucket if the image location is set to AWS_S3. |
| fredbet.points-final-winner | 10 | Extra points a user will get for betting the final winner correctly. |
| fredbet.points-semi-final-winner | 5 | Extra points a user will get for betting the semi final winner correctly. |
| fredbet.points-third-final-winner | 2 | Extra points a user will get for betting the third final winner correctly. |

Please have a look at [Spring Boots externalized configuration documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) on how to setup these properties as JVM parameters or environment variables.

## Database Properties

| Key | Default Value | Description |
|--------|--------|--------|
| fredbet.database-url | | The database jdbc connection url, e.g. jdbc:mariadb://localhost:3306/fredbetdb. |
| fredbet.database-username | | The database username. |
| fredbet.database-password | | The database password. |
| fredbet.database-type | h2 | The used database type. Choose one of these values: h2, maria-db, mysql, postgres |

## Production Environment

FredBet is designed to run within the Amazon Web Services (AWS) cloud as production environment. Typically you run the docker container in EC2 container service (ECS) with these environment properties while storing the images of the image gallery in S3:

| Key | Value | Description |
|--------|--------|--------|
| spring.profiles.active | prod |  |
| fredbet.image-location | AWS_S3 |  |
| fredbet.aws-s3bucket-name | fredbet | Or any other name for your S3 bucket. |
| fredbet.database-url | e.g. jdbc:mysql://HOST:3306/DB_NAME |  |
| fredbet.database-username | fredbet | The database username. |
| fredbet.database-password | password | The database password. |
| fredbet.database-type | mysql |  |

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
