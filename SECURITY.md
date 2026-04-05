# Security Documentation

## Tổng quan về Hệ thống Authentication

Hệ thống authentication của PCCC Backend sử dụng JWT (JSON Web Tokens) để xác thực người dùng. Hệ thống hỗ trợ ba vai trò chính: USER, EDITOR và ADMIN.

### Các Thành Phần Chính

- **JWT Authentication**: Stateless authentication sử dụng JWT tokens
- **Spring Security**: Framework bảo mật chính
- **BCrypt Password Encoder**: Mã hóa mật khẩu
- **Role-based Access Control**: Phân quyền dựa trên vai trò

## Vai Trò Người Dùng

### ROLE_USER
- Vai trò mặc định cho người dùng đăng ký tự do
- Chỉ được phép xem dữ liệu và truy cập các API đọc

### ROLE_EDITOR
- Vai trò dành cho người chỉnh sửa
- Được phép xem và chỉnh sửa dữ liệu (create/update/delete tài nguyên chức năng)
- Không có quyền quản lý tài khoản người dùng

### ROLE_ADMIN
- Vai trò quản trị viên
- Có thể tạo và quản lý người dùng khác
- Có toàn quyền truy cập tất cả các API
- Được tạo tự động khi khởi động ứng dụng lần đầu

## API Endpoints Authentication

### 1. Đăng Nhập (Login)

**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "string",
  "email": "string"
}
```

**Status Codes:**
- `200 OK`: Đăng nhập thành công
- `401 Unauthorized`: Sai thông tin đăng nhập

### 2. Đăng Ký (Register)

**Endpoint:** `POST /api/v1/auth/register`

**Request Body:**
```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "string",
  "email": "string"
}
```

**Status Codes:**
- `200 OK`: Đăng ký thành công
- `400 Bad Request`: Username hoặc email đã tồn tại

**Lưu ý:** Người dùng đăng ký tự do sẽ có vai trò ROLE_USER.

## User Profile API

### Lấy Thông Tin Cá Nhân

**Endpoint:** `GET /api/v1/users/me`

**Header:**
```
Authorization: Bearer <your_jwt_token>
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User",
  "role": "ROLE_USER",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Status Codes:**
- `200 OK`: Lấy thông tin thành công
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn

### Cập Nhật Thông Tin Cá Nhân

**Endpoint:** `PUT /api/v1/users/me`

**Header:**
```
Authorization: Bearer <your_jwt_token>
```

**Request Body:**
```json
{
  "firstName": "NewFirstName",
  "lastName": "NewLastName",
  "email": "newemail@example.com"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "email": "newemail@example.com",
  "firstName": "NewFirstName",
  "lastName": "NewLastName",
  "role": "ROLE_USER",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T11:45:30"
}
```

**Status Codes:**
- `200 OK`: Cập nhật thông tin thành công
- `400 Bad Request`: Email đã được sử dụng bởi user khác
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn

**Lưu ý:** 
- User có thể cập nhật firstName, lastName và email
- Username, role không thể thay đổi thông qua endpoint này
- Email phải chưa được sử dụng bởi user khác

### Đổi Mật Khẩu

**Endpoint:** `PUT /api/v1/users/me/password`

**Header:**
```
Authorization: Bearer <your_jwt_token>
```

**Request Body:**
```json
{
  "oldPassword": "currentPassword123",
  "newPassword": "newSecurePassword456"
}
```

**Response:** 200 OK (no body)

**Status Codes:**
- `200 OK`: Đổi mật khẩu thành công
- `400 Bad Request`: Mật khẩu cũ không đúng
- `401 Unauthorized`: Token không hợp lệ hoặc hết hạn

**Lưu ý:**
- Phải cung cấp mật khẩu cũ chính xác để đổi mật khẩu
- Mật khẩu mới phải khác mật khẩu cũ

### Reset Mật Khẩu (Admin Only)

**Endpoint:** `PUT /api/v1/admin/users/{id}/reset-password`

**Header:**
```
Authorization: Bearer <admin_jwt_token>
```

**Request Parameters:**
- `id`: UUID của user cần reset mật khẩu

**Request Body:**
```json
{
  "newPassword": "newTemporaryPassword789"
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "testuser",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User",
  "role": "ROLE_USER",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T11:45:30"
}
```

**Status Codes:**
- `200 OK`: Reset mật khẩu thành công
- `404 Not Found`: User không tồn tại
- `401 Unauthorized`: Không phải admin hoặc token hết hạn

**Lưu ý:**
- Chỉ admin mới có quyền reset mật khẩu cho user khác
- Được sử dụng khi user quên mật khẩu hoặc cần cấp lại quyền truy cập

## Sử dụng JWT Token

### Gửi Token trong Request

Để truy cập các API được bảo vệ, gửi JWT token trong header Authorization:

```
Authorization: Bearer <your_jwt_token>
```

### Ví dụ Request với Token

```bash
curl -X GET "http://localhost:8081/api/v1/protected-endpoint" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

## Cấu Hình Bảo Mật

### Endpoints Công Khai
- `/api/v1/auth/**`: Tất cả endpoints authentication không yêu cầu token

### Endpoints Bảo Vệ
- Tất cả endpoints khác yêu cầu JWT token hợp lệ

### CORS Configuration
- Cho phép tất cả origins: `*`
- Methods: GET, POST, PUT, DELETE, OPTIONS
- Headers: `*`
- Credentials: false

## JWT Configuration

### Secret Key
- Được cấu hình trong `application.yml`
- Secret: `mySecretKeyForJWTTokenGenerationAndValidationPurposesOnlyForDevelopmentEnvironment12345678901234567890`

### Token Expiration
- Thời gian sống: 24 giờ (86400000 milliseconds)
- Token sẽ hết hạn sau thời gian này

## Admin User

### Khởi Tạo Admin Mặc Định

Khi ứng dụng khởi động lần đầu và database trống, hệ thống sẽ tự động tạo tài khoản admin:

- **Username:** admin (có thể cấu hình qua `app.admin.username`)
- **Password:** admin123 (có thể cấu hình qua `app.admin.password`)
- **Email:** admin@example.com (có thể cấu hình qua `app.admin.email`)

### Cấu Hình Admin

Có thể thay đổi thông tin admin mặc định trong `application.yml`:

```yaml
app:
  admin:
    username: your_admin_username
    password: your_admin_password
    email: your_admin_email
```

## Bảo Mật Mật Khẩu

- Sử dụng BCrypt để mã hóa mật khẩu
- Mật khẩu được lưu trữ dưới dạng hash, không thể giải mã ngược

## Xử Lý Lỗi Authentication

### Các Lỗi Thường Gặp

1. **Token Hết Hạn**
   - Lỗi: "JWT token is expired"
   - Giải pháp: Đăng nhập lại để lấy token mới

2. **Token Không Hợp Lệ**
   - Lỗi: "Invalid JWT token"
   - Giải pháp: Kiểm tra token được gửi

3. **Không Có Quyền Truy Cập**
   - Status: 401 Unauthorized
   - Nguyên nhân: Thiếu token hoặc token không hợp lệ

## Ví dụ Sử dụng

### 1. Đăng Ký Người Dùng Mới

```bash
curl -X POST "http://localhost:8081/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### 2. Đăng Nhập

```bash
curl -X POST "http://localhost:8081/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### 3. Lấy Thông Tin Cá Nhân

```bash
curl -X GET "http://localhost:8081/api/v1/users/me" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 4. Cập Nhật Thông Tin Cá Nhân

```bash
curl -X PUT "http://localhost:8081/api/v1/users/me" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "firstName": "NewFirstName",
    "lastName": "NewLastName",
    "email": "newemail@example.com"
  }'
```

### 5. Đổi Mật Khẩu

```bash
curl -X PUT "http://localhost:8081/api/v1/users/me/password" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "oldPassword": "currentPassword123",
    "newPassword": "newSecurePassword456"
  }'
```

### 6. Reset Mật Khẩu User (Admin Only)

```bash
curl -X PUT "http://localhost:8081/api/v1/admin/users/{user_id}/reset-password" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN_HERE" \
  -d '{
    "newPassword": "newTemporaryPassword789"
  }'
```

### 7. Truy Cập API Bảo Vệ

```bash
curl -X GET "http://localhost:8081/api/v1/some-protected-endpoint" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Lưu Ý Bảo Mật

1. **Không Chia Sẻ Token**: JWT token chứa thông tin xác thực, không chia sẻ với người khác
2. **Sử dụng HTTPS**: Trong môi trường production, luôn sử dụng HTTPS
3. **Thay Đổi Secret Key**: Thay đổi JWT secret key trong môi trường production
4. **Giới Hạn Thời Gian Token**: Cân nhắc giảm thời gian expiration cho token
5. **Validate Token**: Luôn validate token ở phía server

## Troubleshooting

### Không Thể Đăng Nhập
- Kiểm tra username và password
- Đảm bảo tài khoản đã được kích hoạt

### Token Bị Từ Chối
- Kiểm tra token chưa hết hạn
- Kiểm tra format header Authorization đúng: `Bearer <token>`

### CORS Errors
- Đảm bảo frontend gửi requests từ origins được phép
- Kiểm tra cấu hình CORS trong WebSecurityConfig</content>
<parameter name="filePath">e:\github_dungicloudhz\pccc-be\SECURITY.md