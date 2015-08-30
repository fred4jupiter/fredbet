FROM java:8

MAINTAINER Michael Staehler <hamsterhase@gmx.de>

RUN echo 'Building docker image for fredbet application...'v

# Configure timezone and locale
RUN echo "Europe/Berlin" > /etc/timezone; dpkg-reconfigure -f noninteractive tzdata
RUN export LANGUAGE=de_DE.UTF-8; export LANG=de_DE.UTF-8; export LC_ALL=de_DE.UTF-8; locale-gen de_DE.UTF-8; DEBIAN_FRONTEND=noninteractive dpkg-reconfigure locales

ENV spring.profiles.active dev

VOLUME /tmp

EXPOSE 8080

ADD fredbet-0.0.1-SNAPSHOT.jar fredbet.jar

RUN bash -c 'touch /fredbet.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/fredbet.jar"]