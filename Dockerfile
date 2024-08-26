# Use a stable and minimal OpenJDK image
FROM openjdk:17-slim-buster

# Install curl with minimal dependencies
RUN apt-get update && \
    apt-get install --no-install-recommends -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the application's jar file to the container
ARG JAR_FILE=target/java-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
