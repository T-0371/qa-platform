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

# Set environment variables with default values
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Set database environment variables with default values
ENV DB_HOST=localhost
ENV DB_PORT=3306
ENV DB_NAME=qa_platform
ENV DB_USERNAME=root
ENV DB_PASSWORD=123456

# Run the application
CMD java -Dserver.port=8080 -jar target/qa-platform.jar --spring.profiles.active=prod