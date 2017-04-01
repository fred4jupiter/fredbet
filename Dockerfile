FROM java:8

MAINTAINER Michael Staehler <hamsterhase@gmx.de>

RUN echo 'Building docker image for fredbet application...'v

# Configure timezone
RUN echo "Europe/Berlin" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

ENV spring.profiles.active dev

EXPOSE 8080
EXPOSE 2000

# Run the image as a non-root user
RUN useradd -ms /bin/bash freduser
USER freduser
WORKDIR /home/freduser

COPY target/fredbet.jar /home/freduser/fredbet.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/freduser/fredbet.jar"]
