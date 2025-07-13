# ----- Build Stage -----
FROM maven:3.9.6-eclipse-temurin-17 as build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ----- Run Stage -----
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/personal-budget-manager-0.0.1-SNAPSHOT.jar app.jar

# Run the app
CMD ["java", "-jar", "app.jar"]
