FROM java:8

MAINTAINER Michael Staehler <hamsterhase@gmx.de>

RUN set -ex; \
        apt-get update && apt-get install -y \
        less \
        dos2unix

# Configure timezone
RUN echo "Europe/Berlin" > /etc/timezone
RUN dpkg-reconfigure -f noninteractive tzdata

ENV JAVA_OPTS="-Xmx2048m"

EXPOSE 8080

# Run the image as a non-root user
RUN useradd -ms /bin/bash freduser

COPY target/fredbet.jar /home/freduser/fredbet.jar

RUN sh -c 'touch /home/freduser/fredbet.jar'

USER freduser

WORKDIR /home/freduser

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /home/freduser/fredbet.jar" ]
