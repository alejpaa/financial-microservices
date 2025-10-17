FROM openjdk:17-jdk-slim
WORKDIR /client-service
COPY target/client-service-0.0.1-SNAPSHOT.jar client-service.jar
ENTRYPOINT ["java", "-jar", "client-service.jar"]