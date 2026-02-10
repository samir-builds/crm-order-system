FROM eclipse-temurin:17-jdk
WORKDIR /app

# OpenTelemetry Java Agent əlavə edirik
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# JAR faylını əlavə edirik
COPY target/*.jar app.jar

# JVM agent ilə start edir
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
