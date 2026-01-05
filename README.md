# CRM Order System

## Overview
CRM Order System is a backend application built with **Spring Boot**.  
It provides REST APIs to manage users, products, customers, and orders in a lightweight CRM environment.  
The project uses an in‑memory H2 database for quick setup and testing, and includes JWT Security, Role‑based authorization, and Audit logging for production‑ready features.


---

## Technologies
- Java 25 (LTS)
- Spring Boot (Web, JPA, Lombok)
- H2 Database (in‑memory)
- Maven
- Postman (for API testing)
- SLF4J + Logback (logging)

- Swagger/OpenAPI (planned)
- Docker (planned)
---

## Features
- User Management → CRUD operations with validation and DTO layer
- Product Management → CRUD operations with pagination, sorting, validation
- Customer Management → CRUD operations with pagination, sorting, and validation
- Order Management → CRUD operations linking Customer, Product, and User with pagination, sorting
- Role Management → ROLE_USER, ROLE_ADMIN seeded for RBAC
- JWT Security → authentication & authorization with role‑based access
- Audit Logging → CREATE, UPDATE, DELETE operations logged with old/new values
- Email Notifications → for order/customer events

PLANNED
- Swagger/OpenAPI
- Docker Deployment → planned with docker‑compose.yml
- Rate Limiting → planned with Bucket4j

## Entities
- **User** → system users
- **Product** → products available for sale
- **Customer** → customers placing orders
- **Order** → links Customer, Product, and User
- **Role** → optional, for security and authorization

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

## API Endpoints
### User
#### • GET /users → list all users
#### • POST /user → create a new user
#### • PUT /user → update user
#### • DELETE /user → delete user


## Product
#### • GET /products → list all products
#### • POST /product → create a new product
#### • PUT /product → update product
#### • DELETE /product → delete product

### Customer
#### • GET /customers → list all customers
#### • POST /customer → create a new customer
#### • PUT /customer → update customer
#### • DELETE /customer → delete customer

## Order
#### • GET /orders → list all orders
#### • POST /order → create a new order
#### • PUT /order → update order
#### • DELETE /order → delete order
