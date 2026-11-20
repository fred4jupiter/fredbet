ARG JRE_BASE_IMAGE=eclipse-temurin:25-jre-jammy

FROM ${JRE_BASE_IMAGE}

LABEL maintainer="Michael Staehler"

RUN apt-get update -qqy \
 && DEBIAN_FRONTEND=noninteractive apt-get upgrade -qqy \
 && rm -rf /var/lib/apt/lists/*

RUN groupadd --gid 10001 appuser \
 && useradd --uid 10001 --gid appuser --create-home --shell /usr/sbin/nologin appuser

WORKDIR /home/appuser

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Berlin"

EXPOSE 8080

# owned by root, read-only for appuser; home dir stays writable (H2 file DB in ~/fredbetdb)
COPY target/fredbet.jar /opt/fredbet/fredbet.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "/opt/fredbet/fredbet.jar"]
