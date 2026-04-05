# CORS Configuration Guide

## Giới thiệu CORS

**CORS** (Cross-Origin Resource Sharing) là một cơ chế bảo mật của trình duyệt cho phép requests từ các domain khác nhau.

Ví dụ:
- Frontend chạy tại: `http://localhost:3000`
- Backend chạy tại: `http://localhost:8080`

Nếu không cấu hình CORS, trình duyệt sẽ chặn request từ frontend tới backend.

---

## Cấu hình CORS hiện tại

File: `src/main/java/com/cozyquoteforge/pccc/config/CorsConfig.java`

### Cấu hình mặc định (Development)

```java
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:3000", "http://localhost:3001", "http://127.0.0.1:3000")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
```

### Giải thích các tham số:

| Tham số | Giá trị | Ý nghĩa |
|--------|--------|--------|
| `addMapping` | `/api/**` | Áp dụng CORS cho tất cả endpoints bắt đầu với `/api/` |
| `allowedOrigins` | `http://localhost:3000` | Cho phép requests từ domain này |
| `allowedMethods` | GET, POST, PUT, DELETE | Cho phép các HTTP methods này |
| `allowedHeaders` | `*` | Cho phép tất cả headers |
| `allowCredentials` | `true` | Cho phép gửi cookies/credentials |
| `maxAge` | `3600` | Cache pre-flight request trong 3600 giây (1 giờ) |

---

## Các scenario cấu hình khác

### Scenario 1: Development (Cho phép mọi origins)

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("*")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
}
```

**Lưu ý**: Chỉ dùng cho development, không an toàn cho production!

---

### Scenario 2: Production (Specific domains)

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins(
                "https://myapp.com",
                "https://www.myapp.com",
                "https://admin.myapp.com"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Content-Type", "Authorization")
            .allowCredentials(true)
            .maxAge(86400); // 24 giờ
}
```

---

### Scenario 3: Sử dụng Environment Variables

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.split(","))
                .allowedMethods(allowedMethods.split(","))
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(maxAge);
    }
}
```

Rồi trong `application.yml`:

```yaml
cors:
  allowed-origins: "http://localhost:3000,http://localhost:3001"
  allowed-methods: "GET,POST,PUT,DELETE,OPTIONS"
  max-age: 3600
```

---

### Scenario 4: Khác nhau theo Environment

**application.yml** (mặc định):
```yaml
cors:
  allowed-origins: "https://myapp.com"
  allowed-methods: "GET,POST,PUT,DELETE"
  max-age: 86400
```

**application-dev.yml** (development):
```yaml
cors:
  allowed-origins: "http://localhost:3000,http://127.0.0.1:3000"
  allowed-methods: "GET,POST,PUT,DELETE,OPTIONS,PATCH"
  max-age: 3600
```

---

## Cấu hình chi tiết theo từng endpoint

### Multiple endpoints với cấu hình khác nhau

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // CORS cho /api/public/** - cho phép tất cả
        registry.addMapping("/api/public/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET")
                .maxAge(3600);

        // CORS cho /api/private/** - chi cho phép authenticated users
        registry.addMapping("/api/private/**")
                .allowedOrigins("https://myapp.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true)
                .maxAge(86400);

        // CORS cho /api/v1/** - mặc định
        registry.addMapping("/api/v1/**")
                .allowedOrigins("http://localhost:3000", "https://myapp.com")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## Using application.yml

Thêm vào `application.yml`:

```yaml
# CORS Configuration
cors:
  allowed-origins: "http://localhost:3000,http://localhost:3001"
  allowed-methods: "GET,POST,PUT,DELETE,OPTIONS,PATCH"
  allowed-headers: "Content-Type,Authorization"
  allow-credentials: true
  max-age: 3600

# Hoặc cấu hình trực tiếp
spring:
  web:
    cors:
      allowed-origins: "http://localhost:3000"
      allowed-methods: "GET,POST,PUT,DELETE"
      allowed-headers: "*"
      max-age: 3600
```

Sau đó đọc từ properties:

```java
@Value("${spring.web.cors.allowed-origins}")
private String allowedOrigins;
```

---

## Test CORS

### 1. Test với curl

```bash
# Pre-flight request (OPTIONS)
curl -X OPTIONS http://localhost:8080/api/v1/part-types \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -v
```

Kiểm tra response headers:
```
Access-Control-Allow-Origin: http://localhost:3000
Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS
Access-Control-Allow-Headers: *
Access-Control-Max-Age: 3600
```

### 2. Test với Browser DevTools

```javascript
// Mở browser console và chạy:
fetch('http://localhost:8080/api/v1/part-types', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

Nếu CORS không được cấu hình, sẽ thấy lỗi:
```
Access to XMLHttpRequest at 'http://localhost:8080/api/v1/part-types'
from origin 'http://localhost:3000' has been blocked by CORS policy
```

### 3. Test với JavaScript Fetch API

```javascript
const apiUrl = 'http://localhost:8080/api/v1/part-types';

fetch(apiUrl, {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  },
  credentials: 'include' // Nếu allowCredentials: true
})
.then(res => res.json())
.then(data => console.log('Success:', data))
.catch(err => console.error('Error:', err));
```

---

## Troubleshooting CORS

### 1. Lỗi: "Access to XMLHttpRequest ... has been blocked by CORS policy"

**Giải pháp:**
- Kiểm tra `allowedOrigins` có chứa domain của frontend không
- Kiểm tra `allowedMethods` có chứa HTTP method mà frontend dùng không
- Restart backend

### 2. Lỗi: "The value of the 'Access-Control-Allow-Credentials' header ... cannot be set to '*'"

**Giải pháp:**
Nếu `allowCredentials(true)`, không thể dùng wildcard `*`. Phải specify origins:

```java
.allowedOrigins("http://localhost:3000") // ✅ Đúng
// .allowedOriginPatterns("*")  // ❌ Sai nếu allowCredentials(true)
.allowCredentials(true)
```

### 3. Pre-flight request không được gửi

**Nguyên nhân**: CORS pre-flight request (`OPTIONS`) bị block

**Kiểm tra**:
```bash
curl -X OPTIONS http://localhost:8080/api/v1/part-types -v
```

**Giải pháp**: Đảm bảo `OPTIONS` method được cho phép:

```java
.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
```

---

## Best Practices

### ✅ Do's

1. **Development**: Cho phép localhost + các ports phổ biến
   ```java
   .allowedOrigins(
       "http://localhost:3000",
       "http://localhost:3001",
       "http://127.0.0.1:3000"
   )
   ```

2. **Production**: Chỉ cho phép specific domains
   ```java
   .allowedOrigins(
       "https://myapp.com",
       "https://www.myapp.com"
   )
   ```

3. **Sử dụng HTTPS** trong production

4. **Specify headers** cần thiết thay vì `*`
   ```java
   .allowedHeaders("Content-Type", "Authorization")
   ```

5. **Đặt maxAge hợp lý**
   - Development: 1 giờ = 3600
   - Production: 24 giờ = 86400

### ❌ Don'ts

1. **Không dùng `*` cho production**
   ```java
   .allowedOrigins("*") // ❌ Không an toàn
   ```

2. **Không cho phép tất cả methods nếu không cần**
   ```java
   .allowedMethods("*") // ❌ Có thể cho phép PATCH, DELETE không cần thiết
   ```

3. **Không expose sensitive headers**
   ```java
   .exposedHeaders("Authorization", "X-Custom-Token") // ⚠️ Cẩn thận
   ```

---

## Cấu hình cho Frontend (React/Vue/Angular)

### React (sử dụng fetch)

```javascript
const API_BASE = 'http://localhost:8080';

export const fetchPartTypes = async () => {
  const response = await fetch(`${API_BASE}/api/v1/part-types`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include' // Nếu backend có allowCredentials(true)
  });
  return response.json();
};
```

### Axios (nếu dùng)

```javascript
import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true // Nếu backend có allowCredentials(true)
});

export default axiosInstance;
```

---

## Summary

| Scenario | Cấu hình |
|----------|---------|
| **Local Development** | `localhost:3000, localhost:3001` |
| **Production** | Specific domains chỉ `https` |
| **Wildcard** | Chỉ dùng trong development |
| **Credentials** | `allowCredentials(true) + specify origins` |
| **Methods** | Specify những methods cần thiết |
| **Headers** | Jô specify nếu không cần wildcard |

---

**Version**: 1.0
**Last Updated**: 2024
