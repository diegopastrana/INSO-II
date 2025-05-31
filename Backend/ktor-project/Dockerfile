############################################################
# ETAPA 1: BUILD (usando imagen oficial de Gradle)         #
############################################################
FROM gradle:8.14.0-jdk21 AS build
WORKDIR /app

# 1.1 Copiamos sólo archivos de Gradle para cacheo de dependencias
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# Damos permisos de ejecución a gradlew
RUN chmod +x gradlew

# 1.2 Descargamos dependencias (cacheo) sin compilar aún todo
RUN ./gradlew --no-daemon dependencies || true

# 1.3 Copiamos el resto del código fuente
COPY src ./src

# 1.4 Ejecutamos el build generando el fat-jar (shadowJar)
RUN ./gradlew clean shadowJar -x test --no-daemon

############################################################
# ETAPA 2: RUNTIME (imagen ligera con JRE)                 #
############################################################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 2.1 Copiamos el JAR generado por ShadowJar
COPY --from=build /app/build/libs/app.jar app.jar

# 2.2 Exponemos el puerto 8080 (por defecto en Ktor)
EXPOSE 8080

# 2.3 Forzamos a Ktor a ligarse a 0.0.0.0 y puerto 8080
ENV JAVA_TOOL_OPTIONS="-Dio.ktor.deployment.host=0.0.0.0 -Dio.ktor.deployment.port=8080"

# 2.4 Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
