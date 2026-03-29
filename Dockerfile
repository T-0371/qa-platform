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
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application using shell to parse environment variables
CMD sh -c "java -Dserver.port=$PORT -jar target/qa-platform.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE"