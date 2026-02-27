# Automated Approval System

A robust and automated document approval system built with Spring Boot, designed to streamline workflow processes.

## Features
- **User Management**: Create, update, and manage user profiles.
- **Request Workflow**: Submit and track document approval requests.
- **Decision Tracking**: Record and manage approval decisions.
- **Configurable Request Types**: Define multiple types of requests for different workflows.
- **Automated API Documentation**: Integrated Swagger UI for easy API exploration.

## Technologies Used
- **Java:** 21
- **Framework:** Spring Boot 3.5.5
- **Database:** MySQL
- **Tools:** Maven, Lombok
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)

## Prerequisites
Before running the application, ensure you have the following installed:
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL Server](https://dev.mysql.com/downloads/mysql/)

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd DocApproval
    ```

2.  **Database Configuration:**
    - Create a MySQL database named `document_db`.
    - The application is configured to use the following default credentials in `src/main/resources/application.yml`:
        - **Username:** `root`
        - **Password:** `Ni@12345`
    - *Note: Update these credentials in `application.yml` or via environment variables to match your local setup.*

3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```

## Running the Application

To run the application locally:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## API Documentation

The application uses Swagger UI for API documentation. Once the application is running, you can access it at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Project Structure

```
src
+---main
|   +---java
|   |   \---com
|   |       \---Project
|   |           \---DocApproval
|   |               +---controller
|   |               +---dto
|   |               +---enums
|   |               +---exceptions
|   |               +---model
|   |               +---repository
|   |               \---service
|   \---resources
```

## API Reference

### User Management (`/user`)
- `POST /user` - Create a new user.
- `POST /user/{id}` - Update an existing user.
- `PATCH /user/{id}` - Partially update a user.
- `DELETE /user/delete/{id}` - Delete a user.

### Request Management (`/Request`)
- `POST /Request` - Create a new request.
- `POST /Request/{id}` - Update a request.
- `PATCH /Request/{id}` - Partially update a request.
- `DELETE /Request/{id}` - Delete a request.

### Decision Management (`/decision`)
- `POST /decision` - Add a decision.
- `POST /decision/{id}` - Update a decision.
- `PATCH /decision/{id}` - Partially update a decision.
- `DELETE /decision/{id}` - Delete a decision.

### Request Type Management (`/request_type`)
- `POST /request_type` - Add a request type.
- `POST /request_type/{id}` - Update a request type.
- `PATCH /request_type/{id}` - Partially update a request type.
- `DELETE /request_type/{id}` - Delete a request type.

## Contributing

Contributions are welcome! Please follow these steps:
1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

# 🚩 Brutally Honest ATS - AI Resume Critic

> [!IMPORTANT]
> **🚧 Project Status: Under Development**
> This project is currently in the active development phase.
> **Current Progress:** Phase 1 (Core Backend & State Machine) is Complete. Phase 2 (AI Critique Engine) is in progress.
