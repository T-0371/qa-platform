# Use Java 11 as base image
FROM eclipse-temurin:11-jdk-alpine

# Set working directory
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application using fixed port 8080
CMD java -Dserver.port=8080 -jar target/qa-platform.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE