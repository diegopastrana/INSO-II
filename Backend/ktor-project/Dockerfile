############################################################
# ETAPA 1: BUILD (imagen oficial de Gradle con JDK 21)    #
############################################################
FROM gradle:8.14.0-jdk21 AS build
WORKDIR /app

# 1.1 Copiamos únicamente los archivos necesarios para que Gradle cachee dependencias:
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# 1.2 Damos permisos de ejecución a gradlew
RUN chmod +x gradlew

# 1.3 Descargamos dependencias (cache) sin compilar todo el proyecto aún
RUN ./gradlew --no-daemon dependencies || true

# 1.4 Copiamos el código fuente
COPY src ./src

# 1.5 Ejecutamos build y generamos el fat-jar llamado app.jar
RUN ./gradlew clean shadowJar -x test --no-daemon

############################################################
# ETAPA 2: RUNTIME (imagen ligera con JRE 21)              #
############################################################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 2.1 Copiamos el fat-jar generado en la etapa anterior
COPY --from=build /app/build/libs/app.jar app.jar

# 2.2 Exponemos el puerto 8080 (Ktor usa 8080 por defecto)
EXPOSE 8080

# 2.3 Forzamos a Ktor a vincularse a 0.0.0.0 (dentro de contenedor)
ENV JAVA_TOOL_OPTIONS="-Dio.ktor.deployment.host=0.0.0.0 -Dio.ktor.deployment.port=8080"

# 2.4 Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
