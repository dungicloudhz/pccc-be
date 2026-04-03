# Hướng dẫn Cài đặt và Chạy Backend PCCC

## Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven 3.8.0 hoặc cao hơn
- PostgreSQL 12 hoặc cao hơn
- Git

## Cài đặt Database PostgreSQL

### 1. Cài đặt PostgreSQL
- Tải từ: https://www.postgresql.org/download/
- Cài đặt với mật khẩu mặc định cho user `postgres` là `postgres`

### 2. Tạo Database
```bash
# Kết nối đến PostgreSQL
psql -U postgres

# Tạo database
CREATE DATABASE pccc_db;

# Kiểm tra
\l
```

## Build và Chạy Backend

### 1. Build dự án
```bash
mvn clean install
```

### 2. Chạy ứng dụng

#### Chế độ Development
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

#### Chế độ Production
```bash
mvn spring-boot:run
```

#### Hoặc sử dụng JAR file
```bash
java -jar target/pccc-backend-1.0.0.jar
```

### 3. Kiểm tra ứng dụng
Backend sẽ chạy trên: `http://localhost:8080`

## Cấu trúc Dự án

```
pccc-be/
├── src/main/java/com/cozyquoteforge/pccc/
│   ├── controller/          # REST Controllers
│   ├── service/             # Business Logic
│   ├── repository/          # Data Access Layer
│   ├── entity/              # JPA Entities
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Exception Handlers
│   └── PcccBackendApplication.java  # Main class
├── src/main/resources/
│   ├── application.yml      # Configuration mặc định
│   └── application-dev.yml  # Configuration cho development
├── pom.xml                  # Maven configuration
└── README.md
```

## API Endpoints

### Part Types (Danh mục hạng mục)
- `GET /api/v1/part-types` - Lấy danh sách
- `GET /api/v1/part-types/{id}` - Lấy chi tiết
- `POST /api/v1/part-types` - Tạo mới
- `PUT /api/v1/part-types/{id}` - Cập nhật
- `DELETE /api/v1/part-types/{id}` - Xóa

### Products (Vật tư, Thiết bị)
- `GET /api/v1/products` - Lấy danh sách
- `GET /api/v1/products/{id}` - Lấy chi tiết
- `POST /api/v1/products` - Tạo mới
- `PUT /api/v1/products/{id}` - Cập nhật
- `DELETE /api/v1/products/{id}` - Xóa

### Constructions (Công trình)
- `GET /api/v1/constructions` - Lấy danh sách
- `GET /api/v1/constructions/{id}` - Lấy chi tiết
- `POST /api/v1/constructions` - Tạo mới
- `PUT /api/v1/constructions/{id}` - Cập nhật
- `DELETE /api/v1/constructions/{id}` - Xóa

### Construction Details (Chi tiết Dự toán)
- `GET /api/v1/constructions/{id}/details` - Lấy chi tiết công trình (bao gồm workshops, sections, rows)
- `PUT /api/v1/constructions/{id}/details` - Lưu chi tiết công trình

## Cấu hình Elasticsearch (Optional)

Nếu cần thêm search functionality:
```xml
<dependency>
    <groupId>org.springframework.data</groupId>
    <artifactId>spring-data-elasticsearch</artifactId>
</dependency>
```

## Troubleshooting

### 1. Lỗi kết nối Database
```
Error connecting to database
```
**Giải pháp:**
- Kiểm tra PostgreSQL đang chạy: `psql -U postgres`
- Kiểm tra database `pccc_db` tồn tại
- Kiểm tra mật khẩu trong `application.yml`

### 2. Lỗi Port 8080 đã được sử dụng
```
Port 8080 already in use
```
**Giải pháp:**
- Đổi port trong `application.yml`: `server.port: 8081`
- Hoặc tìm process sử dụng port 8080 và kill nó

### 3. Lỗi Maven build
```
Maven build failure
```
**Giải pháp:**
```bash
# Clear Maven cache
mvn clean
# Rebuild
mvn install
```

## Kiểm tra API với cURL

### Tạo Part Type
```bash
curl -X POST http://localhost:8080/api/v1/part-types \
  -H "Content-Type: application/json" \
  -d '{"name": "Phần chữa cháy"}'
```

### Lấy danh sách Part Types
```bash
curl http://localhost:8080/api/v1/part-types
```

### Tạo Product
```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "id": "p1",
    "name": "Đầu phun sprinkler",
    "code": "P1",
    "unit": "Cái",
    "category": "Phần chữa cháy",
    "origin": "Trung Quốc",
    "materialUnitPrice": 120000,
    "laborUnitPrice": 30000,
    "lossPercent": 5
  }'
```

## Deployment

### Docker (Optional)
Tạo file `Dockerfile`:
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/pccc-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build Docker image:
```bash
docker build -t pccc-backend:1.0.0 .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/pccc_db pccc-backend:1.0.0
```

## Tài liệu Thêm

- Thiết kế Database: Xem `README.md`
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- JPA/Hibernate: https://hibernate.org/orm/
- PostgreSQL: https://www.postgresql.org/docs/
