FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /build

COPY pom.xml .
COPY src/ /build/src/
COPY .git /build/.git/
RUN mvn -B dependency:go-offline -DskipTests package

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

# --------------------------------------------------------------------
FROM eclipse-temurin:17-jre-focal
LABEL maintainer="Michael Staehler"

WORKDIR /build

ENV JAVA_OPTS="-Duser.timezone=Europe/Berlin -Djava.security.egd=file:/dev/./urandom"

COPY --from=builder /build/dependencies/ ./
COPY --from=builder /build/spring-boot-loader/ ./
COPY --from=builder /build/snapshot-dependencies/ ./
COPY --from=builder /build/application/ ./
ENTRYPOINT exec java $JAVA_OPTS "org.springframework.boot.loader.JarLauncher"
