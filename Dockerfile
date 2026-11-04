FROM eclipse-temurin:25-jdk AS build
WORKDIR /src
COPY . .
RUN chmod +x gradlew
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :server:bootJar --no-daemon -x test -Dorg.gradle.jvmargs=-Xmx1g \
 && cp "$(ls server/build/libs/*.jar | grep -v -- '-plain.jar')" /app.jar

FROM eclipse-temurin:25-jre
RUN groupadd --system --gid 10001 smarthome \
 && useradd --system --uid 10001 --gid 10001 --home-dir /app --shell /usr/sbin/nologin smarthome
WORKDIR /app
RUN mkdir -p /app/logs && chown -R 10001:10001 /app
COPY --from=build --chown=10001:10001 /app.jar /app/app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
