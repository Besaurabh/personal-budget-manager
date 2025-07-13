# --------- Build Stage ---------
FROM maven:3.9.6-eclipse-temurin-17 as build
WORKDIR /app

# Copy only pom.xml first to leverage Docker cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the rest of the project
COPY . .
RUN mvn clean package -DskipTests

# --------- Run Stage ---------
FROM eclipse-temurin:17-jdk-slim
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/personal-budget-manager-0.0.1-SNAPSHOT.jar app.jar

# Start the application
CMD ["java", "-jar", "app.jar"]
