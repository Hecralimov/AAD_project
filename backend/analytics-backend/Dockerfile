# Stage 1: Dependency Caching
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Only copy the files that define dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies. Docker will CACHE this layer. 
# It only re-runs if pom.xml changes.
RUN ./mvnw dependency:go-offline

# Stage 2: Code Compilation
# Now we copy the source. If only a Java file changed, 
# Docker starts from THIS line, skipping the download above.
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 3: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]