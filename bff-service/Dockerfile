FROM openjdk:17-jdk-slim
WORKDIR /bff-service
COPY target/bff-service-0.0.1-SNAPSHOT.jar bff-service.jar
ENTRYPOINT ["java", "-jar", "bff-service.jar"]