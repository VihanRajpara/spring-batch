# Stage 1: Build the app
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /backend
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run the app
FROM openjdk:17.0.1-jdk-slim
WORKDIR /backend
COPY --from=build /backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
