############################################################
# 1) ETAPA DE BUILD: usamos la imagen oficial de Gradle    #
############################################################
FROM gradle:8.14.0-jdk21 AS build
WORKDIR /app

# 1.1 Copiamos archivos del proyecto
COPY . .
RUN chmod +x gradlew

# 1.2 Ejecutamos limpieza y empaquetado con ShadowJar (fat jar)
RUN ./gradlew clean shadowJar -x test --no-daemon

############################################################
# 2) ETAPA DE RUNTIME: JRE ligero para ejecutar el JAR    #
############################################################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 2.1 Copiamos el JAR que generó shadowJar
COPY --from=build /app/build/libs/app.jar app.jar

# 2.2 Exponemos el puerto donde Ktor escucha (8080 por defecto)
EXPOSE 8080

# 2.3 Arrancamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
