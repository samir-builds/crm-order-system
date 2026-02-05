FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
COPY target/*.jar app.jar

ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-Dotel.exporter.otlp.endpoint=http://jaeger:4318", "-Dotel.service.name=crm-order-app", "-jar", "app.jar"]
