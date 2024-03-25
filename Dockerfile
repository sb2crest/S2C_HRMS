# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Install libfreetype to resolve the UnsatisfiedLinkError
RUN apk add --no-cache freetype ttf-freefont

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/management-0.0.1-SNAPSHOT.jar hrm_service.jar

# Copy necessary resources
COPY src/main/resources/images/seabed2crest.jpg /app/src/main/resources/images/seabed2crest.jpg
COPY src/main/resources/images/ceo_sign.jpg /app/src/main/resources/images/ceo_sign.jpg
COPY src/main/resources/images/icons8-rupee-96.png /app/src/main/resources/images/icons8-rupee-96.png
COPY src/main/resources/images/project_manager_sign.jpg /app/src/main/resources/images/project_manager_sign.jpg
COPY src/main/resources/images/seabed_transparent.jpeg /app/src/main/resources/images/seabed_transparent.jpeg

# Expose the port your application will listen on
EXPOSE 8081

# Define the command to run your application when the container starts
# Set the java.awt.headless system property to true
CMD ["java", "-Djava.awt.headless=true", "-jar", "hrm_service.jar"]
