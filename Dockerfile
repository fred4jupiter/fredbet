FROM openjdk:8-jre-slim

LABEL maintainer="Michael Staehler"

# Add custom user for running the image as a non-root user
RUN useradd -ms /bin/bash freduser

RUN set -ex; \
        apt-get update && apt-get install -y \
        less \
        dos2unix \
        && chown -R freduser:0 /home/freduser \
        && chmod -R g+rw /home/freduser \
        && chmod g+w /etc/passwd

# Configure timezone
RUN echo "Europe/Berlin" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

ENV JAVA_OPTS="-Xms256m -Xmx1024m"

EXPOSE 8080

COPY target/fredbet.jar /home/freduser/fredbet.jar

RUN sh -c 'touch /home/freduser/fredbet.jar'

USER freduser

WORKDIR /home/freduser

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/freduser/fredbet.jar" ]
