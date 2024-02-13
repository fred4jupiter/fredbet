# Step : Test and package
FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /build
COPY pom.xml .

COPY src/ /build/src/
COPY .git /build/.git/
RUN mvn -B -DskipTests package

# Step : Package image
FROM eclipse-temurin:21-jre-jammy
LABEL maintainer="Michael Staehler"

VOLUME /tmp

ENV JAVA_OPTS="-Duser.timezone=Europe/Berlin"

EXPOSE 8080

COPY --from=builder /build/target/fredbet.jar fredbet.jar
CMD exec java $JAVA_OPTS -jar fredbet.jar