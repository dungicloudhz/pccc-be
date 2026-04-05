# Tài liệu thiết kế API - Cozy Quote Forge (Backend)

Tài liệu này mô tả các API và cấu trúc cơ sở dữ liệu (Database Schema) để phục vụ cho việc xây dựng Backend bằng Java Spring Boot và cơ sở dữ liệu PostgreSQL dựa trên các tính năng hiện tại của Frontend.

## 1. Tổng quan các tính năng
Hệ thống hiện tại (Frontend) bao gồm các chức năng chính xoay quanh việc lập dự toán, quản lý vật tư và quản lý công trình báo giá, bao gồm các Module cụ thể như sau:
1. **Quản lý Loại hạng mục / Loại vật tư (Part Types / Categories)**: Lưu trữ các nhóm danh mục như "Phần chữa cháy", "Phần báo cháy", "Vật liệu xây dựng",...
2. **Quản lý Vật tư, Thiết bị (Products)**: Thêm, sửa, xóa, danh sách các vật tư với các thuộc tính như tên, mã, hãng, giá vật tư, giá nhân công, tỉ lệ hao hụt,...
3. **Quản lý Danh sách Công trình (Constructions)**: Tạo mới, cập nhật, xóa, và liệt kê các dự án/công trình cần lập dự toán.
4. **Chi tiết Dự toán Công trình (Construction Detail)**: Lưu trữ chi tiết một bảng khối lượng vật tư công trình phức tạp, với cấu trúc bao gồm các hạng mục (sections), các xưởng (workshops - đại diện cho các khu vực/vị trí đo đếm), và các dòng vật tư chi tiết bên trong từng hạng mục.

---

## 2. Thông tin cấu trúc Database (PostgreSQL)

Dưới đây là thiết kế các bảng dữ liệu cho PostgreSQL để lưu trữ trạng thái của ứng dụng.

### Bảng `part_types` (Danh mục Hạng mục)
* `id` (UUID, Primary Key)
* `name` (VARCHAR, Not Null): Tên danh mục (Ví dụ: "Phần chữa cháy").
* `created_at` (TIMESTAMP)
* `updated_at` (TIMESTAMP)

### Bảng `products` (Vật tư, thiết bị)
* `id` (VARCHAR hoặc UUID, Primary Key)
* `name` (VARCHAR, Not Null): Tên vật tư
* `unit` (VARCHAR): Đơn vị tính (Cái, M, Lít...)
* `category_id` (UUID, Foreign Key hoặc VARCHAR nullable) / Hoặc sử dụng VARCHAR `category` theo thiết kế cũ.
* `origin` (VARCHAR): Xuất xứ
* `code` (VARCHAR): Mã vật tư
* `material_unit_price` (NUMERIC): Đơn giá vật tư gốc
* `labor_unit_price` (NUMERIC): Đơn giá nhân công gốc
* `loss_percent` (NUMERIC): % Hao hụt mặc định
* `created_at` (TIMESTAMP)

### Bảng `constructions` (Công trình)
* `id` (UUID, Primary Key)
* `name` (VARCHAR, Not Null): Tên dự án / nhà thuốc
* `material_percent` (NUMERIC): % điều chỉnh giá vật tư cho công trình này (Mặc định 100)
* `labor_percent` (NUMERIC): % điều chỉnh giá nhân công cho công trình này (Mặc định 100)
* `created_at` (TIMESTAMP)
* `updated_at` (TIMESTAMP)

### Bảng `construction_workshops` (Các xưởng / khu vực trong công trình)
* `id` (UUID, Primary Key)
* `construction_id` (UUID, Foreign Key -> `constructions.id`)
* `name` (VARCHAR): Tên xưởng (Ví dụ: "Xưởng 1")
* `display_order` (INT): Thứ tự hiển thị

### Bảng `construction_sections` (Các hạng mục trong bảng giá của công trình)
* `id` (UUID, Primary Key)
* `construction_id` (UUID, Foreign Key -> `constructions.id`)
* `name` (VARCHAR): Tên hạng mục (Ví dụ: "Phần chữa cháy")
* `display_order` (INT): Thứ tự hiển thị

### Bảng `construction_rows` (Chi tiết dòng vật tư trong hạng mục)
* `id` (UUID, Primary Key)
* `section_id` (UUID, Foreign Key -> `construction_sections.id`)
* `product_id` (VARCHAR/UUID, Nullable, Foreign Key -> `products.id`): ID Vật tư (hoặc có thể bỏ trống nếu user nhập tự do).
* `code` (VARCHAR): Mã (có thể overwrite từ product)
* `note` (TEXT): Ghi chú
* `total_cable` (NUMERIC, Nullable): Tổng dây (thông tin bổ sung)
* `loss_percent` (NUMERIC, Nullable): % hao hụt áp dụng cho dòng này
* `material_price` (NUMERIC, Nullable): Đơn giá vật tư áp dụng
* `labor_price` (NUMERIC, Nullable): Đơn giá nhân công áp dụng
* `display_order` (INT): Thứ tự dòng

### Bảng `construction_row_workshop_values` (Giá trị đo khối lượng cho từng xưởng trên mỗi dòng)
* `row_id` (UUID, Foreign Key -> `construction_rows.id`)
* `workshop_id` (UUID, Foreign Key -> `construction_workshops.id`)
* `value` (NUMERIC, Nullable): Số lượng khối lượng ghi nhận
* Primary Key chập: `(row_id, workshop_id)`

---

## 3. Hệ thống Authentication và Security

Hệ thống sử dụng JWT (JSON Web Token) để xác thực người dùng, kết hợp với Spring Security để bảo vệ các API endpoints.

### 3.1. Tổng quan

- **Authentication**: Sử dụng username/password với mã hóa BCrypt
- **Authorization**: JWT Bearer Token với thời hạn 24 giờ
- **Security Framework**: Spring Security với stateless session
- **CORS**: Cho phép tất cả origins (có thể cấu hình lại cho production)

### 3.2. Bảng Users

* `id` (UUID, Primary Key)
* `username` (VARCHAR, Unique, Not Null): Tên đăng nhập
* `email` (VARCHAR, Unique, Not Null): Email
* `password` (VARCHAR, Not Null): Mật khẩu mã hóa BCrypt
* `first_name` (VARCHAR): Tên
* `last_name` (VARCHAR): Họ
* `role` (VARCHAR, Not Null): Vai trò (ROLE_USER, ROLE_ADMIN)
* `created_at` (TIMESTAMP)
* `updated_at` (TIMESTAMP)

### 3.2.1. Roles và Quyền

- **ROLE_USER**: Người dùng thông thường, có thể truy cập các API cơ bản
- **ROLE_ADMIN**: Quản trị viên, có thể quản lý users và tất cả quyền của USER

### 3.2.2. Admin mặc định

Khi khởi động ứng dụng lần đầu, hệ thống sẽ tự động tạo tài khoản admin mặc định:
- Username: `admin` (có thể cấu hình trong application.yml)
- Password: `admin123` (có thể cấu hình trong application.yml)
- Email: `admin@example.com` (có thể cấu hình trong application.yml)

### 3.3. API Endpoints Authentication

#### `POST /api/v1/auth/register`
* Đăng ký tài khoản mới
* **Request**:
  ```json
  {
    "username": "nguyenvana",
    "email": "nguyenvana@example.com",
    "password": "password123",
    "firstName": "Văn",
    "lastName": "Nguyễn"
  }
  ```
* **Response `200 OK`**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "username": "nguyenvana",
    "email": "nguyenvana@example.com"
  }
  ```

### 3.3. API Quản lý User (Admin only)

#### `GET /api/v1/admin/users`
* Lấy danh sách tất cả users
* **Authorization**: Bearer token với ROLE_ADMIN
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "uuid",
      "username": "nguyenvana",
      "email": "nguyenvana@example.com",
      "firstName": "Văn",
      "lastName": "Nguyễn",
      "role": "ROLE_USER",
      "createdAt": "2023-10-01T12:00:00Z",
      "updatedAt": "2023-10-01T12:00:00Z"
    }
  ]
  ```

#### `GET /api/v1/admin/users/{id}`
* Lấy thông tin chi tiết user theo ID
* **Authorization**: Bearer token với ROLE_ADMIN

#### `POST /api/v1/admin/users`
* Tạo user mới bởi admin
* **Authorization**: Bearer token với ROLE_ADMIN
* **Query Parameter**: `role` (ROLE_USER hoặc ROLE_ADMIN, mặc định ROLE_USER)
* **Request**: Giống register request
* **Response `201 Created`**: Thông tin user vừa tạo

#### `PUT /api/v1/admin/users/{id}/role`
* Cập nhật role của user
* **Authorization**: Bearer token với ROLE_ADMIN
* **Request**:
  ```json
  {
    "role": "ROLE_ADMIN"
  }
  ```
* **Response `200 OK`**: Thông tin user đã cập nhật

#### `DELETE /api/v1/admin/users/{id}`
* Xóa user
* **Authorization**: Bearer token với ROLE_ADMIN
* **Response `204 No Content`**

**Lưu ý**: Admin không thể tự xóa tài khoản của mình hoặc hạ cấp role của chính mình.

#### `POST /api/v1/auth/login`
* Đăng nhập
* **Request**:
  ```json
  {
    "username": "nguyenvana",
    "password": "password123"
  }
  ```
* **Response `200 OK`**: Trả về JWT token (giống register)

### 3.4. Sử dụng API với Authentication

Tất cả API endpoints (trừ `/api/v1/auth/**`) đều yêu cầu authentication. Thêm header:

```
Authorization: Bearer <jwt_token>
```

**Ví dụ sử dụng curl**:
```bash
# 1. Đăng ký user thường
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user","email":"user@example.com","password":"123456","firstName":"Test","lastName":"User"}'

# 2. Đăng nhập với admin mặc định
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 3. Lấy danh sách users (cần token admin)
curl -X GET http://localhost:8081/api/v1/admin/users \
  -H "Authorization: Bearer <admin_token>"

# 4. Tạo user mới với role ADMIN
curl -X POST "http://localhost:8081/api/v1/admin/users?role=ROLE_ADMIN" \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"username":"newadmin","email":"newadmin@example.com","password":"123456","firstName":"New","lastName":"Admin"}'

# 5. Cập nhật role của user
curl -X PUT http://localhost:8081/api/v1/admin/users/{user_id}/role \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{"role":"ROLE_ADMIN"}'

# 6. Xóa user
curl -X DELETE http://localhost:8081/api/v1/admin/users/{user_id} \
  -H "Authorization: Bearer <admin_token>"
```

### 3.5. Cấu hình Security

#### application.yml
```yaml
app:
  jwt:
    secret: your-256-bit-secret-key-here
    expiration: 86400000  # 24 giờ tính bằng ms
  admin:
    username: admin
    password: admin123
    email: admin@example.com

# CORS Configuration
cors:
  allow-credentials: false
  max-age: 86400
```

#### JWT Secret Key
- Sử dụng key 256-bit (32 bytes) hoặc dài hơn
- Trong production: Sử dụng environment variable hoặc secret management

### 3.6. Bảo mật API

- **Endpoints công khai**: `/api/v1/auth/**`
- **Endpoints USER**: Tất cả endpoints khác yêu cầu `ROLE_USER` hoặc `ROLE_ADMIN`
- **Endpoints ADMIN**: `/api/v1/admin/**` chỉ dành cho `ROLE_ADMIN`

Tất cả endpoints được bảo vệ với `@PreAuthorize("hasAuthority('ROLE_USER')")` hoặc `@PreAuthorize("hasAuthority('ROLE_ADMIN')")`.

### 3.7. Xử lý Lỗi Authentication

- **401 Unauthorized**: Token không hợp lệ hoặc hết hạn
- **403 Forbidden**: Không có quyền truy cập (cần ROLE_ADMIN cho admin endpoints)
- **400 Bad Request**: Dữ liệu đăng ký không hợp lệ (username/email đã tồn tại)
- **404 Not Found**: User không tồn tại
- **409 Conflict**: Admin cố gắng tự hạ cấp role hoặc xóa tài khoản của mình

### 3.8. Best Practices

1. **Password Policy**: 
   - Tối thiểu 8 ký tự
   - Bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt

2. **JWT Expiration**: 
   - Token ngắn hạn (24h) cho security
   - Implement refresh token nếu cần

3. **Role Management**:
   - Cẩn thận khi cấp ROLE_ADMIN
   - Admin không thể tự hạ cấp hoặc xóa tài khoản của mình
   - Log tất cả thay đổi role và xóa user

4. **HTTPS**: Luôn sử dụng HTTPS trong production

5. **CORS**: Cấu hình allowed origins cụ thể thay vì "*"

6. **Rate Limiting**: Thêm rate limiting cho auth endpoints

7. **Logging**: Log các hoạt động authentication quan trọng

---

## 4. Danh sách API (Request và Response)

### 4.1. Danh mục hạng mục (Part Types)

#### `GET /api/v1/part-types`
* Lấy danh sách các loại hạng mục.
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "uuid",
      "name": "Phần chữa cháy",
      "createdAt": "2023-10-01T12:00:00Z",
      "updatedAt": "2023-10-01T12:00:00Z"
    }
  ]
  ```

#### `POST /api/v1/part-types`
* Tạo mới hạng mục.
* **Request**:
  ```json
  { "name": "Vật liệu điện" }
  ```
* **Response `201 Created`**: Object hạng mục vừa tạo kèm ID.

#### `PUT /api/v1/part-types/{id}`
* Cập nhật hạng mục.
* **Request**:
  ```json
  { "name": "Vật liệu điện (Cập nhật)" }
  ```
* **Response `200 OK`**.

#### `DELETE /api/v1/part-types/{id}`
* Xóa hạng mục (Response `204 No Content`).

---

### 4.2. Quản lý Vật tư, Thiết bị (Products)

#### `GET /api/v1/products`
* Lấy danh sách vật tư.
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "p1",
      "name": "Đầu phun sprinkler",
      "category": "Phần chữa cháy",
      "origin": "Trung Quốc",
      "unit": "Cái",
      "code": "P1",
      "materialUnitPrice": 120000,
      "laborUnitPrice": 30000,
      "lossPercent": 5
    }
  ]
  ```

#### `POST /api/v1/products`
* Xây dựng API tạo mới sản phẩm (Tương tự format Request của Response GET).

#### `PUT /api/v1/products/{id}`
* Cập nhật thông tin vật tư.

#### `DELETE /api/v1/products/{id}`
* Xóa vật tư by ID.

---

### 4.3. Quản lý danh sách Công trình (Constructions)

#### `GET /api/v1/constructions`
* Màn hình danh sách công trình (hiển thị tóm tắt, không load chi tiết row/section).
* **Response `200 OK`**:
  ```json
  [
    {
      "id": "c1",
      "name": "Nhà thuốc A",
      "createdAt": "2023-10-01T10:00:00Z",
      "updatedAt": "2023-10-01T10:00:00Z"
    }
  ]
  ```

#### `POST /api/v1/constructions`
* Khởi tạo công trình mới
* **Request**:
  ```json
  { "name": "Dự án B" }
  ```

#### `PUT /api/v1/constructions/{id}` / `DELETE /api/v1/constructions/{id}`
* Đổi tên hoặc Xóa công trình.

---

### 4.4. API Chi tiết Dự toán Công trình (Construction Details)
Đây là API phức tạp nhất, nên gom nhóm vào một cấu trúc JSON JSON (Tree) để dễ dàng đồng bộ từ Frontend.

#### `GET /api/v1/constructions/{id}/details`
* Tải toàn bộ cấu trúc chi tiết để điền vào màn hình `ConstructionDetailPage`.
* **Response `200 OK`**:
  ```json
  {
    "id": "c1",
    "projectName": "Nhà thuốc",
    "materialPercent": 100,
    "laborPercent": 100,
    "workshops": [
      { "id": "w1", "name": "Xưởng 1" },
      { "id": "w2", "name": "Xưởng 2" }
    ],
    "sections": [
      {
        "id": "sec1",
        "name": "Phần chữa cháy",
        "rows": [
          {
            "id": "row1",
            "productId": "p1",
            "code": "P1",
            "note": "Ghi chú thêm",
            "totalCable": 10.5,
            "lossPercent": 5,
            "materialPrice": 120000,
            "laborPrice": 30000,
            "workshopValues": {
              "w1": 15,
              "w2": 20
            }
          }
        ]
      }
    ]
  }
  ```

#### `PUT /api/v1/constructions/{id}/details`
* API dùng để "Lưu" (Save) bảng dữ liệu. Nó sẽ cập nhật lại toàn bộ `percent`, `workshops`, `sections`, và `rows` của chi tiết bảng khối lượng. Tại backend, logic chuẩn nhất là có thể Drop & Re-insert các entities con (`sections`, `rows`, `workshops`) hoặc sync thông minh (Upsert).
* **Request**:
  Gửi vào JSON Body cấu trúc tương tự API `GET` để backend phân tích và lưu.
  ```json
  {
    "projectName": "Nhà thuốc (Đã sửa)",
    "materialPercent": 105,
    "laborPercent": 100,
    "workshops": [
       ...
    ],
    "sections": [
       ...
    ]
  }
  ```
* **Response `200 OK`**: 
  ```json
  {
    "message": "Saved successfully",
    "status": 200
  }
  ``` 

## 4. Ghi chú cho lập trình viên Backend (Java Spring Boot)
1. Hãy dùng JPA / Hibernate cùng với các cấu hình cascading như `CascadeType.ALL` (`orphanRemoval = true`) cho mối quan hệ từ `Construction` -> `ConstructionSection` -> `ConstructionRow` để dễ dàng update toàn bộ bảng khi gọi `PUT /details` mà không gặp rác dữ liệu.
2. Với `workshopValues` (đang map key-value từ React state) bạn có thể custom map thành List Entity `ConstructionRowWorkshopValue` trong payload hoặc dùng tính năng `@ElementCollection` của Hibernate map trực tiếp ra bảng.
3. Để hiệu năng tốt, bạn có thể thiết kế `@EntityGraph` khi GET chi tiết công trình tránh N+1 Query.
