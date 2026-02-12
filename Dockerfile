FROM eclipse-temurin:17-jdk
WORKDIR /app

RUN mkdir -p logs

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
