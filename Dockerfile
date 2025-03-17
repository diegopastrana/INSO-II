FROM gradle:7-jdk11 AS build
WORKDIR /app
COPY . .
RUN ./gradlew buildFatJar --no-daemon

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/your-app.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
