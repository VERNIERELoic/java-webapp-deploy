FROM openjdk:11.0.13-slim-buster as ban-backend
ADD target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "app/app.jar"]

