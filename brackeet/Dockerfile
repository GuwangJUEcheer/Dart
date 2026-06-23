# ===== Stage 1: Build =====
FROM gradle:8.13-jdk17 AS builder
WORKDIR /app

# Copy dependency definition first for layer caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Download dependencies (cached unless build.gradle changes)
RUN gradle dependencies --no-daemon 2>/dev/null || true

# Copy source and build
COPY src ./src
RUN gradle bootJar --no-daemon -x test

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S brackeet && adduser -S brackeet -G brackeet

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown brackeet:brackeet app.jar
USER brackeet

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
