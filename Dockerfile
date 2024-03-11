# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Specify a build argument for the JAR file name (default to hrm-0.0.1-SNAPSHOT.jar)
ARG JAR_FILE=target/management-0.0.1-SNAPSHOT.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} hrm_service.jar

# Expose the port the app runs on
EXPOSE 8081

# Install Dockerize (download the latest release from https://github.com/jwilder/dockerize)
ENV DOCKERIZE_VERSION v0.6.1
RUN apt-get update && apt-get install -y wget \
    && wget https://github.com/jwilder/dockerize/releases/download/${DOCKERIZE_VERSION}/dockerize-linux-amd64-${DOCKERIZE_VERSION}.tar.gz \
    && tar -C /usr/local/bin -xzvf dockerize-linux-amd64-${DOCKERIZE_VERSION}.tar.gz \
    && rm dockerize-linux-amd64-${DOCKERIZE_VERSION}.tar.gz

# Run the application with Dockerize to wait for MySQL to initialize
CMD ["sh", "-c", "dockerize -wait tcp://mdb:3306 -timeout 60s java -jar hrm_service.jar"]
