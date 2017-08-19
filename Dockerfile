FROM java:8

MAINTAINER Michael Staehler <hamsterhase@gmx.de>

RUN set -ex; \
        apt-get update && apt-get install -y \
        less \
        dos2unix

# Configure timezone
RUN echo "Europe/Berlin" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

ENV spring.profiles.active dev

EXPOSE 8080

COPY target/fredbet.jar /fredbet.jar

ENV JAVA_OPTS="-Xmx2048m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /fredbet.jar" ]

