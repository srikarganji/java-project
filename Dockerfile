# Use an appropriate base image with JDK
FROM openjdk:11-jdk-slim

# Set working directory
WORKDIR /usr/src/app

# Copy the Java file into the container
COPY app.java .

# Compile the Java file
RUN javac app.java

# Run the compiled Java class
CMD ["java", "app"]
