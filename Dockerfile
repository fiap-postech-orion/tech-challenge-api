# Build stage
FROM maven:3.8.3-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ /app/src
RUN mvn clean install -DskipTests

# Final stage
FROM openjdk:17-alpine
COPY --from=build /app/target/techchallenge-eks.jar techchallenge-eks.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "techchallenge-eks.jar"]