# --- Сборка ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Сначала только pom.xml, чтобы зависимости кэшировались отдельным слоем
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# --- Запуск ---
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

VOLUME /app/upload
EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
