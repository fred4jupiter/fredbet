FROM maven:3.8.4-openjdk-17 as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-jre-focal
LABEL maintainer="Michael Staehler"

WORKDIR application

ENV JAVA_OPTS="-Duser.timezone=Europe/Berlin -Djava.security.egd=file:/dev/./urandom"

COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
