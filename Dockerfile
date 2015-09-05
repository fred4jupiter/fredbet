FROM java:8

MAINTAINER Michael Staehler <hamsterhase@gmx.de>

RUN echo 'Building docker image for fredbet application...'v

# Configure timezone
RUN echo "Europe/Berlin" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

ENV spring.profiles.active dev

VOLUME /tmp

EXPOSE 8080
EXPOSE 2000

ADD fredbet-0.0.1-SNAPSHOT.jar fredbet.jar

RUN bash -c 'touch /fredbet.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/fredbet.jar"]