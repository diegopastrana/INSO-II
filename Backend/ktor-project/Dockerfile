FROM gradle:8.14.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]