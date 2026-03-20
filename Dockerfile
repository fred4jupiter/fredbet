ARG JRE_BASE_IMAGE=eclipse-temurin:25-jre-jammy

FROM ${JRE_BASE_IMAGE}

LABEL maintainer="Michael Staehler"

RUN apt-get update && apt-get upgrade -y && rm -rf /var/lib/apt/lists/*

RUN useradd -m appuser

WORKDIR /home/appuser

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Berlin"

EXPOSE 8080

COPY --chown=appuser:appuser target/fredbet.jar fredbet.jar

USER appuser

ENTRYPOINT ["java", "-jar", "fredbet.jar"]
