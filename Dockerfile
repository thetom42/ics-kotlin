# Multi-stage build Dockerfile

# Stage 1: Build the application
FROM kotlin:latest AS build

# Set the working directory
WORKDIR /app

# Copy the source code
COPY . .

# Build the application
RUN ./gradlew build

# Stage 2: Run the application
FROM alpine:latest

# Set the working directory
WORKDIR /app

# Copy the built application from the first stage
COPY --from=build /app/build/libs/rollout-api.jar .

# Expose the necessary ports
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "rollout-api.jar"]
