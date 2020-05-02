# Step : Test and package
FROM maven:3.6.3-openjdk-11 as target
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src/ /build/src/
COPY .git /build/.git/
RUN mvn -B package

# Step : Package image
FROM openjdk:11-jre-slim
LABEL maintainer="Michael Staehler"

VOLUME /tmp

# Add custom user for running the image as a non-root user (but in root group)
RUN useradd -ms /bin/bash fred

RUN set -ex; \
        apt-get update \        
        && chown -R fred:0 /home/fred \
        && chmod -R g+rw /home/fred \
        && chmod g+w /etc/passwd

ENV JAVA_OPTS="-Duser.timezone=Europe/Berlin"

USER fred

WORKDIR /home/fred

EXPOSE 8080

COPY --from=target /build/target/fredbet.jar /home/fred/fredbet.jar
CMD exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/fred/fredbet.jar