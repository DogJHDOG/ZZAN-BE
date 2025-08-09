# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Download dependencies (this will cache them)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application
RUN gradle clean bootJar -x test --no-daemon

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]