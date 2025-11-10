# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
RUN mkdir -p /opt/app/dumps
VOLUME /opt/app/dumps

ENV JAVA_OPTS="\
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:InitialRAMPercentage=50.0 \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+AlwaysActAsServerClassMachine \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+UseContainerSupport \
  -Dfile.encoding=UTF-8 \
"

RUN addgroup -S app && adduser -S app -G app
USER app

COPY --from=build /app/target/aviation-api-wrapper-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
