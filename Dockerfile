# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

# ---------- Run Stage ----------
FROM openjdk:17-jdk-slim
WORKDIR /app

# 👇 Use the correct JAR file name (copy this exactly as shown in logs)
COPY --from=build /app/target/Personal_Budget_Manager-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
