FROM openjdk:11-jre-slim
COPY build/libs/your-app.jar /app/app.jar
CMD ["java", "-jar", "/app/app.jar"]
