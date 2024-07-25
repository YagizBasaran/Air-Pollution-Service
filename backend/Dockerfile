# Use a Maven image to build the application
FROM maven:3.8.5-openjdk-17-slim as build-stage

# Set the working directory
WORKDIR /app

# Copy the pom.xml file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the application code and build the application
COPY src ./src
RUN mvn clean package

# Use a JDK image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built jar file
COPY --from=build-stage /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
