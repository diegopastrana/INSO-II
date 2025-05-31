FROM gradle:7-jdk11 AS build
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew build

EXPOSE 8080

CMD ["./gradlew", "run"]
