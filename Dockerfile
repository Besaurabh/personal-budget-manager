# Stage 1: Build the application using Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the project (skipping tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Run the application using OpenJDK
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/personal-budget-manager-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional, Render handles this internally)
EXPOSE 8080

# Command to run the JAR
CMD ["java", "-jar", "app.jar"]
