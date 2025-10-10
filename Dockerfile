ARG JRE_BASE_IMAGE=eclipse-temurin:21-jre-jammy

FROM ${JRE_BASE_IMAGE}

LABEL maintainer="Michael Staehler"

WORKDIR /

ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Europe/Berlin"

EXPOSE 8080

COPY target/fredbet.jar fredbet.jar

RUN useradd -m appuser
USER appuser

ENTRYPOINT ["java", "-jar", "fredbet.jar"]
CMD ["exec"]
