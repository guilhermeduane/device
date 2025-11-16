# Device Management API

A REST API for managing devices as part of a backend challenge. This project is fully containerized using Docker.

## Tech Stack

* Java 21
* Spring Boot 3
* Spring Data JPA
* MySQL 8
* Docker / Docker Compose
* MapStruct (for DTOs)
* Springdoc OpenAPI (Swagger)
* JUnit 5 / Mockito

---

## How to Run (Docker)

Follow these steps to build and run the application and the database.

### 1. Prerequisites

* Docker
* Docker Compose

### 2. Environment File

**`.env` content:**
```env
MYSQL_ROOT_PASS=rootpassdb
MYSQL_DB_NAME=1globaldb
MYSQL_USER_NAME=userdb
MYSQL_USER_PASS=passdb
```

### 3. Run the Containers

Execute the following command in your terminal from the project's root directory:

```
docker-compose up --build
```
The Spring application will be available at http://localhost:8080 and the database at localhost:3306.

## Tests

The project includes unit tests for the Service and Controller layers, covering all business logic.

## Test Locations

* **Service Tests:**
  `src/test/java/com/oneglobal/chalenge/service/DeviceServiceTest.java`

* **Controller Tests:**
  `src/test/java/com/oneglobal/chalenge/controller/DeviceControllerTest.java`

### Running Tests

Tests are executed **automatically** by Maven inside the Docker container every time you run the `docker-compose up --build` command.

If the build fails, it indicates that one or more tests have failed.

