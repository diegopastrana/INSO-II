############################################################
# 1) ETAPA DE BUILD: compilamos con Gradle (sin shadow)  ##
############################################################
FROM gradle:8.14.0-jdk21 AS build
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

############################################################
# 2) ETAPA DE RUNTIME: ejecutamos el JAR desde classpath  ##
############################################################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 2.1 Copiamos todos los JARs que quedaron en build/libs
COPY --from=build /app/build/libs /app/libs

# 2.2 Exponemos el puerto 8080
EXPOSE 8080

# 2.3 Arrancamos con “java -cp” incluyendo TODO /app/libs/*.jar,
#     asumiendo que tu mainClass es io.ktor.server.netty.EngineMain
ENTRYPOINT ["sh", "-c", "java -cp 'libs/*' io.ktor.server.netty.EngineMain"]
