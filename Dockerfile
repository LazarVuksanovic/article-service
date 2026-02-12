FROM amazoncorretto:21-alpine-jdk AS builder
WORKDIR /app
COPY .. .
RUN ./mvnw clean package -DskipTests

# Run stage with JRE only
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]