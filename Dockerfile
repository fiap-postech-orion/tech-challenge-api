FROM openjdk:17
ADD target/techchallenge-eks.jar techchallenge-eks.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "techchallenge-eks.jar"]