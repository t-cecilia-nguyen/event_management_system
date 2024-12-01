#--------------
# BUILD STAGE
#--------------

# Start from the Gradle 8 image file with JDK 22. This image provides both Gradle and JDK 22.
# We use this stage to build the application within the container.
FROM gradle:8-jdk22 AS builder

# Copy the application file from the host machine to the image filesystem.
# We use the --chown=gradle:gradle flag to ensure proper file permissions for the Gradle user
COPY --chown=gradle:gradle . /home/gradle/src

# Set the working directory inside the container to /home/gradle/src.
# All future commands will be executed from this directory.
WORKDIR /home/gradle/src

# Run the Gradle build inside the container, skipping the tests (-x test)
# This command compiles the code, resolves dependecies, and packages the application as a .jar file
# Note: The command applies to the image only, not to the host machine.
RUN gradle build -x test

#----------------
# PACKAGE STAGE
#----------------

# Start from a lightweight OpenJDK 22 Alpine image. This will be our runtime image.
# Alphine images are much smaller, which helps keep the final image size down.
FROM openjdk:22-jdk

# Create a directory inside the container where the application will be stored.
# This directory is where we will place the package .jar gile built in the previous stage.
RUN mkdir /app

# Copy the built .jar file from the build stage to the /app directory in the final image.
# We use the --from=builder instruction to reference the "builder" stage
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/approval-service.jar

# Set environment variables for the application. These variables can be assessed within the application.
# These values could be replaced by Docker Compose or passed during runtime with the 'docker run' command.
ENV MONGO_DB_USERNAME=admin \
    MONGO_DB_PWD=password

# Expose port 8086 to allow communication with the containerized application
# EXPOSE does not actually make the port accessible to the host machine; it's documentation for the image.
EXPOSE 8087

# The ENTRYPOINT instruction defines the command to run when the container starts.
# In this case, we are telling Docker to run the Java command with the packaged JAR file.
ENTRYPOINT ["java", "-jar", "/app/approval-service.jar"]