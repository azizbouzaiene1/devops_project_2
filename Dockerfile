FROM openjdk:11-jre-slim
EXPOSE 8082
WORKDIR /app
COPY target/eventsProject-1.0.1-SNAPSHOT.jar /app/eventsProject.jar
CMD ["java", "-jar", "eventsProject.jar"]