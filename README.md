# NCKH Backend

Base backend Spring Boot cho dự án NCKH, dựng theo style code của Lockly.

## Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Bean Validation
- Swagger/OpenAPI
- Lombok

## Project Structure

```text
src/main/java/com/example/base
├── common/response      # ApiResponse, ListResponse
├── config               # Security, OpenAPI, password encoder, env
├── constant             # ApiPath, message, common constants
├── controller           # REST controllers
├── domain
│   ├── dto              # request/response DTO
│   ├── entity           # JPA entities
│   └── mapper           # mapper components
├── exception            # global handler and custom exceptions
├── repository           # Spring Data JPA repositories
├── security             # JWT, filter, user details
└── service              # service interfaces and implementations
```

## Environment

Tạo file `.env` từ `.env.example`:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/nckh_be
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your-secret-key-your-secret-key-your-secret-key
JWT_ACCESS_EXPIRATION_TIME=3600000
JWT_REFRESH_EXPIRATION_TIME=259200000
ADMIN_PASSWORD=Admin123@
```

## Run

```powershell
.\mvnw.cmd spring-boot:run
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Git Workflow

```text
feature/* -> dev -> main
```

- `main`: code ổn định, dùng để demo/release.
- `dev`: nhánh phát triển chung.
- `feature/*`: nhánh cho từng chức năng.

Không code trực tiếp trên `main`.
# ncf-bedrock-be
