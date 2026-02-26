FROM eclipse-temurin:17-jdk

WORKDIR /app

# Log folder (optional)
RUN mkdir -p logs

# Copy application JAR
COPY target/*.jar app.jar

# Copy OpenTelemetry Java Agent
COPY opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# OpenTelemetry environment variables
ENV OTEL_SERVICE_NAME=crm-order-system
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4317
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none

# Enable OpenTelemetry agent
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "app.jar"]
