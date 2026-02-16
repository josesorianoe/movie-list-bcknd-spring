# ===== BUILD (compila el jar) =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos primero el pom para aprovechar cache de dependencias
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copiamos el código fuente y compilamos
COPY src ./src
RUN mvn -q -DskipTests package

# ===== RUN (ejecuta el jar) =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado en la fase build
COPY --from=build /app/target/*.jar app.jar

# Render usa PORT; Spring lo leerá desde application.properties
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]