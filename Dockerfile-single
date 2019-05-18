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

EXPOSE 8080

COPY target/fredbet.jar /home/fred/fredbet.jar

RUN sh -c 'touch /home/fred/fredbet.jar'

USER fred

WORKDIR /home/fred

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/fred/fredbet.jar" ]