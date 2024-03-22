# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/management-0.0.1-SNAPSHOT.jar hrm_service.jar

# Expose the port your application will listen on
EXPOSE 8081

# Define the command to run your application when the container starts
CMD ["java", "-jar", "hrm_service.jar"]
