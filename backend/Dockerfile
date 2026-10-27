FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
COPY src ./src
COPY docker/HealthCheck.java /tmp/HealthCheck.java
RUN javac /tmp/HealthCheck.java -d /tmp/hc
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
COPY --from=build /tmp/hc /opt/hc
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
