# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Specify a build argument for the JAR file name (default to fdapn-0.0.1-SNAPSHOT.jar)
ARG JAR_FILE=target/management-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} hrm_service.jar

# Expose the port the app runs on
EXPOSE 8081

# Run the application with a delay to allow MySQL to initialize
CMD ["sh", "-c", "sleep 30 && java -jar hrm_service.jar"]