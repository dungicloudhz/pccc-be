# Tài liệu dự án PCCC Backend

## 1. Giới thiệu

`pccc-backend` là backend REST API xây dựng bằng Java Spring Boot cho hệ thống quản lý báo giá và dự toán công trình PCCC (Phòng cháy chữa cháy).

Mục tiêu chính của dự án:
- Quản lý người dùng và phân quyền
- Quản lý danh mục hạng mục/loại vật tư
- Quản lý vật tư/thiết bị
- Quản lý công trình và dữ liệu dự toán chi tiết
- Xác thực bằng JWT
- Hoạt động với cơ sở dữ liệu PostgreSQL

## 2. Công nghệ sử dụng

- Java 17
- Spring Boot 3.2
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- JWT (jjwt)
- Lombok
- MapStruct
- Maven

## 3. Tính năng chính

### 3.1. Authentication & User

- Đăng ký tài khoản mới: `POST /api/v1/auth/register`
- Đăng nhập: `POST /api/v1/auth/login`
- JWT xác thực cho tất cả API khác
- Cập nhật hồ sơ người dùng hiện tại: `PUT /api/v1/users/me`
- Đổi mật khẩu người dùng hiện tại: `PUT /api/v1/users/me/password`
- Quản lý người dùng bởi admin:
  - Lấy danh sách users: `GET /api/v1/admin/users`
  - Lấy user chi tiết: `GET /api/v1/admin/users/{id}`
  - Tạo user mới: `POST /api/v1/admin/users`
  - Cập nhật role user: `PUT /api/v1/admin/users/{id}/role`
  - Reset mật khẩu: `PUT /api/v1/admin/users/{id}/reset-password`
  - Xóa user: `DELETE /api/v1/admin/users/{id}`

### 3.2. Phân quyền

- ROLE_USER: truy cập đọc các dữ liệu cơ bản
- ROLE_EDITOR: tạo/sửa/xóa dữ liệu vật tư, công trình, hạng mục, chi tiết công trình
- ROLE_ADMIN: tất cả quyền, quản lý người dùng cũng như toàn bộ dữ liệu

### 3.3. Quản lý danh mục hạng mục (Part Types)

- Lấy danh sách: `GET /api/v1/part-types`
- Lấy chi tiết: `GET /api/v1/part-types/{id}`
- Tạo mới: `POST /api/v1/part-types`
- Cập nhật: `PUT /api/v1/part-types/{id}`
- Xóa: `DELETE /api/v1/part-types/{id}`

### 3.4. Quản lý vật tư/thiết bị (Products)

- Lấy danh sách: `GET /api/v1/products`
- Lấy chi tiết: `GET /api/v1/products/{id}`
- Tạo mới: `POST /api/v1/products`
- Cập nhật: `PUT /api/v1/products/{id}`
- Xóa: `DELETE /api/v1/products/{id}`

### 3.5. Quản lý công trình (Constructions)

- Lấy danh sách: `GET /api/v1/constructions`
- Lấy chi tiết: `GET /api/v1/constructions/{id}`
- Tạo công trình: `POST /api/v1/constructions`
- Cập nhật công trình: `PUT /api/v1/constructions/{id}`
- Xóa công trình: `DELETE /api/v1/constructions/{id}`

### 3.6. Quản lý chi tiết dự toán công trình

- Lấy chi tiết dự toán: `GET /api/v1/constructions/{id}/details`
- Lưu/ cập nhật chi tiết dự toán: `PUT /api/v1/constructions/{id}/details`
- Tạo chi tiết dự toán mới: `POST /api/v1/constructions/create-details`

## 4. Cấu trúc dữ liệu và database

### 4.1. Database chính

Dự án sử dụng PostgreSQL và cấu hình mặc định trong `src/main/resources/application.yml`:

- URL: `jdbc:postgresql://localhost:5432/pccc_db`
- Username: `postgres`
- Password: `postgres`
- `spring.jpa.hibernate.ddl-auto: update`

### 4.2. Các bảng dữ liệu chính

#### Bảng `users`
- `id` (UUID)
- `username` (unique, not null)
- `email` (unique, not null)
- `password` (hashed bằng BCrypt)
- `first_name`
- `last_name`
- `role` (ROLE_USER, ROLE_EDITOR, ROLE_ADMIN)
- `created_at`
- `updated_at`

#### Bảng `part_types`
- `id` (UUID)
- `name`
- `created_at`
- `updated_at`

#### Bảng `products`
- `id` (Long)
- `name`
- `unit`
- `category`
- `origin`
- `code`
- `material_unit_price`
- `labor_unit_price`
- `loss_percent`
- `created_at`

#### Bảng `constructions`
- `id` (UUID)
- `name`
- `material_percent`
- `labor_percent`
- `created_at`
- `updated_at`

#### Bảng `construction_workshops`
- `id` (UUID)
- `construction_id` (FK -> `constructions.id`)
- `name`
- `order_id`
- `display_order`

#### Bảng `construction_sections`
- `id` (UUID)
- `construction_id` (FK -> `constructions.id`)
- `name`
- `order_id`
- `display_order`
- `rows` (TEXT, lưu JSON mảng dòng vật tư)

### 4.3. Lưu ý cấu trúc chi tiết công trình

- `Construction` chứa các `ConstructionWorkshop` và `ConstructionSection`
- `ConstructionSection.rows` lưu trữ JSON text gồm danh sách `RowDto`
- Mỗi dòng có các trường như:
  - `id` (productId dưới dạng chuỗi)
  - `code`, `note`
  - `totalCable`
  - `lossPercent`
  - `materialPrice`, `laborPrice`
  - `workshopValues` (giá trị đo theo từng xưởng)

## 5. Kiến trúc backend & luồng hoạt động

### 5.1. Luồng xác thực JWT

- Người dùng gửi `POST /api/v1/auth/login` với `username` và `password`
- Backend kiểm tra mật khẩu bằng BCrypt
- Nếu hợp lệ, phát JWT với thời hạn 24 giờ
- JWT được gửi trong header `Authorization: Bearer <token>`
- `AuthTokenFilter` đọc token, xác thực và thiết lập `SecurityContext`
- `WebSecurityConfig` cho phép truy cập tự do với `/api/v1/auth/**` và yêu cầu xác thực với tất cả request khác

### 5.2. Ủy quyền role-based

- `UserController` bị bảo vệ bởi `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`
- `PartTypeController`, `ProductController`, `ConstructionController`, `ConstructionDetailController` dùng `@PreAuthorize` để phân quyền:
  - Người dùng `ROLE_USER` chỉ được đọc dữ liệu
  - `ROLE_EDITOR` và `ROLE_ADMIN` được phép tạo/sửa/xóa

### 5.3. Khởi tạo admin mặc định

Component `AdminInitializer` tự tạo tài khoản admin khi không có user nào trong database:
- Username: `admin`
- Password: `admin123`
- Email: `admin@example.com`

Giá trị này có thể cấu hình trong `application.yml` bằng các property:
- `app.admin.username`
- `app.admin.password`
- `app.admin.email`

## 6. Cài đặt môi trường

### 6.1. Yêu cầu phần mềm

- JDK 17
- Maven 3.x
- PostgreSQL 15 hoặc Docker
- Docker Compose (nếu dùng Docker)

### 6.2. Thiết lập database

#### Option A: Chạy PostgreSQL cục bộ

1. Cài PostgreSQL
2. Tạo database `pccc_db`
3. Cập nhật `src/main/resources/application.yml` nếu cần

#### Option B: Dùng Docker Compose

Dự án cung cấp `docker-compose.yml` gồm:
- `postgres` (PostgreSQL 15)
- `pgadmin` (PGAdmin 4)

Chạy:
```powershell
cd e:\github_dungicloudhz\pccc-be
docker compose up -d
```

Kết nối PGAdmin: `http://localhost:5050`
- Email: `admin@example.com`
- Password: `admin`

### 6.3. Cấu hình ứng dụng

Các cấu hình chính nằm trong `src/main/resources/application.yml`:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto` (mặc định `update`)
- `app.jwt.secret`
- `app.jwt.expiration`
- `app.admin.username`
- `app.admin.password`
- `app.admin.email`

### 6.4. Chạy ứng dụng

#### Bước 1: Build
```powershell
mvn clean package
```

#### Bước 2: Khởi động
```powershell
java -jar target/pccc-backend-1.0.0.jar
```

Hoặc chạy trực tiếp bằng Maven:
```powershell
mvn spring-boot:run
```

Trang chạy mặc định:
- `http://localhost:8081`

### 6.5. Môi trường phát triển

File `src/main/resources/application-dev.yml` chứa cấu hình dev:
- `spring.jpa.show-sql: true`
- Logging debug cao hơn
- Cùng database `postgresql://localhost:5432/pccc_db`

## 7. Endpoints chính (chi tiết)

### 7.1. `/api/v1/auth`
- `POST /login`
  - Request: `username`, `password`
  - Response: `token`, `type`, `username`, `email`
- `POST /register`
  - Request: `username`, `email`, `password`, `firstName`, `lastName`
  - Response: AuthResponse

### 7.2. `/api/v1/users`
- `GET /me` - Lấy profile hiện tại
- `PUT /me` - Cập nhật `firstName`, `lastName`, `email`
- `PUT /me/password` - Đổi mật khẩu với `oldPassword`, `newPassword`

### 7.3. `/api/v1/admin/users`
- `GET /` - Lấy tất cả users
- `GET /{id}` - Lấy user theo ID
- `POST /?role=ROLE_USER|ROLE_ADMIN` - Tạo user mới
- `PUT /{id}/role` - Cập nhật role
- `PUT /{id}/reset-password` - Reset mật khẩu
- `DELETE /{id}` - Xóa user

### 7.4. `/api/v1/part-types`
- `GET /` - Lấy tất cả
- `GET /{id}` - Lấy theo ID
- `POST /` - Tạo mới
- `PUT /{id}` - Cập nhật
- `DELETE /{id}` - Xóa

### 7.5. `/api/v1/products`
- `GET /` - Lấy tất cả
- `GET /{id}` - Lấy theo ID
- `POST /` - Tạo mới
- `PUT /{id}` - Cập nhật
- `DELETE /{id}` - Xóa

### 7.6. `/api/v1/constructions`
- `GET /` - Lấy tất cả
- `GET /{id}` - Lấy theo ID
- `POST /` - Tạo mới
- `PUT /{id}` - Cập nhật
- `DELETE /{id}` - Xóa

### 7.7. `/api/v1/constructions/{id}/details`
- `GET` - Lấy chi tiết dự toán công trình
- `PUT` - Lưu/cập nhật chi tiết dự toán
- `POST /create-details` - Tạo chi tiết dự toán mới

## 8. Ghi chú quan trọng

- Ứng dụng dùng JWT secret và admin mặc định được cấu hình trong `application.yml`.
- `spring.jpa.hibernate.ddl-auto` hiện đang `update`; nếu triển khai production, nên chuyển sang `validate` hoặc quản lý schema qua migration tool.
- CORS hiện cho phép tất cả origin (`*`) để dễ phát triển.
- Các trường `createdAt`, `updatedAt` được gán tự động qua JPA.
- `ConstructionSection.rows` lưu JSON text, do đó API chi tiết công trình phải gửi/nhận dữ liệu cấu trúc JSON phức tạp.

## 9. Khuyến nghị cho báo giá

- Backend đã sẵn sàng cho hệ thống báo giá/dự toán công trình với các chức năng CRUD cho users, product, part type, construction, construction details.
- Cần bổ sung frontend hiển thị chi tiết `workshops`, `sections`, `rows` và tính toán tổng giá dựa trên giá vật tư, nhân công, tỷ lệ hao hụt.
- Nếu cần mở rộng:
  - Thêm migration tool (Flyway hoặc Liquibase)
  - Thêm API lọc/phan trang cho danh sách sản phẩm và công trình
  - Thêm chức năng export báo giá (Excel/PDF)
  - Thêm quyền chi tiết hơn cho các role

---

Tài liệu này đã tóm tắt toàn bộ chức năng hiện tại, cấu hình môi trường, database và các endpoint cần thiết để làm hồ sơ báo giá hoặc chuyển giao triển khai.
