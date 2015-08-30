# FredBet #

Simple football betting application using Spring Boot and MongoDB. You can build the application as a docker container by running the following commands:

	mvn clean install
	mvn docker:build

This will build an image with name `fred4jupiter/fredbet`.

Running the docker containers with mongodb issue the following command:

	docker-compose up -d
The application is available under [http://localhost:8080/](http://localhost:8080/)

Notes for pushing into Tutums private repository:

	docker login tutum.co
	docker tag fred4jupiter/fredbet tutum.co/fred4jupiter/fredbet
	docker push tutum.co/fred4jupiter/fredbet
	
Note: You can springloaded javaagent like this: -javaagent:d:/maven_local_repo/org/springframework/springloaded/1.2.4.RELEASE/springloaded-1.2.4.RELEASE.jar -noverify 