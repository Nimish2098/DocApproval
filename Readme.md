# Resume Analyzer API

A Spring Boot REST API that analyzes resumes against job descriptions using Apache Tika for document parsing and Apache OpenNLP for skill extraction. Built with stateless JWT authentication and role-based access control.
Deployed On Heroku Link
---

## Features

- **Resume Analysis** — Upload a resume (PDF/DOCX) and compare it against a job description to get a match score and list of missing skills
- **JWT Authentication** — Stateless auth with short-lived access tokens and long-lived refresh tokens
- **Role-Based Access** — `USER` and `ADMIN` roles with endpoint-level protection
- **Document Parsing** — Apache Tika auto-detects PDF vs DOCX and extracts raw text
- **NLP Skill Extraction** — Apache OpenNLP tags technical nouns from job description text
- **Profile Management** — Users can update their name, email, and password
- **Token Refresh & Logout** — Refresh tokens stored in DB, invalidated on logout
- **Metrics** — Prometheus-compatible metrics via Spring Boot Actuator

---

## Tech Stack

| Technology | Role |
|------------|------|
| Spring Boot 3.5 | Core framework, REST API |
| Spring Security | JWT-based stateless authentication |
| PostgreSQL | Primary database |
| Apache Tika | PDF and DOCX text extraction |
| Apache OpenNLP | NLP-based skill extraction from JD |
| JJWT | JWT token generation and validation |
| Hibernate / JPA | ORM and database mapping |
| Lombok | Boilerplate reduction |

---

## Project Structure

```
src/main/java/com/Project/DocApproval/
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ResumeController.java
│   ├── ResultController.java
│   └── JobDescriptionController.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ResumeService.java
│   ├── RefreshTokenService.java
│   └── MetricsService.java
├── security/
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   ├── SecurityConfig.java
│   └── UserDetailsServiceImpl.java
├── model/
│   ├── User.java
│   └── RefreshToken.java
├── repository/
│   ├── UserRepository.java
│   └── RefreshTokenRepository.java
├── dto/
│   ├── AuthResponse.java
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── RefreshTokenRequest.java
│   ├── UpdateProfileRequest.java
│   ├── ChangePasswordRequest.java
│   └── UserProfileResponse.java
└── enums/
    └── Role.java
```

---

## API Endpoints

### Auth — `/api/v1/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth/register` | Public | Register a new user |
| POST | `/api/v1/auth/login` | Public | Login and receive tokens |
| POST | `/api/v1/auth/refresh-token` | Public | Get new access token |
| POST | `/api/v1/auth/logout` | Bearer Token | Invalidate refresh token |

### User — `/user`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/user/profile` | Bearer Token | Get own profile |
| PUT | `/user/profile` | Bearer Token | Update name or email |
| PATCH | `/user/change-password` | Bearer Token | Change password |
| DELETE | `/user/profile` | Bearer Token | Delete own account |
| GET | `/user` | Admin only | Get all users |

### Job Descriptions — `/api/v1/jobs`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/jobs/create` | Bearer Token | Create a job description |

### Resumes — `/api/v1/resumes`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/resumes/upload/{jdId}` | Bearer Token | Upload resume for analysis |

### Results — `/api/v1/results`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/results/{trackingId}` | Bearer Token | Get analysis report |

---

## Getting Started

### Prerequisites

- Java 21
- Maven
- PostgreSQL

### 1. Clone the repository

```bash
git clone https://github.com/your-username/DocApproval.git
cd DocApproval
```

### 2. Create the database

```sql
CREATE DATABASE resume_db;
```

### 3. Configure environment variables

Set the following variables in your environment or IDE run configuration:

```
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
JWT_SECRET
JWT_EXPIRATION
JWT_REFRESH_EXPIRATION
```

### 4. Run the application

```bash
./mvnw spring-boot:run
```

App starts at `http://localhost:8080`

---

## Authentication Flow

```
Register or Login
  → receive accessToken + refreshToken

Use accessToken on every request:
  Authorization: Bearer <accessToken>

When accessToken expires:
  POST /api/v1/auth/refresh-token
  → receive new accessToken

On logout:
  POST /api/v1/auth/logout
  → refreshToken deleted from DB
```

---

## Swagger DOC
https://docapproval-api-fb4963d3a934.herokuapp.com/swagger-ui/index.html
---

## License

This project is built for educational purposes as a Personal project.
