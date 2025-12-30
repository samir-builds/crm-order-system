# CRM Order System

## Overview
CRM Order System is a simple backend application built with **Spring Boot**.  
It provides REST APIs to manage users, products, customers, and orders in a lightweight CRM environment.  
The project uses an in‑memory **H2 database** for quick setup and testing.

---

## Technologies
- Java 25 (LTS)
- Spring Boot (Web, JPA, Lombok)
- H2 Database (in‑memory)
- Maven
- Postman (for API testing)

---

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
