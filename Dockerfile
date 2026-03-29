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

# Set environment variables explicitly
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
CMD java -Dserver.port=8080 -jar target/qa-platform.jar --spring.profiles.active=prod