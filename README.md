# 📊 CRM Order System

## Overview
CRM Order System is a backend application built with Spring Boot. It provides REST APIs to manage users, products, customers, and orders in a lightweight CRM environment. The project uses an in‑memory H2 database for quick setup and testing, and includes JWT Security, Role‑based authorization, Audit logging, request throttling, and full monitoring stack (Prometheus + Grafana + Alertmanager) for production‑ready features.


---

## Technologies
- Java 25 (LTS)
- Spring Boot (Web, JPA, Lombok)
- H2 Database (in‑memory)
- Maven
- Postman (for API testing)
- SLF4J + Logback (logging)
- Swagger/OpenAPI
- Docker + Docker Compose
- Prometheus (metrics collection)
- Alertmanager (alerting via Gmail)
- Grafana (dashboards & visualization)
- GitHub Actions (CI/CD pipeline)

  
---

## 🚀 Features
- 👤 **User Management** → CRUD operations with validation and DTO layer  
- 📦 **Product Management** → CRUD operations with pagination, sorting, validation  
- 🧑‍🤝‍🧑 **Customer Management** → CRUD operations with pagination, sorting, and validation  
- 📝 **Order Management** → CRUD operations linking Customer, Product, and User with pagination, sorting  
- 🔑 **Role Management** → ROLE_USER, ROLE_ADMIN seeded for RBAC  
- 🔒 **JWT Security** → authentication & authorization with role‑based access  
- 🕵️ **Audit Logging** → CREATE, UPDATE, DELETE operations logged with old/new values  
- 📧 **Email Notifications** → for order/customer events  
- 📖 **Swagger/OpenAPI** → interactive API documentation and testing  
- ⚡ Request Throttling → in‑memory throttling to prevent abuse
- 📊 Monitoring → Prometheus metrics, Grafana dashboards (CPU, Memory, Latency, Error Rate, HTTP Requests)
- 🚨 Alerting → Prometheus rules + Alertmanager Gmail integration
- 🐳 **Docker Deployment** → planned with docker‑compose.yml
- ⚙️ CI/CD Pipeline → GitHub Actions build, test, Docker push, and auto‑deploy to VPS


## 📂 Project Structure

```plaintext
crm-order-system/
├── annotation/       # 🏷️ Custom annotations (validation, logging, etc.)
├── aop/              # 🎯 Aspect Oriented Programming (cross-cutting concerns)
├── config/           # 🔧 Application & security configuration
├── controller/       # 🎮 REST API controllers
├── dto/              # 📦 Data Transfer Objects (request/response models)
├── enums/            # 🔤 Enum definitions (statuses, roles, etc.)
├── exception/        # ⚠️ Custom exceptions & global handlers
├── model/            # 🗂️ Entity classes (JPA models)
├── repository/       # 💾 Spring Data JPA repositories
├── security/         # 🔒 JWT filters, authentication & authorization
├── service/          # ⚙️ Business logic & workflows
├── docker-compose.yml # 🐳 App + Prometheus + Alertmanager + Grafana
├── prometheus.yml     # 📊 Prometheus config
├── rules.yml          # 🚨 Alerting rules
├── alertmanager.yml   # 📧 Alertmanager config
└── .github/workflows/ci-cd.yml # ⚙️ CI/CD pipeline
```

## 🔄 Event Flow

👤 User Authentication → 📥 Request Handling → ⚙️ Business Logic → 🕵️ Audit Logging
📧 Event Notifications → 📤 Response → 📖 API Documentation
📊 Metrics → Prometheus → Grafana Dashboards → 🚨 Alertmanager (Gmail)

##

1. 👤 **Client** → sends request with JWT token  
2. 🔒 **Security Layer** → validates token & applies role‑based access  
3. 🎮 **Controller** → receives request, validates via DTO  
4. ⚙️ **Service** → executes business logic  
5. 💾 **Repository** → performs DB operations (CRUD)  
6. 🕵️ **AOP + Audit** → logs operations with old/new values  
7. 📧 **Notification** → sends email if event occurs  
8. 📤 **Response** → returns DTO result to client
9. 📊 Metrics → Prometheus → Grafana Dashboards → 🚨 Alertmanager (Gmail)


---

## Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/samir-builds/crm-order-system.git

2. Open the project in your IDE (IntelliJ IDEA recommended).
3. Run the application:
   ```bash
    mvn spring-boot:run

4. Access the H2 console in your browser:
    ```bash
    http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:crmdb
Username: sa
Password: (leave empty)

5. Start monitoring stack:
    ```bash
    docker compose up -d

- 📊 **Prometheus** → [http://localhost:9090](http://localhost:9090)
- 🚨 **Alertmanager** → [http://localhost:9093](http://localhost:9093)
- 📈 **Grafana** → [http://localhost:3000](http://localhost:3000) *(default login: admin/admin)*

