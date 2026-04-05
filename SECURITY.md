# Security Documentation

## Tổng quan về Hệ thống Authentication

Hệ thống authentication của PCCC Backend sử dụng JWT (JSON Web Tokens) để xác thực người dùng. Hệ thống hỗ trợ hai vai trò chính: USER và ADMIN.

### Các Thành Phần Chính

- **JWT Authentication**: Stateless authentication sử dụng JWT tokens
- **Spring Security**: Framework bảo mật chính
- **BCrypt Password Encoder**: Mã hóa mật khẩu
- **Role-based Access Control**: Phân quyền dựa trên vai trò

## Vai Trò Người Dùng

### ROLE_USER
- Vai trò mặc định cho người dùng đăng ký tự do
- Có thể truy cập các API được bảo vệ sau khi xác thực

### ROLE_ADMIN
- Vai trò quản trị viên
- Có thể tạo và quản lý người dùng khác
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

### 3. Truy Cập API Bảo Vệ

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