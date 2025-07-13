# Use OpenJDK as base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file into the container
COPY target/personal-budget-manager-0.0.1-SNAPSHOT.jar app.jar

# Set environment variable
ENV PORT=8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
